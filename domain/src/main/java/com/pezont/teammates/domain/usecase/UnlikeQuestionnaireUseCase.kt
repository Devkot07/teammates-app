package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.LikeQuestionnaireResponse
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UnlikeQuestionnaireUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(unlikedQuestionnaireId: String): Result<LikeQuestionnaireResponse> {
        return runCatching {

            val user = userDataRepository.user.first()

            usersRepository.unlikeQuestionnaire(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
                unlikedQuestionnaireId = unlikedQuestionnaireId
            ).getOrThrow()
        }
    }
}
