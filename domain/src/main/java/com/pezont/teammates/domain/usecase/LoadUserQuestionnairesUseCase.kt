package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoadUserQuestionnairesUseCase @Inject constructor(
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(limit: Int = 100, game: Games?): Result<List<Questionnaire>> {
        return runCatching {

            val user = userDataRepository.user.first()

            questionnairesRepository.loadQuestionnaires(
                token = userDataRepository.accessToken.first(),
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
