package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.repository.UsersRepository
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
