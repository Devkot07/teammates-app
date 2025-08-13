package com.devkot.teammates.utils

import com.devkot.teammates.R
import com.devkot.teammates.domain.model.ValidationError


fun ValidationError.toMessageRes(): Int = when (this) {

    ValidationError.HEADER_TOO_SHORT -> R.string.the_header_must_contain_at_least_3_characters
    ValidationError.HEADER_TOO_LONG -> R.string.the_maximum_length_of_the_header_is_80_characters
    ValidationError.DESCRIPTION_TOO_LONG -> R.string.the_maximum_length_of_the_description_is_300_characters
    ValidationError.GAME_NOT_SELECTED -> R.string.please_select_a_game
    ValidationError.NICKNAME_TOO_SHORT -> R.string.the_nickname_must_contain_at_least_3_characters
    ValidationError.NICKNAME_TOO_LONG -> R.string.the_maximum_length_of_the_nickname_is_20_characters
}

