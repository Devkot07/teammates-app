package com.pezont.teammates.ui

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@Composable
fun <T> TeammatesDropdownMenu(
    isExpanded: Boolean,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = isExpanded, label = "DropdownTransition")

    val height by transition.animateDp(
        transitionSpec = { tween(durationMillis = 400) },
        label = "DropdownHeight"
    ) { expanded ->
        if (expanded) (items.size * 48).dp else 0.dp
    }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 700) },
        label = "DropdownAlpha"
    ) { expanded ->
        if (expanded) 1f else 0f
    }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .graphicsLayer { this.alpha = alpha }
                .clip(MaterialTheme.shapes.small)
                .shadow(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        shape = ShapeDefaults.Small,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.toString()) },
                        onClick = { onItemSelected(item) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }
}
