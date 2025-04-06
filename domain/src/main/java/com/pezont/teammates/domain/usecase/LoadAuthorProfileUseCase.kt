package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.LoadAuthorRequest
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadAuthorProfileUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(authorId: String): Result<User> {
        return runCatching {

            val user = userDataRepository.user.first()
            if (user.publicId == null) throw Exception("User not authenticated")

            usersRepository.loadAuthorProfile(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
                LoadAuthorRequest(authorId = authorId)
            ).getOrThrow()
        }
    }
}