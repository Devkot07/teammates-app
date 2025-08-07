package com.devkot.teammates.ui.snackbar

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarEvent(
    val messageId: Int,
    val formatArgs: List<Any> = emptyList(),
    val action:  SnackbarAction? = null,
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit
)


object SnackbarController {

    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }


}