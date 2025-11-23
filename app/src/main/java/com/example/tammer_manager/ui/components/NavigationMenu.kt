package com.example.tammer_manager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val items = listOf(
    MenuItem("home", "Main menu")
)


@Composable
fun NavigationMenu(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (String) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet { NavigationList(modifier, itemTextStyle, onItemClick) }
        }
    ){content()}
}

@Composable
fun NavigationList(
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle,
    onItemClick: (String) -> Unit
) {
    LazyColumn(modifier) {
        items(items.size) { i ->
            val item = items[i]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item.route)
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = item.text,
                    style = itemTextStyle
                )
            }
        }
    }
}

data class MenuItem(
    val route: String,
    val text: String
)