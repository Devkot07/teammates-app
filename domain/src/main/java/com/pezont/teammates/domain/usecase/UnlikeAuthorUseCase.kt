package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.LikeUserResponse
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UnlikeAuthorUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(unlikedUserId: String): Result<LikeUserResponse> {
        return runCatching {

            val user = userDataRepository.user.first()

            usersRepository.unlikeUser(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
                unlikedUserId = unlikedUserId
            ).getOrThrow()
        }
    }
}
