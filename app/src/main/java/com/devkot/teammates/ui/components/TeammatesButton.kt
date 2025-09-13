package com.devkot.teammates.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devkot.teammates.ui.theme.TeammatesTheme


@Composable
fun TeammatesButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = true,
    imageVector: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ShapeDefaults.Small,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        imageVector?.let {
            Icon(imageVector = it, contentDescription = null)
        }
        text?.let {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(name = "With Icon")
@Composable
fun TeammatesButtonWithIconPreview() {
    TeammatesTheme {
        TeammatesButton(onClick = {}, text = "Click", imageVector = Icons.Default.AdsClick)
    }
}

@Preview(name = "Only Icon")
@Composable
fun TeammatesButtonOnlyIconPreview() {
    TeammatesTheme {
        TeammatesButton(onClick = {}, imageVector = Icons.Default.AdsClick)
    }
}

@Preview(name = "Without Icon")
@Composable
fun TeammatesButtonWithoutIconPreview() {
    TeammatesTheme {
        TeammatesButton(
            onClick = {}, text = "Click"
        )
    }
}

@Preview(name = "Dark Theme")
@Composable
fun TeammatesButtonDarkThemePreview() {

    TeammatesTheme(darkTheme = true) {
        TeammatesButton(onClick = {}, text = "Click", imageVector = Icons.Default.AdsClick)
    }
}
