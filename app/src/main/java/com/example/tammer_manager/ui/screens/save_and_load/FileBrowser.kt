package com.example.tammer_manager.ui.screens.save_and_load

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.ui.components.ConfirmDialog
import com.example.tammer_manager.ui.components.ErrorDialog
import com.example.tammer_manager.ui.theme.Typography
import com.example.tammer_manager.viewmodels.TournamentViewModel

@Composable
fun FileBrowser(
    vmTournament: TournamentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        val (fileList, setFileList) = remember { mutableStateOf(vmTournament.getFileList(context)) }
        val checkedIndices = remember(fileList) { listOf<Int>().toMutableStateList() }
        val (confirmDelete, setConfirmDelete) = remember { mutableStateOf(false) }
        val (error, setError) = remember { mutableStateOf(false) }
        val (errorText, setErrorText) = remember { mutableStateOf("") }

        Text(
            text ="Saved tournaments",
            style = Typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (fileList.isNotEmpty()){
            FileColumn(
                fileList = fileList,
                checkedIndices = checkedIndices
            )
        } else {
            Text(
                text = "No files to show",
                style = Typography.headlineMedium,
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                enabled = checkedIndices.size == 1,
                onClick = {
                    val success = vmTournament.load(
                        context = context,
                        filename = fileList[checkedIndices.first()]
                    )

                    if (success){
                        navController.navigate("home")
                    } else{
                        setError(true)
                        setErrorText("Unable to load file '${fileList[checkedIndices.first()]}'")
                    }
                }
            ) { Text ("Load") }

            Button(
                onClick = { setConfirmDelete(true) },
                enabled = checkedIndices.isNotEmpty()
            ) { Text("Delete") }
        }

        Button(onClick = { navController.navigate("home") }) { Text("Cancel") }

        when { confirmDelete ->
            ConfirmDialog(
                onDismissRequest = { setConfirmDelete(false) },
                onConfirmRequest = {
                    for(i in checkedIndices.indices) {
                        val it = checkedIndices[i]
                        if (!vmTournament.delete(context = context, filename = fileList[it])){
                            setError(true)
                            setErrorText("Unable to delete file '${fileList[it]}'")
                            break
                        }
                    }

                    setFileList(vmTournament.getFileList(context))
                    setConfirmDelete(false)
                },
                confirmButtonText = "Yes",
                dismissButtonText = "No",
                dialogText =
                    if (checkedIndices.size == 1) "Delete '${fileList[checkedIndices.first()]}'?"
                    else "Delete ${checkedIndices.size} files?"
            )
        }

        when { error ->
            ErrorDialog(
                onDismissRequest = { setError(false) },
                errorText = errorText
            )
        }
    }
}

@Composable
fun FileColumn(
    modifier: Modifier = Modifier,
    fileList: List<String>,
    checkedIndices: SnapshotStateList<Int>

){
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Checkbox(checked = checkedIndices.size == fileList.size, onCheckedChange = {
            if (it){
                checkedIndices.addAll(fileList.indices.minus(checkedIndices))
            } else {
                checkedIndices.clear()
            }
        })
        Text(
            text = "Select all",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 5.dp),
            style = Typography.bodyLarge
        )
    }

    LazyColumn(modifier = modifier.padding(10.dp)
        .background(Color.White)
        .border(
            width = 1.dp,
            color = Color.Black
        )
    ) {
        items(fileList.size){ index ->
            FileListItem(
                index = index,
                filename = fileList[index],
                checkedIndices = checkedIndices,
            )
        }
    }
}

@Composable
fun FileListItem(
    index: Int,
    filename: String,
    checkedIndices: SnapshotStateList<Int>,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ){

        Checkbox(
            checked = index in checkedIndices,
            onCheckedChange = {
                if (it){
                    checkedIndices.add(index)
                } else{
                    checkedIndices.remove(index)
                }
            }
        )

        Text(
            text = filename,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 5.dp),
            style = Typography.bodyLarge
        )
    }
}