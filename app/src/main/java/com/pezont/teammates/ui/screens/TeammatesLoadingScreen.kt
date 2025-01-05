package com.pezont.teammates.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R

@Composable
fun TeammatesLoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()

    ) {
        Box(
            modifier = Modifier
                .padding(start = 50.dp, end = 50.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = stringResource(R.string.loading),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(stringResource(R.string.please_wait))

    }
}