package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.response.LikeUserResponse
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LikeAuthorUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(likedUserId: String): Result<LikeUserResponse> {
        return runCatching {

            val user = userDataRepository.user.first()

            usersRepository.likeUser(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
                likedUserId = likedUserId
            ).getOrThrow()
        }
    }
}
