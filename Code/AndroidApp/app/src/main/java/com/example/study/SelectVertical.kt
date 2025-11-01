package com.example.study

import android.annotation.SuppressLint
import android.media.RingtoneManager
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.study.data.DataSource
import com.example.study.data.LogCSV
import com.example.study.data.SettingsDataStore
import com.example.study.model.ControlUiState
import com.example.study.model.ControlViewModel
import com.example.study.model.VerticalUiState
import com.example.study.model.VerticalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val cardsOnScreen = 13// number of card which can be seen on screen

private var verticalNumber = 25
private var markedIndex = 1
private var cardTopIndex = cardsOnScreen / 2        // Index of the top list element for scrolling

private lateinit var log : LogCSV
private var desc_old = ""
private var eventList = listOf("User Event", "Control Event", "App Event")

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SelectVerticalApp(controlViewModel: ControlViewModel, verticalViewModel: VerticalViewModel, settingsDataStore: SettingsDataStore, logCSV: LogCSV, modifier: Modifier = Modifier) {
    log = logCSV

    // Read current control state
    // Control events coming from the Arduino device
    val controlUIState = controlViewModel.controlUIState

    // Read the number of rows from the settings datastore
    val numberOfRowsSettings by settingsDataStore.getVerticalNumber.collectAsState(initial = 25)
    verticalNumber = numberOfRowsSettings ?: 25

    // Read the wait time from the settings datastore
    val waitTimeSettings by settingsDataStore.getWaitTime.collectAsState(initial = 20000)
    val waitTime = waitTimeSettings ?: 20000
    verticalViewModel.waitTime = waitTime

    // Get a predefined array to generate the list automatically
    val list = DataSource().loadRowData(verticalNumber)

    // Remember variables over recomposition
    var clickedIndex : Int by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Evaluate select event
    if ( controlUIState == ControlUiState.SELECT ) {
        clickedIndex = markedIndex
        log("Control Event", "SELECT", clickedIndex.toString())
    }
    if ( clickedIndex == verticalViewModel.targetIndex && verticalViewModel.verticalUIState == VerticalUiState.TARGET ) {
        log("App Event", "Target selected")
        // deactivate the destination index by setting it on a high value
        verticalViewModel.targetIndex = 100
        verticalViewModel.targetCounter++
        if ( verticalViewModel.targetCounter >= 8 ) {
            verticalViewModel.verticalUIState = VerticalUiState.END
        } else {
            verticalViewModel.verticalUIState = VerticalUiState.WAIT
        }
    }

    // State machine changes
    if ( verticalViewModel.verticalUIState == VerticalUiState.END ) {
        log("State", "END")
        log("App Event", "Vertical assignment finished")
        Toast.makeText(context, "Congratulations! You have passed the vertical assignment!", Toast.LENGTH_SHORT).show()
        verticalViewModel.resetModel()
        // go to next assignment or home
        if ( !settingsDataStore.balanceLatinSquare.walkthrough.isEmpty() ) {
            val route = when (settingsDataStore.balanceLatinSquare.walkthrough.removeFirstOrNull()) {
                "Horizontal" -> NavDrawerItem.Horizontal.route
                "2D" -> if(settingsDataStore.selectionMethod == "Direct Touch") NavDrawerItem.TwoDimensionalFS.route else NavDrawerItem.TwoDimensional.route
                else -> NavDrawerItem.Home.route
            }
            settingsDataStore.navController.navigate(route) {
                popUpTo(NavDrawerItem.Home.route) { inclusive = true }
            }
        }
        else {
            log("App Event", "All assignments finished")
            settingsDataStore.navController.navigate(NavDrawerItem.Home.route) {
                popUpTo(NavDrawerItem.Home.route) { inclusive = true }
            }
        }
    }
    if ( verticalViewModel.verticalUIState == VerticalUiState.INIT ) {
        log("State", "INIT")
        resetUI(verticalViewModel, coroutineScope, listState)
        verticalViewModel.verticalUIState = VerticalUiState.START
    }
    if ( verticalViewModel.verticalUIState == VerticalUiState.START ) {
        log("State", "START")
    }
    if ( verticalViewModel.verticalUIState == VerticalUiState.WAIT ) {
        log("State", "WAIT")
        verticalViewModel.targetIndex = 100
        clickedIndex = 101
    }
    if ( verticalViewModel.verticalUIState == VerticalUiState.RESET ) {
        // play notification sound
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        resetUI(verticalViewModel, coroutineScope, listState)
        val timestamp = System.currentTimeMillis()
        verticalViewModel.targetTimeStart = timestamp
        verticalViewModel.verticalUIState = VerticalUiState.TARGET
        verticalViewModel.targetIndex = when ( verticalViewModel.targetDirection ) {
            "Up_out" -> (1 .. (cardsOnScreen/2)).random()
            "Up" -> ((cardsOnScreen / 2) + 1 until cardsOnScreen ).random()
            "Down" -> (cardsOnScreen + 1 .. cardsOnScreen + (cardsOnScreen / 2) ).random()
            "Down_out" -> ((cardsOnScreen + (cardsOnScreen / 2) + 1) until cardsOnScreen * 2).random()
            else -> (1 .. cardsOnScreen * 2).random()
        }
        log("State", "RESET")
        log("App Event", "Target direction[" + verticalViewModel.targetDirection + "]", verticalViewModel.targetIndex.toString())
    }

    // change the indicated position of the marked card and scroll the list if needed
    changeMarkedIndexByControl(controlUIState)
    coroutineScope.launch {
        if ( controlUIState == ControlUiState.UP ) {
            log("Control Event", "UP", markedIndex.toString())
            if ( markedIndex < cardTopIndex + 1) {
                listState.scrollToItem(index = markedIndex - 1, scrollOffset = 0)
                cardTopIndex = markedIndex -1
            }
        }
        if ( controlUIState == ControlUiState.DOWN ) {
            log("Control Event", "DOWN", markedIndex.toString())
            if ( markedIndex > cardTopIndex + cardsOnScreen ) {
                listState.scrollToItem(index = cardTopIndex + 1, scrollOffset = 0)
                cardTopIndex += 1
            }
        }
    }

    // Top column element which is the frame of the layout
    Column(
        modifier = modifier.padding(top = 60.dp)
    )
    {
        Spacer(modifier = modifier
            .height(6.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
        )
        Card (
            colors = if (verticalViewModel.targetIndex > cardsOnScreen * 2) {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            } else if (verticalViewModel.targetIndex < markedIndex) {
                CardDefaults.cardColors(Color.Cyan, Color.Black)
            } else if (verticalViewModel.targetIndex == markedIndex) {
                CardDefaults.cardColors(Color.Green, Color.Black)
            } else {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            },
            modifier = modifier
                .background(Color.DarkGray)
                .padding(6.dp)
                .fillMaxWidth()
                .clickable {
                    log("User Event", "UP clicked")
                    controlViewModel.controlUIState = ControlUiState.UP
                }
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Icon (
                    Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "Keyboard arrow up",
                    modifier = modifier
                        .size(64.dp)
                )
            }
        }
        Spacer(modifier = modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
        )
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp),
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .background(Color.DarkGray)
                .weight(1.0f)
        ) {
            items(list) {content ->
                Card(
                    border = when(content.row) {
                        markedIndex -> BorderStroke(8.dp, Color.Red)
                        verticalViewModel.targetIndex -> BorderStroke(2.dp, Color.Cyan)
                        else -> BorderStroke(2.dp, Color.LightGray)
                    },
                    colors = if (verticalViewModel.targetIndex == content.row) {
                        CardDefaults.cardColors(Color.Cyan, Color.Black)
                    } else {
                        CardDefaults.cardColors(Color.LightGray, Color.Black)
                    },
                    modifier = modifier
                        .fillMaxWidth(0.5f)
                        .clickable {
                            clickedIndex = content.row
                            markedIndex = content.row
                            log("User Event", "Clicked", clickedIndex.toString())
                        }
                ) {
                    Text(
                        text = "${content.row}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        modifier = modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .wrapContentHeight()
                    )
                }
            }
        }
        Spacer(modifier = modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
        )
        Card (
            colors = if (verticalViewModel.targetIndex > cardsOnScreen * 2) {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            } else if (verticalViewModel.targetIndex > markedIndex) {
                CardDefaults.cardColors(Color.Cyan, Color.Black)
            } else if (verticalViewModel.targetIndex == markedIndex) {
                CardDefaults.cardColors(Color.Green, Color.Black)
            } else {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            },
            modifier = modifier
                .background(Color.DarkGray)
                .padding(6.dp)
                .fillMaxWidth()
                .clickable {
                    log("User Event", "DOWN clicked")
                    controlViewModel.controlUIState = ControlUiState.DOWN
                }
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Icon (
                    Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Keyboard arrow down",
                    modifier = modifier
                        .size(64.dp)
                )
            }
        }
        Spacer(modifier = modifier
            .height(6.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
        )
    }

    // Resets UI-state to IDLE after the event is handled and the input is worked out
    controlViewModel.controlUIState = ControlUiState.IDLE
}

private fun changeMarkedIndexByControl(controlUIState: ControlUiState) {
    // must between 1 and numberOfRows
    markedIndex += when (controlUIState) {
        ControlUiState.UP   -> -1
        ControlUiState.DOWN -> +1
        else                ->  0
    }
    if (markedIndex < 1 ) markedIndex = 1
    if (markedIndex > verticalNumber) markedIndex = verticalNumber
}

private fun resetUI(verticalViewModel: VerticalViewModel, coroutineScope: CoroutineScope, listState: LazyListState) {
    cardTopIndex = cardsOnScreen / 2
    markedIndex = cardsOnScreen
    verticalViewModel.targetIndex = 100
    coroutineScope.launch {
        listState.scrollToItem(index = cardsOnScreen / 2, scrollOffset = 0)
    }
}

private fun log(src : String, desc : String, index : String = "") {
    val isEvent = src in eventList

    if ( desc_old != desc || isEvent) {
        log.appendLog("Vertical", src, desc, index)
        desc_old = desc
    }
}