package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.response.LikeUserResponse
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import javax.inject.Inject

class LikeAuthorUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(likedUserId: String): Result<LikeUserResponse> {
        return runCatching {

            val user = userDataRepository.user()

            usersRepository.likeUser(
                userId = user.publicId,
                likedUserId = likedUserId
            ).getOrThrow()
        }
    }
}
