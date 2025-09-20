package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.model.ValidationError
import com.devkot.teammates.domain.model.ValidationResult
import com.devkot.teammates.domain.model.requesrt.UpdateUserProfileRequest
import com.devkot.teammates.domain.model.response.UpdateUserProfilePhotoResponse
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(nickname: String, description: String): Result<User> {
        return runCatching {

            val user = userDataRepository.user()

            val response = usersRepository.updateUserProfile(
                token = userDataRepository.accessToken(),
                userId = user.publicId,
                UpdateUserProfileRequest(
                    nickname = nickname,
                    publicId = user.publicId,
                    description = description,
                )
            ).getOrThrow()

            userDataRepository.updateUserProfile(
                response.nickname,
                response.description,
                response.imagePath
            )

            response

        }
    }

    //TODO validate
    fun validateUserProfileForm(
        nickname: String,
        description: String?,
    ): ValidationResult {
        return when {
            nickname.length < 3 -> ValidationResult.Error(ValidationError.HEADER_TOO_SHORT)
            nickname.length > 20 -> ValidationResult.Error(ValidationError.HEADER_TOO_LONG)
            description != null && description.length > 2000 -> ValidationResult.Error(
                ValidationError.DESCRIPTION_TOO_LONG
            )

            else -> ValidationResult.Success
        }
    }


    suspend fun updateUserAvatar(image: MultipartBody.Part): Result<UpdateUserProfilePhotoResponse> {
        return runCatching {

            val user = userDataRepository.user()

            val response = usersRepository.updateUserProfilePhoto(
                token = userDataRepository.accessToken(),
                userId = user.publicId,
                image = image
            ).getOrThrow()

            userDataRepository.updateUserProfile(
                imagePath = response.imagePath
            )
            response
        }
    }
}