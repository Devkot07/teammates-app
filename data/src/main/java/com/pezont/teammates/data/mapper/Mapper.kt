package com.pezont.teammates.data.mapper

import com.pezont.teammates.data.dto.CreateQuestionnaireRequestDto
import com.pezont.teammates.data.dto.LoginRequestDto
import com.pezont.teammates.data.dto.UpdateTokenRequestDto
import com.pezont.teammates.data.dto.UpdateUserProfileRequestDto
import com.pezont.teammates.domain.model.CreateQuestionnaireRequest
import com.pezont.teammates.domain.model.LoginRequest
import com.pezont.teammates.domain.model.UpdateTokenRequest
import com.pezont.teammates.domain.model.UpdateUserProfileRequest


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





