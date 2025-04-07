package com.pezont.teammates.domain.usecase

import android.content.Context
import android.net.Uri
import com.pezont.teammates.domain.model.Games
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class CreateQuestionnaireUseCase @Inject constructor(
    private val questionnairesRepository: QuestionnairesRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(header: String, description: String, selectedGame: Games, image: MultipartBody.Part?): Result<Questionnaire> {
        return runCatching {

            val user = userDataRepository.user.first()
            if (user.publicId == null) throw Exception("User not authenticated")

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

    fun uriToMultipart(uri: Uri, context: Context, name: String = "image"): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val requestBody = inputStream.readBytes()
            .toRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
        inputStream.close()
        return MultipartBody.Part.createFormData(name, uri.lastPathSegment, requestBody)
    }
}
