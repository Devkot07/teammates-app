package com.pezont.teammates.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R
import com.pezont.teammates.ui.theme.TeammatesTheme

@Composable
fun LoadingItem(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.padding(50.dp)
        ) { CircularProgressIndicator(modifier = Modifier.size(100.dp)) }
    }
}

@Composable
fun LoadingItemWithText(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
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
        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = stringResource(R.string.loading),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun LoadingItemPreview() {
    TeammatesTheme{
        LoadingItem()
    }
}

@Preview
@Composable
fun LoadingItemWithTextPreview() {
    TeammatesTheme{
        LoadingItemWithText()
    }
}