package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import com.example.tammer_manager.ui.theme.Typography

@Composable
fun EnterResults(modifier: Modifier = Modifier) {

}

@Composable
fun PlayerScoreRow(
    color: PlayerColor,
    name: String,
    points: Float?,
    modifier: Modifier = Modifier,
    borderThickness: Dp = 1.dp,
) {
    Row(modifier = modifier
        .border(
            width = borderThickness,
            color = Color.Black
        )
        .background(color = Color.White)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(borderThickness * -1),
        verticalAlignment = Alignment.CenterVertically
    ){

        Box(modifier = Modifier
            .background(color = color.colorValue)
            .border(width = borderThickness, color = Color.Black)
            .fillMaxHeight()
            .layout() { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val currentHeight = placeable.height

                layout(currentHeight, currentHeight) {
                    placeable.placeRelative(0, 0)
                }
            }) {}

        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp).weight(1f),
            style = Typography.bodyLarge
        )

        Text(
            text = when(points){
                null -> "-"
                0.5f -> "½"
                else -> "%,.0f".format(locale = null, points)
            },
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 2.dp),
            style = Typography.headlineSmall
        )
    }
}