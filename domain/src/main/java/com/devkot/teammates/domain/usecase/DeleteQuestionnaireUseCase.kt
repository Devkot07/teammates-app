package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.repository.QuestionnairesRepository
import com.devkot.teammates.domain.repository.UserDataRepository
import javax.inject.Inject

class DeleteQuestionnaireUseCase @Inject constructor(
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        questionnaireId: String,
    ): Result<Unit> {
        return runCatching {

            val user = userDataRepository.user()

            questionnairesRepository.deleteQuestionnaires(
                userId = user.publicId,
                questionnaireId = questionnaireId,

            ).getOrThrow()
        }
    }
}
