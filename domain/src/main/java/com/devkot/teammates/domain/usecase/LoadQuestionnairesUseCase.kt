package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.enums.Games
import com.devkot.teammates.domain.repository.QuestionnairesRepository
import com.devkot.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadQuestionnairesUseCase @Inject constructor(
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 10,
        game: Games?,
        authorId: String?
    ): Result<List<Questionnaire>> {
        return runCatching {

            val user = userDataRepository.user.first()

            questionnairesRepository.loadQuestionnaires(
                token = userDataRepository.accessToken.first(),
                userId = user.publicId,
                page = page,
                limit = limit,
                game = game,
                authorId = authorId,
                questionnaireId = null
            ).getOrThrow()
        }
    }
}
