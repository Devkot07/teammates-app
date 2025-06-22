package com.pezont.teammates.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class PrepareImageForUploadUseCase @Inject constructor(){

    operator fun invoke(
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