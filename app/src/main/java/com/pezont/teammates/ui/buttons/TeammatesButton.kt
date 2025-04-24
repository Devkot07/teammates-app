package com.pezont.teammates.ui.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pezont.teammates.R


@Composable
//TODO Main Button
fun TeammatesButton( onClick:()->Unit, ) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = ShapeDefaults.Small,
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.subscribe),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
