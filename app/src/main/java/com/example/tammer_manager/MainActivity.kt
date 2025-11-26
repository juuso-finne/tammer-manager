package com.example.tammer_manager

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tammer_manager.ui.screens.AppFrame
import com.example.tammer_manager.ui.screens.EnterPlayers
import com.example.tammer_manager.ui.screens.Home
import com.example.tammer_manager.ui.screens.PlayerImport
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(this)
        }
    }
}

@Composable
fun App( context: Context) {
    val navController = rememberNavController()
    val vmPlayerPool: PlayerPoolViewModel = viewModel()

    AppFrame(navController = navController) {
        innerPadding ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = "home"
        ) {
            composable ("home") { Home(navController) }
            composable("playerImport") { PlayerImport(navController) }
            composable (route = "enterPlayers") { EnterPlayers(vmPlayerPool = vmPlayerPool) }
        }
    }
}