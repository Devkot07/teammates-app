package com.pezont.teammates.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R
import com.pezont.teammates.ui.navigation.NavigationDestination

object ErrorDestination : NavigationDestination {
    override val route = "error"
    override val titleRes = R.string.error
}

//TODO error Screen
@Composable
fun ErrorScreen(
    onClick: () -> Unit, errorText: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = if (errorText != null) "${stringResource(R.string.error)}: $errorText"
                else stringResource(R.string.something_don_t_work_that_s_all_we_known),

                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Red,
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { onClick() }, modifier = Modifier.wrapContentWidth()
            ) { Text(text = stringResource(R.string.retry)) }
        }
    }
}