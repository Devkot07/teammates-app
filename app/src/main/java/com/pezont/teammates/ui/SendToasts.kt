package com.pezont.teammates.ui

import android.content.Context
import android.widget.Toast
import com.pezont.teammates.R


fun sendAuthToast(status: Int?, context: Context) {
    when (status){
        400 -> {
            Toast.makeText(context,
                R.string.authorization_error_incorrect_login_or_password,
                Toast.LENGTH_SHORT).show()
        }
        401 ->{
            Toast.makeText(context,
            R.string.authorization_error_authorization_failed,
                Toast.LENGTH_SHORT).show()
        }
        1 -> { Toast.makeText(context,
            R.string.logout,
            Toast.LENGTH_SHORT).show()}
        else -> {}
    }
}

fun sendQuestionnairesToast(status: Int?, context: Context) {
    when (status){
        401 ->{ Toast.makeText(context,
            "sendQuestionnairesToast", Toast.LENGTH_SHORT).show()}
        1 -> { Toast.makeText(context,
            R.string.logout, Toast.LENGTH_SHORT).show()}
        else -> {}
    }
}


