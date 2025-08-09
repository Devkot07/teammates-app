package com.devkot.teammates.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devkot.teammates.domain.model.Questionnaire

data class QuestionnaireFields(
    @ColumnInfo(name = "header") val header: String,
    @ColumnInfo(name = "game") val game: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    @PrimaryKey
    @ColumnInfo(name = "questionnaire_id") val questionnaireId: String,
    @ColumnInfo(name = "image_path") val imagePath: String
)

@Entity(tableName = "questionnaire")
data class QuestionnaireEntity(
    @Embedded val fields: QuestionnaireFields
)

@Entity(tableName = "like_questionnaire")
data class LikeQuestionnaireEntity(
    @Embedded val fields: QuestionnaireFields
)

fun QuestionnaireEntity.toDomain(): Questionnaire = Questionnaire(
    header = this.fields.header,
    game = this.fields.game,
    description = this.fields.description,
    authorId = this.fields.authorId,
    questionnaireId = this.fields.questionnaireId,
    imagePath = this.fields.imagePath
)

fun Questionnaire.toQuestionnaireEntity(): QuestionnaireEntity = QuestionnaireEntity(
    QuestionnaireFields(
        header = this.header,
        game = this.game,
        description = this.description,
        authorId = this.authorId,
        questionnaireId = this.questionnaireId,
        imagePath = this.imagePath
    )
)

fun LikeQuestionnaireEntity.toDomain(): Questionnaire = Questionnaire(
    header = this.fields.header,
    game = this.fields.game,
    description = this.fields.description,
    authorId = this.fields.authorId,
    questionnaireId = this.fields.questionnaireId,
    imagePath = this.fields.imagePath
)

fun Questionnaire.toLikeQuestionnaireEntity(): LikeQuestionnaireEntity = LikeQuestionnaireEntity(
    QuestionnaireFields(
        header = this.header,
        game = this.game,
        description = this.description,
        authorId = this.authorId,
        questionnaireId = this.questionnaireId,
        imagePath = this.imagePath
    )

)

