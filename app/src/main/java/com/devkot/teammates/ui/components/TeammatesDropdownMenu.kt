package com.devkot.teammates.ui.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.devkot.teammates.domain.model.enums.Games

@Composable
fun TeammatesDropdownMenu(
    expanded: Boolean,
    items: List<DropdownItem>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset((-4).dp, (-40).dp),
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 1.dp,
                shape = ShapeDefaults.Small,
                color = MaterialTheme.colorScheme.outline,
            )
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                onClick = item.onClick,
                leadingIcon = { Icon(item.icon, null) },
                modifier = modifier
                    .wrapContentSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}

@Composable
fun <T> AnimatedDropdownMenu(
    isExpanded: Boolean,
    items: List<T>,
    itemText: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
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
                    .verticalScroll(rememberScrollState())
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemText(item)) },
                        onClick = { onItemSelected(item) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }

}

@Composable
fun GamesDropdownMenu(
    expanded: Boolean,
    games: List<Games>,
    onGameSelected: (Games) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedDropdownMenu(
        isExpanded = expanded,
        items = games,
        itemText = { it.nameOfGame },
        onItemSelected = onGameSelected,
        modifier = modifier
    )
}

data class DropdownItem(
    val text: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)