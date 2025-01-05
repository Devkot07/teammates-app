package com.pezont.teammates.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R

@Composable
fun ErrorNetworkScreen(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.SignalWifiStatusbarConnectedNoInternet4,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.no_internet_connection),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onClick() }) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}