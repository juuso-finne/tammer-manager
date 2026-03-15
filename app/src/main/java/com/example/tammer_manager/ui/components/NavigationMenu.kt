package com.example.tammer_manager.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tammer_manager.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationMenu(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
    onItemClick: (String) -> Unit,
    scope: CoroutineScope,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet { NavigationList(
                itemTextStyle = itemTextStyle,
                onItemClick = onItemClick,
                drawerState = drawerState,
                scope = scope
            ) }
        }
    ){content()}
}

@Composable
fun NavigationList(
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle,
    onItemClick: (String) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope
) {

    val items = listOf(
        MenuItem("home", stringResource(R.string.main_menu)),
        MenuItem("enterPlayers", stringResource(R.string.enter_players)),
        MenuItem("enterResults", stringResource(R.string.enter_results)),
        MenuItem("standings", stringResource(R.string.standings))
    )

    LazyColumn(modifier) {
        item{
            CloseMenuButton(drawerState, scope)
        }
        items(items.size) { i ->
            Column() {
                val item = items[i]
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onItemClick(item.route) }
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = item.text,
                            style = itemTextStyle
                        )
                    }
                }

                HorizontalDivider()
            }
        }
    }
}

@Composable
fun CloseMenuButton(
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    Column() {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ){
            IconButton(
                onClick = { scope.launch { drawerState.close() } }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_menu)
                )
            }

        }
        HorizontalDivider()
    }

}

data class MenuItem(
    val route: String,
    val text: String
)