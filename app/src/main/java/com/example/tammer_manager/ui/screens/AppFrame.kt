package com.example.tammer_manager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tammer_manager.R
import com.example.tammer_manager.ui.components.NavigationMenu
import kotlinx.coroutines.launch

@Composable
fun AppFrame(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavigationMenu(
        drawerState = drawerState,
        modifier =  modifier,
        onItemClick = {route ->
            navController.navigate(route)
            scope.launch { drawerState.close() }
        },
        scope = scope
    ){
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                AppTitleBar(
                    modifier = modifier,
                    openDrawer = {scope.launch { drawerState.open() }}
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }

}

@Composable
fun AppTitleBar(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit
    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 6.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
    ){
        Row(modifier = Modifier.weight(1f)){
            IconButton(
                onClick = {openDrawer()}
            ){
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Text(
            text = stringResource(R.string.app_name),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}