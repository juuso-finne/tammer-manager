package com.example.tammer_manager.ui.screens.save_and_load

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tammer_manager.ui.components.ConfirmDialog
import com.example.tammer_manager.ui.components.ErrorDialog
import com.example.tammer_manager.viewmodels.TournamentViewModel
import com.example.tammer_manager.ui.theme.Typography

@Composable
fun SaveTournament(
    vmTournament: TournamentViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val (filename, setFilename) = remember { mutableStateOf("") }
        val (confirmDialog, setConfirmDialog) = remember { mutableStateOf(false) }
        val (errorDialog, setErrorDialog) = remember { mutableStateOf(false) }

        val illegalCharacters = remember (filename) { Regex("[^A-Za-z0-9åäöÅÄÖ_-]").containsMatchIn(filename) }

        val context = LocalContext.current

        Text(
            text ="Save as",
            style = Typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text("Filename") },
            value = filename,
            onValueChange = { setFilename(it) },
        )

        if(illegalCharacters){
            Text(
                text = "Filename can only contain Letters A-Ö, numbers, dashes(-) and underscores(_)",
                color = Color.Red,
                style = Typography.labelSmall,
                modifier = Modifier.padding(start = 15.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(
                enabled = filename.isNotEmpty() && !illegalCharacters,
                onClick = {
                    vmTournament.saveAs(
                        newFilename = filename,
                        context = context,
                        onError = { setErrorDialog(true) },
                        onSuccess = { navController.navigate("home") },
                        confirmOverWrite = { setConfirmDialog(true) }
                    )
                }
            ){
                Text("Save")
            }

            Button(
                onClick = {
                    navController.navigate("home")
                }
            ){
                Text("Cancel")
            }
        }

        when{ confirmDialog ->
            ConfirmDialog(
                onDismissRequest = { setConfirmDialog(false) },
                onConfirmRequest = {
                    vmTournament.saveAs(
                        newFilename = filename,
                        onError = { setErrorDialog(true) },
                        onSuccess = { navController.navigate("home") },
                        overWrite = true,
                        context = context
                    )
                },
                confirmButtonText = "Yes",
                dismissButtonText = "No",
                confirmationText = "File $filename already exists. Do you want to replace the file?"
            )
        }

        when{ errorDialog ->
            ErrorDialog(
                onDismissRequest = { setErrorDialog(false) },
                errorText = "Unable to save file $filename"
            )
        }
    }
}