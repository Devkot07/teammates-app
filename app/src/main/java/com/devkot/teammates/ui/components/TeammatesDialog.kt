package com.devkot.teammates.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.devkot.teammates.R
import com.devkot.teammates.ui.theme.TeammatesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeammatesDialog(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.confirm_the_action),
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onCancel() },
        modifier = modifier,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = AlertDialogDefaults.TonalElevation,
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
            )

        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(message)

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TeammatesButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        text = "Cancel",
                    )
                    TeammatesButton(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        text = "Confirm",
                    )
                }

            }
        }

    }
}

@Preview
@Composable
fun TeammatesDialogPreview() {
    TeammatesTheme(darkTheme = true) {
        Box(

            Modifier
                .size(100.dp)
                .background(Color.White)
                .padding(10.dp),
        ) {

            TeammatesDialog(onConfirm = {}, onCancel = {})

        }
    }
}