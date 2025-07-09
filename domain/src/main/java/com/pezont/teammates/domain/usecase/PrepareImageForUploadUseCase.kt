package com.pezont.teammates.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class PrepareImageForUploadUseCase @Inject constructor(){

    operator fun invoke(
        uri: Uri?,
        context: Context,
        name: String = "image"
    ): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val inputStream = uri?.let { contentResolver.openInputStream(it) } ?: return null

        val options = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
        val originalBitmap = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        if (originalBitmap == null) return null

        val correctedBitmap = correctImageOrientation(uri, originalBitmap, context)

        val dimension = minOf(correctedBitmap.width, correctedBitmap.height)
        val xOffset = (correctedBitmap.width - dimension) / 2
        val yOffset = (correctedBitmap.height - dimension) / 2
        val squareBitmap = Bitmap.createBitmap(correctedBitmap, xOffset, yOffset, dimension, dimension)

        val byteArrayOutputStream = ByteArrayOutputStream()
        squareBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 95, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val mediaType = "image/webp".toMediaTypeOrNull()
        val requestBody = byteArray.toRequestBody(mediaType)
        return MultipartBody.Part.createFormData(name, "$name.webp", requestBody)
    }

    private fun correctImageOrientation(uri: Uri, bitmap: Bitmap, context: Context): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exifInterface = inputStream?.let { ExifInterface(it) }
            inputStream?.close()

            val orientation = exifInterface?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL

            val rotationDegrees = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

            if (rotationDegrees == 0f) {
                bitmap
            } else {
                val matrix = Matrix().apply { postRotate(rotationDegrees) }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
        } catch (e: Exception) {
            bitmap
        }
    }
}