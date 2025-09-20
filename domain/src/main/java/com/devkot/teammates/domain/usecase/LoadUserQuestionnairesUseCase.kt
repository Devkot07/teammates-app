package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.enums.Games
import com.devkot.teammates.domain.repository.QuestionnairesRepository
import com.devkot.teammates.domain.repository.UserDataRepository
import javax.inject.Inject

class LoadUserQuestionnairesUseCase @Inject constructor(
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        limit: Int = 100,
        game: Games?
    ): Result<Pair<List<Questionnaire>, Throwable?>> {
        return runCatching {

            val user = userDataRepository.user()

            questionnairesRepository.loadQuestionnaires(
                token = userDataRepository.accessToken(),
                userId = user.publicId,
                page = null,
                limit = limit,
                game = game,
                authorId = user.publicId,
                questionnaireId = null
            ).getOrThrow()
        }
    }
}
