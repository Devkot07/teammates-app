package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.requesrt.LoadAuthorRequest
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadAuthorProfileUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(authorId: String): Result<User> {
        return runCatching {

            val user = userDataRepository.user.first()

            usersRepository.loadAuthorProfile(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
                LoadAuthorRequest(authorId = authorId)
            ).getOrThrow()
        }
    }
}