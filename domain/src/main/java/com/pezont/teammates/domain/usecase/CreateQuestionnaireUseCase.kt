package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.ValidationError
import com.pezont.teammates.domain.model.ValidationResult
import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import javax.inject.Inject

class CreateQuestionnaireUseCase @Inject constructor(
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        header: String,
        description: String,
        selectedGame: Games,
        image: MultipartBody.Part?
    ): Result<Questionnaire> {
        return runCatching {

            val user = userDataRepository.user.first()

            questionnairesRepository.createQuestionnaire(
                token = userDataRepository.accessToken.first(),
                header = header,
                game = selectedGame,
                description = description,
                authorId = user.publicId,
                image = image,
            ).getOrThrow()
        }
    }

    //TODO potential problems
    fun validateQuestionnaireForm(
        header: String,
        description: String,
        selectedGame: Games?
    ): ValidationResult {
        return when {
            header.length < 3 -> ValidationResult.Error(ValidationError.HEADER_TOO_SHORT)
            header.length > 63 -> ValidationResult.Error(ValidationError.HEADER_TOO_LONG)
            description.length > 2000 -> ValidationResult.Error(ValidationError.DESCRIPTION_TOO_LONG)
            selectedGame == null -> ValidationResult.Error(ValidationError.GAME_NOT_SELECTED)
            else -> ValidationResult.Success
        }
    }
}
