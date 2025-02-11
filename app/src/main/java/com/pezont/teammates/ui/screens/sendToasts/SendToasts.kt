package com.pezont.teammates.ui.screens.sendToasts

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pezont.teammates.R

// TODO: make general
@Composable
fun SendNetworkErrorToast(status: Int, context: Context) {
    when (status){
        400 -> {
            Toast.makeText(context,
                stringResource(R.string.authorization_error_incorrect_login_or_password), Toast.LENGTH_SHORT).show()}
        401 ->{ Toast.makeText(context,
            stringResource(R.string.authorization_error_authorization_failed), Toast.LENGTH_SHORT).show()}
        1 -> { Toast.makeText(context, stringResource(R.string.logout), Toast.LENGTH_SHORT).show()}
        else -> {}
    }

}
