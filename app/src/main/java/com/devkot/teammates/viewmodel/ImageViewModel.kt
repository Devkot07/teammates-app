package com.devkot.teammates.viewmodel

import androidx.lifecycle.ViewModel
import com.devkot.teammates.domain.usecase.ImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ImageViewModel @Inject constructor(val imageUseCase: ImageUseCase) : ViewModel()