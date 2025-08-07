package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.repository.ImageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend fun getImage(url: String, loadFromCache: Boolean = true, saveToCache: Boolean = true): String {
        return imageRepository.getImage(url, loadFromCache, saveToCache)
    }

    suspend fun updateImageCache(url: String): String {
        return imageRepository.updateImageCache(url)
    }
}
