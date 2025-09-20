package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.response.LikeQuestionnaireResponse
import com.devkot.teammates.domain.repository.UserDataRepository
import com.devkot.teammates.domain.repository.UsersRepository
import javax.inject.Inject

class LikeQuestionnaireUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(questionnaire: Questionnaire): Result<LikeQuestionnaireResponse> {
        return runCatching {

            val user = userDataRepository.user()

            usersRepository.likeQuestionnaire(
                userId = user.publicId,
                questionnaire = questionnaire
            ).getOrThrow()
        }
    }
}
