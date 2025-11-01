package com.example.study

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.study.data.DataSource
import com.example.study.data.LogCSV
import com.example.study.data.SettingsDataStore
import com.example.study.model.HorizontalViewModel
import com.example.study.model.TwoDViewModel
import com.example.study.model.VerticalViewModel

private val participantIDs: List<String> = DataSource().getParticipantIDs()
private val selectionMethods: List<String> = DataSource().getSelectionMethods()
private val remoteMountingSides: List<String> = DataSource().getRemoteMountingSides()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(verticalViewModel: VerticalViewModel, horizontalViewModel: HorizontalViewModel, viewModel2D: TwoDViewModel, settingsDataStore: SettingsDataStore, logCSV: LogCSV, modifier: Modifier = Modifier) {
    // first dropdown menu
    var expandedParticipant by remember { mutableStateOf(false) }
    var participantID by remember { mutableStateOf(settingsDataStore.participantID) }

    // second dropdown menu
    var expandedSelectionMethod by remember { mutableStateOf(false) }
    var selectionMethod by remember { mutableStateOf(settingsDataStore.selectionMethod) }

    // third dropdown menu
    var expandedRemoteMountingSide by remember { mutableStateOf(false) }
    var remoteMountingSide by remember { mutableStateOf(settingsDataStore.remoteMountingSide) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = modifier.padding(48.dp))
        Text(
            text = "Welcome to my study!",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.padding(8.dp))
        Spacer(modifier = modifier.padding(8.dp))
        Text(
            text = "Please choose some App from the drawer menu..",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(16.dp))
        // first dropdown menu
        ExposedDropdownMenuBox(
            expanded = expandedParticipant,
            onExpandedChange = { expandedParticipant = !expandedParticipant }
        ) {
            OutlinedTextField(
                value = participantID,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedParticipant) },
                modifier = Modifier
                    .clickable { expandedParticipant = !expandedParticipant }
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
            )
            ExposedDropdownMenu(
                expanded = expandedParticipant,
                onDismissRequest = { expandedParticipant = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                participantIDs.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            participantID = item
                            settingsDataStore.participantID = item
                            expandedParticipant = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        // second dropdown menu
        ExposedDropdownMenuBox(
            expanded = expandedSelectionMethod,
            onExpandedChange = { expandedSelectionMethod = !expandedSelectionMethod }
        ) {
            OutlinedTextField(
                value = selectionMethod,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSelectionMethod) },
                modifier = Modifier
                    .clickable { expandedSelectionMethod = !expandedSelectionMethod }
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
            )

            ExposedDropdownMenu(
                expanded = expandedSelectionMethod,
                onDismissRequest = { expandedSelectionMethod = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                selectionMethods.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectionMethod = item
                            settingsDataStore.selectionMethod = item
                            expandedSelectionMethod = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        // third dropdown menu
        ExposedDropdownMenuBox(
            expanded = expandedRemoteMountingSide,
            onExpandedChange = { expandedRemoteMountingSide = !expandedRemoteMountingSide }
        ) {
            OutlinedTextField(
                value = remoteMountingSide,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRemoteMountingSide) },
                modifier = Modifier
                    .clickable { expandedRemoteMountingSide = !expandedRemoteMountingSide }
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
            )

            ExposedDropdownMenu(
                expanded = expandedRemoteMountingSide,
                onDismissRequest = { expandedRemoteMountingSide = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                remoteMountingSides.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            remoteMountingSide = item
                            settingsDataStore.remoteMountingSide = item
                            expandedRemoteMountingSide = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            onClick = {
                verticalViewModel.resetModel()
                horizontalViewModel.resetModel()
                viewModel2D.resetModel()
                settingsDataStore.balanceLatinSquare.walkthrough = (settingsDataStore.balanceLatinSquare.balanceLatinSquare[participantID]?.get(selectionMethod) ?: emptyList()).toMutableList()
                if ( !settingsDataStore.balanceLatinSquare.walkthrough.isEmpty() ) {
                    // choose next app path
                    val route = when (settingsDataStore.balanceLatinSquare.walkthrough.removeFirstOrNull()) {
                        "Vertical" -> NavDrawerItem.Vertical.route
                        "Horizontal" -> NavDrawerItem.Horizontal.route
                        "2D" -> if(selectionMethod == "Direct Touch") NavDrawerItem.TwoDimensionalFS.route else NavDrawerItem.TwoDimensional.route
                        else -> NavDrawerItem.Home.route
                    }

                    logCSV.createNewFile()

                    settingsDataStore.navController.navigate(route) {
                        popUpTo(NavDrawerItem.Home.route) { inclusive = true }
                    }
                }
            }
        ) {
            Text("Start Assignment")
        }
    }
}