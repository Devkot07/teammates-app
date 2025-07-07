package com.pezont.teammates.data.mapper

import com.pezont.teammates.data.dto.CreateQuestionnaireRequestDto
import com.pezont.teammates.data.dto.LoginRequestDto
import com.pezont.teammates.data.dto.LoginResponseDto
import com.pezont.teammates.data.dto.QuestionnaireDto
import com.pezont.teammates.data.dto.UpdateTokenRequestDto
import com.pezont.teammates.data.dto.UpdateTokenResponseDto
import com.pezont.teammates.data.dto.UpdateUserProfilePhotoResponseDto
import com.pezont.teammates.data.dto.UpdateUserProfileRequestDto
import com.pezont.teammates.data.dto.UserDto
import com.pezont.teammates.domain.model.CreateQuestionnaireRequest
import com.pezont.teammates.domain.model.LoginRequest
import com.pezont.teammates.domain.model.LoginResponse
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.UpdateTokenRequest
import com.pezont.teammates.domain.model.UpdateTokenResponse
import com.pezont.teammates.domain.model.UpdateUserProfilePhotoResponse
import com.pezont.teammates.domain.model.UpdateUserProfileRequest
import com.pezont.teammates.domain.model.User

fun UserDto.toDomain(): User = User(
    publicId = this.publicId.orEmpty(),
    nickname = this.nickname.orEmpty(),
    email = this.email.orEmpty(),
    description = this.description.orEmpty(),
    imagePath = this.imagePath.orEmpty()
)

fun QuestionnaireDto.toDomain(): Questionnaire = Questionnaire(
    header = this.header.orEmpty(),
    game = this.game.orEmpty(),
    description = this.description.orEmpty(),
    authorId = this.authorId.orEmpty(),
    questionnaireId = this.questionnaireId.orEmpty(),
    imagePath = this.imagePath.orEmpty()
)


fun LoginRequest.toDto(): LoginRequestDto =
    LoginRequestDto(nickname = this.nickname, password = this.password)

fun LoginResponseDto.toDomain(): LoginResponse =
    LoginResponse(
        user = this.user.toDomain(),
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )


fun UpdateTokenRequest.toDto(): UpdateTokenRequestDto =
    UpdateTokenRequestDto(
        publicId = this.publicId,
        refreshToken = this.refreshToken
    )

fun UpdateTokenResponseDto.toDomain(): UpdateTokenResponse =
    UpdateTokenResponse(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )


fun CreateQuestionnaireRequest.toDto(): CreateQuestionnaireRequestDto =
    CreateQuestionnaireRequestDto(
        header = this.header,
        game = this.game,
        description = this.description,
        authorId = this.authorId
    )

fun UpdateUserProfilePhotoResponseDto.toDomain(): UpdateUserProfilePhotoResponse =
    UpdateUserProfilePhotoResponse(
        userId = this.userId,
        imagePath = this.imagePath
    )

fun UpdateUserProfileRequest.toDto(): UpdateUserProfileRequestDto =
    UpdateUserProfileRequestDto(
        nickname = this.nickname,
        publicId = this.publicId,
        description = this.description,
    )





