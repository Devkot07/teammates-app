package com.pezont.teammates.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageScope
import com.pezont.teammates.domain.usecase.ImageUseCase
import com.pezont.teammates.viewmodel.ImageViewModel

@Composable
fun TeammatesImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    loadFromCache: Boolean = true,
    saveToCache: Boolean = true,
    loading: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Error) -> Unit)? = {
        Icon(
            imageVector = Icons.Default.BrokenImage,
            contentDescription = null
        )
    },
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    imageUseCase: ImageUseCase = hiltViewModel<ImageViewModel>().imageUseCase
) {

    val processedModel by produceState<Any?>(initialValue = null, key1 = model) {
        value = when (model) {
            is String -> when {
                model.startsWith("http") -> try {
                    imageUseCase.getImage(
                        url = model,
                        loadFromCache = loadFromCache,
                        saveToCache = saveToCache
                    )
                } catch (_: Exception) {
                    model
                }

                else -> model
            }

            else -> model
        }
    }
    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        if (processedModel != null) {
            SubcomposeAsyncImage(
                model = processedModel,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                loading = loading,
                success = success,
                error = error,
                onLoading = onLoading,
                onSuccess = onSuccess,
                onError = onError,
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality
            )
        } else {
            LoadingItem()
        }
    }

}