package com.devkot.teammates.data.mapper

import com.devkot.teammates.data.dto.CreateQuestionnaireRequestDto
import com.devkot.teammates.data.dto.LoginRequestDto
import com.devkot.teammates.data.dto.UpdateTokenRequestDto
import com.devkot.teammates.data.dto.UpdateUserProfileRequestDto
import com.devkot.teammates.domain.model.requesrt.CreateQuestionnaireRequest
import com.devkot.teammates.domain.model.requesrt.LoginRequest
import com.devkot.teammates.domain.model.requesrt.UpdateTokenRequest
import com.devkot.teammates.domain.model.requesrt.UpdateUserProfileRequest


fun LoginRequest.toDto(): LoginRequestDto =
    LoginRequestDto(nickname = this.nickname, password = this.password)

fun UpdateTokenRequest.toDto(): UpdateTokenRequestDto =
    UpdateTokenRequestDto(
        publicId = this.publicId,
        refreshToken = this.refreshToken
    )

fun CreateQuestionnaireRequest.toDto(): CreateQuestionnaireRequestDto =
    CreateQuestionnaireRequestDto(
        header = this.header,
        game = this.game,
        description = this.description,
        authorId = this.authorId
    )

fun UpdateUserProfileRequest.toDto(): UpdateUserProfileRequestDto =
    UpdateUserProfileRequestDto(
        nickname = this.nickname,
        publicId = this.publicId,
        description = this.description,
    )





