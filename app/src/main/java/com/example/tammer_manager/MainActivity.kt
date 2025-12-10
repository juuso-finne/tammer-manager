package com.example.tammer_manager

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tammer_manager.ui.screens.AppFrame
import com.example.tammer_manager.ui.screens.EnterResults
import com.example.tammer_manager.ui.screens.enter_players.EnterPlayers
import com.example.tammer_manager.ui.screens.Home
import com.example.tammer_manager.ui.screens.PlayerImport
import com.example.tammer_manager.viewmodels.PlayerPoolViewModel
import com.example.tammer_manager.viewmodels.TournamentViewModel

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
    val vmTournament: TournamentViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    AppFrame(
        navController = navController,
        snackbarHostState = snackbarHostState
    ) {
        innerPadding ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = "home"
        ) {
            composable ("home") { Home(navController) }

            composable("playerImport") { PlayerImport(
                context = context,
                vmPlayerPool = vmPlayerPool,
                navController = navController,
                snackBarHostState = snackbarHostState
            ) }

            composable (route = "enterPlayers") { EnterPlayers(
                vmPlayerPool = vmPlayerPool,
                vmTournament = vmTournament,
                navController = navController,
                snackbarHostState = snackbarHostState
            ) }

            composable (route = "enterResults") {
                EnterResults(
                    vmTournament = vmTournament
                )
            }
        }
    }
}