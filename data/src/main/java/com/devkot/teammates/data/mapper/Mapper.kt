package com.devkot.teammates.data.mapper

import com.devkot.teammates.data.remote.dto.QuestionnaireInRequestDto
import com.devkot.teammates.data.remote.dto.LoginRequestDto
import com.devkot.teammates.data.remote.dto.UpdateTokenRequestDto
import com.devkot.teammates.data.remote.dto.UpdateUserProfileRequestDto
import com.devkot.teammates.domain.model.requesrt.QuestionnaireInRequest
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

fun QuestionnaireInRequest.toDto(): QuestionnaireInRequestDto =
    QuestionnaireInRequestDto(
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





