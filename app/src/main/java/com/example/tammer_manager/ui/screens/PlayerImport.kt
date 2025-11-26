package com.example.tammer_manager.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tammer_manager.data.player_import.importFromExcel
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel
import kotlinx.coroutines.launch

@Composable
fun PlayerImport(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    vmPlayerPool: PlayerPoolViewModel,
    context: Context,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val documentPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()){
            uri -> importFromExcel(
                context = context,
                uri = uri,
                vmPlayerPool = vmPlayerPool,
                onError = { scope.launch { snackBarHostState.showSnackbar("Error loading file") } }
            )
        }
        Text(
            text = "Import from:",
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                documentPicker.launch(
                arrayOf(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
            }
        ) {
            Text(".xlsx file")
        }
    }
}