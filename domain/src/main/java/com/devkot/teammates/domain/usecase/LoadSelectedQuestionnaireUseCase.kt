package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.repository.QuestionnairesRepository
import com.devkot.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadSelectedQuestionnaireUseCase @Inject constructor(
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(questionnaireId: String): Result<Pair<List<Questionnaire>, Throwable?>> {
        return runCatching {

            val user = userDataRepository.user.first()

            questionnairesRepository.loadQuestionnaires(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
                page = null,
                limit = null,
                game = null,
                authorId = null,
                questionnaireId = questionnaireId
            ).getOrThrow()
        }
    }
}
