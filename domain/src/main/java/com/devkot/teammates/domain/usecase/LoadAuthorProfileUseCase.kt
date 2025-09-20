package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.model.requesrt.LoadAuthorRequest
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import javax.inject.Inject

class LoadAuthorProfileUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(authorId: String): Result<User> {
        return runCatching {

            val user = userDataRepository.user()

            usersRepository.loadAuthorProfile(
                userId = user.publicId,
                request = LoadAuthorRequest(authorId = authorId)
            ).getOrThrow()
        }
    }
}