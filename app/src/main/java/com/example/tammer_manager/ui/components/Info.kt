package com.example.tammer_manager.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tammer_manager.ui.theme.Typography

@Composable
fun NoActiveTournament(modifier: Modifier = Modifier) {
    Text(
        text = "No active tournament",
        modifier = modifier.fillMaxWidth().padding(top=32.dp),
        style = Typography.headlineMedium,
        textAlign = TextAlign.Center
    )
}

