package com.pezont.teammates.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.ValidationError
import com.pezont.teammates.domain.model.ValidationResult
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
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

    //TODO extract
    fun uriToSquareCroppedWebpMultipart(
        uri: Uri,
        context: Context,
        name: String = "image"
    ): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null

        val options = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
        val originalBitmap = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        if (originalBitmap == null) return null

        val dimension = minOf(originalBitmap.width, originalBitmap.height)
        val xOffset = (originalBitmap.width - dimension) / 2
        val yOffset = (originalBitmap.height - dimension) / 2
        val squareBitmap =
            Bitmap.createBitmap(originalBitmap, xOffset, yOffset, dimension, dimension)

        val byteArrayOutputStream = ByteArrayOutputStream()
        squareBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 95, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val mediaType = "image/webp".toMediaTypeOrNull()
        val requestBody = byteArray.toRequestBody(mediaType)
        return MultipartBody.Part.createFormData(name, "$name.webp", requestBody)
    }

}
