package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.response.LikeQuestionnaireResponse
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LikeQuestionnaireUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(likedQuestionnaireId: String): Result<LikeQuestionnaireResponse> {
        return runCatching {

            val user = userDataRepository.user.first()

            usersRepository.likeQuestionnaire(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
                likedQuestionnaireId = likedQuestionnaireId
            ).getOrThrow()
        }
    }
}
