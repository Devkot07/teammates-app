package com.devkot.teammates.domain.repository

interface ImageRepository {
    suspend fun getImage(url: String, loadFromCache: Boolean = true, saveToCache: Boolean = true): String
    suspend fun updateImageCache(url: String): String
}