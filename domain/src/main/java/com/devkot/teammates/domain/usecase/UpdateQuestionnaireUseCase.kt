package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.enums.Games
import com.devkot.teammates.domain.repository.QuestionnairesRepository
import com.devkot.teammates.domain.repository.UserDataRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UpdateQuestionnaireUseCase @Inject constructor(
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        header: String,
        description: String,
        selectedGame: Games,
        questionnaireId: String,
        image: MultipartBody.Part?
    ): Result<Questionnaire> {
        return runCatching {

            val user = userDataRepository.user()

            questionnairesRepository.updateQuestionnaire(
                header = header,
                game = selectedGame,
                description = description,
                authorId = user.publicId,
                questionnaireId = questionnaireId,
                image = image,
            ).getOrThrow()
        }
    }
}
