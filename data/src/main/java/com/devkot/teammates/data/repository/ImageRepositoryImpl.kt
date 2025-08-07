package com.devkot.teammates.data.repository

import android.content.Context
import android.graphics.Bitmap
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.devkot.teammates.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class ImageRepositoryImpl (
    private val context: Context,
    private val imageLoader: ImageLoader,
) : ImageRepository {

    companion object {
        private const val CACHE_DURATION_MS = 3 * 60 * 60 * 1000L
    }

    private val cacheDir = File(context.cacheDir, "images").apply {
        if (!exists()) mkdirs()
    }

    override suspend fun getImage(url: String, loadFromCache: Boolean, saveToCache: Boolean): String = withContext(Dispatchers.IO) {
        val fileName = url.hashCode().toString() + ".webp"
        val cacheFile = File(cacheDir, fileName)

        if (loadFromCache && cacheFile.exists() && isCacheValid(cacheFile)) {
            return@withContext cacheFile.absolutePath
        }

        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()

            val result = imageLoader.execute(request)
            if (saveToCache && result is SuccessResult) {
                val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                bitmap?.let {
                    saveBitmapToCache(it, cacheFile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext if (cacheFile.exists()) cacheFile.absolutePath else url
    }

    override suspend fun updateImageCache(url: String): String = withContext(Dispatchers.IO) {
        val fileName = url.hashCode().toString() + ".webp"
        val cacheFile = File(cacheDir, fileName)

        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                bitmap?.let {
                    if (cacheFile.exists()) {
                        cacheFile.delete()
                    }
                    saveBitmapToCache(it, cacheFile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext if (cacheFile.exists()) cacheFile.absolutePath else url
    }

    private fun isCacheValid(file: File): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - file.lastModified()) < CACHE_DURATION_MS
    }

    private fun saveBitmapToCache(bitmap: Bitmap, file: File) {
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}