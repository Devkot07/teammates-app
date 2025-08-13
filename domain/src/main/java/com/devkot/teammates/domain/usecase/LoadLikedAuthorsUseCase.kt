package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadLikedAuthorsUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return runCatching {

            val user = userDataRepository.user.first()

            usersRepository.loadLikedUsers(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
            ).getOrThrow()
        }
    }
}
