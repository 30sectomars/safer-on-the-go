package com.example.study

import android.annotation.SuppressLint
import android.media.RingtoneManager
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
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
import com.example.study.model.TwoDViewModel
import com.example.study.model.UiState2D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val rowCardsOnScreen = 7                  // number of card which can be seen on row
private const val columnCardsOnScreen = 5               // number of card which can be seen on column

private var numberOfRows = 13
private var numberOfColumns = 9
private var markedRowIndex = numberOfRows / 2
private var markedColumnIndex = numberOfColumns / 2
private var scrollRowPosition = numberOfRows / 4        // Index of the top list element for scrolling
private var scrollColumnPosition = numberOfColumns / 4  // Index of the left list element for scrolling

private lateinit var log : LogCSV
private var desc_old = ""
private var eventList = listOf("User Event", "Control Event", "App Event")

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Select2DApp(viewModel2D: TwoDViewModel, controlViewModel: ControlViewModel, settingsDataStore: SettingsDataStore, logCSV: LogCSV, modifier: Modifier = Modifier) {
    log = logCSV

    // Read current control state
    // Control events coming from the Arduino device
    val controlUIState = controlViewModel.controlUIState

    // Read the numbers of rows and columns from the settings datastore
    val numberOfRowsSettings by settingsDataStore.getTwoDNumberOfRows.collectAsState(initial = 13)
    val numberOfColumnsSettings by settingsDataStore.getTwoDNumberOfColumns.collectAsState(initial = 9)
    numberOfRows = numberOfRowsSettings ?: 13
    numberOfColumns = numberOfColumnsSettings ?: 9

    // Read the wait time from the settings datastore
    val waitTimeSettings by settingsDataStore.getWaitTime.collectAsState(initial = 20000)
    val waitTime = waitTimeSettings ?: 20000
    viewModel2D.waitTime = waitTime

    // Get a predefined 2D array to generate the lists automatically
    val colList = DataSource().loadColumnData(numberOfColumns)
    val rowList = DataSource().loadRowData(numberOfRows)

    // Remember variables over recomposition
    var clickedRowIndex by remember { mutableIntStateOf(0) }
    var clickedColumnIndex by remember { mutableIntStateOf(0) }
    val listColumnState = rememberLazyListState()
    val listRowState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Evaluate select event
    if ( controlUIState == ControlUiState.SELECT ) {
        clickedRowIndex = markedRowIndex
        clickedColumnIndex = markedColumnIndex
        log("Control Event", "SELECT", "$clickedRowIndex / $clickedColumnIndex")
    }
    if ( clickedRowIndex == viewModel2D.targetRowIndex && clickedColumnIndex == viewModel2D.targetColumnIndex && viewModel2D.uiState2D == UiState2D.TARGET ) {
        log("App Event", "Target selected")
        // deactivate the destination index by setting it on a high value
        viewModel2D.targetRowIndex = 100
        viewModel2D.targetColumnIndex = 100
        viewModel2D.targetCounter++
        viewModel2D.uiState2D = if (viewModel2D.targetCounter >= 8) UiState2D.END else UiState2D.WAIT
    }

    // State machine changes
    if ( viewModel2D.uiState2D == UiState2D.END ) {
        log("State", "END")
        log("App Event", "2D assignment finished")
        Toast.makeText(context, "Congratulations! You have passed the 2D assignment!", Toast.LENGTH_SHORT).show()
        viewModel2D.resetModel()
        // go to next assignment or home
        if ( !settingsDataStore.balanceLatinSquare.walkthrough.isEmpty() ) {
            val route = when (settingsDataStore.balanceLatinSquare.walkthrough.removeFirstOrNull()) {
                "Vertical" -> NavDrawerItem.Vertical.route
                "Horizontal" -> NavDrawerItem.Horizontal.route
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
    if ( viewModel2D.uiState2D == UiState2D.INIT ) {
        log("State", "INIT")
        resetUI(viewModel2D, coroutineScope, listRowState, listColumnState)
        viewModel2D.uiState2D = UiState2D.START
    }
    if ( viewModel2D.uiState2D == UiState2D.START ) {
        log("State", "START")
    }
    if ( viewModel2D.uiState2D == UiState2D.WAIT ) {
        log("State", "WAIT")
        viewModel2D.targetRowIndex = 100
        viewModel2D.targetColumnIndex = 100
        clickedRowIndex = 101
        clickedColumnIndex = 101
    }
    if ( viewModel2D.uiState2D == UiState2D.RESET ) {
        // play notification sound
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        resetUI(viewModel2D, coroutineScope, listRowState, listColumnState)
        viewModel2D.targetTimeStart = System.currentTimeMillis()
        viewModel2D.uiState2D = UiState2D.TARGET
        viewModel2D.targetRowIndex = when ( viewModel2D.targetDirection ) {
            "Up_out_left" -> (1 .. (rowCardsOnScreen /2)).random()
            "Up_out_right" -> (1 .. (rowCardsOnScreen /2)).random()
            "Up_left" -> ((rowCardsOnScreen / 2) + 1 until rowCardsOnScreen).random()
            "Up_right" -> ((rowCardsOnScreen / 2) + 1 until rowCardsOnScreen).random()
            "Down_left" -> (rowCardsOnScreen + 1 .. rowCardsOnScreen + (rowCardsOnScreen / 2) ).random()
            "Down_right" -> (rowCardsOnScreen + 1 .. rowCardsOnScreen + (rowCardsOnScreen / 2) ).random()
            "Down_out_left" -> ((rowCardsOnScreen + (rowCardsOnScreen / 2) + 1) until rowCardsOnScreen * 2).random()
            "Down_out_right" -> ((rowCardsOnScreen + (rowCardsOnScreen / 2) + 1) until rowCardsOnScreen * 2).random()
            else -> (1 .. rowCardsOnScreen * 2).random()
        }
        viewModel2D.targetColumnIndex = when ( viewModel2D.targetDirection ) {
            "Up_out_left" -> (1 .. (columnCardsOnScreen /2)).random()
            "Down_out_left" -> (1 .. (columnCardsOnScreen /2)).random()
            "Up_left" -> ((columnCardsOnScreen / 2) + 1 until columnCardsOnScreen).random()
            "Down_left" -> ((columnCardsOnScreen / 2) + 1 until columnCardsOnScreen).random()
            "Up_right" -> (columnCardsOnScreen + 1 .. columnCardsOnScreen + (columnCardsOnScreen / 2) ).random()
            "Down_right" -> (columnCardsOnScreen + 1 .. columnCardsOnScreen + (columnCardsOnScreen / 2) ).random()
            "Up_out_right" -> ((columnCardsOnScreen + (columnCardsOnScreen / 2) + 1) until columnCardsOnScreen * 2).random()
            "Down_out_right" -> ((columnCardsOnScreen + (columnCardsOnScreen / 2) + 1) until columnCardsOnScreen * 2).random()
            else -> (1 .. columnCardsOnScreen * 2).random()
        }
        log("State", "RESET")
        log("App Event", "Target direction[" + viewModel2D.targetDirection + "]", viewModel2D.targetRowIndex.toString() + " / " + viewModel2D.targetColumnIndex.toString())
    }

    // change the indicated position of the marked card and scroll the list if needed
    changeMarkedIndexByControl(controlUIState)
    coroutineScope.launch {
        if ( controlUIState == ControlUiState.LEFT ) {
            log("Control Event", "LEFT", "$markedRowIndex / $markedColumnIndex" )
            if ( markedColumnIndex < scrollColumnPosition + 1) {
                listColumnState.scrollToItem(index = markedColumnIndex - 1, scrollOffset = 0)
                scrollColumnPosition--
            }
        }
        if ( controlUIState == ControlUiState.RIGHT ) {
            log("Control Event", "RIGHT", "$markedRowIndex / $markedColumnIndex")
            if ( markedColumnIndex > scrollColumnPosition + columnCardsOnScreen) {
                listColumnState.scrollToItem(index = scrollColumnPosition + 1, scrollOffset = 0)
                scrollColumnPosition++
            }
        }
        if ( controlUIState == ControlUiState.UP ) {
            log("Control Event", "UP", "$markedRowIndex / $markedColumnIndex")
            if ( markedRowIndex < scrollRowPosition + 1) {
                listRowState.scrollToItem(index = markedRowIndex - 1, scrollOffset = 0)
                scrollRowPosition--
            }
        }
        if ( controlUIState == ControlUiState.DOWN ) {
            log("Control Event", "DOWN", "$markedRowIndex / $markedColumnIndex")
            if ( markedRowIndex > scrollRowPosition + rowCardsOnScreen) {
                listRowState.scrollToItem(index = scrollRowPosition + 1, scrollOffset = 0)
                scrollRowPosition++
            }
        }
    }

    Column (
        modifier = modifier.padding(top = 60.dp)
    ) {
        Spacer(modifier = modifier
            .height(6.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
        )
        Card (
            colors = if (viewModel2D.targetRowIndex > rowCardsOnScreen * 2) {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            } else if (viewModel2D.targetRowIndex < markedRowIndex) {
                CardDefaults.cardColors(Color.Cyan, Color.Black)
            } else if (viewModel2D.targetRowIndex == markedRowIndex) {
                CardDefaults.cardColors(Color.Green, Color.Black)
            } else {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            },
            modifier = modifier
                .background(Color.DarkGray)
                .padding(horizontal = 4.dp)
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
            .height(6.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
        )
        Row (
            modifier = modifier
                .background(Color.DarkGray)
                .weight(1.0f)
        ){
            Spacer(modifier = modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(Color.DarkGray)
            )
            Card (
                colors = if (viewModel2D.targetColumnIndex > columnCardsOnScreen * 2) {
                    CardDefaults.cardColors(Color.LightGray, Color.Black)
                } else if (viewModel2D.targetColumnIndex < markedColumnIndex) {
                    CardDefaults.cardColors(Color.Cyan, Color.Black)
                } else if (viewModel2D.targetColumnIndex == markedColumnIndex) {
                    CardDefaults.cardColors(Color.Green, Color.Black)
                } else {
                    CardDefaults.cardColors(Color.LightGray, Color.Black)
                },
                modifier = modifier
                    .background(Color.DarkGray)
                    .fillMaxHeight()
                    .clickable {
                        log("User Event", "LEFT clicked")
                        controlViewModel.controlUIState = ControlUiState.LEFT
                    }
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .fillMaxHeight()
                ) {
                    Icon (
                        Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = "Keyboard arrow left",
                        modifier = modifier
                            .size(64.dp)
                    )
                }
            }
            Spacer(modifier = modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(Color.DarkGray)
            )
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                state = listColumnState,
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
                    .weight(1.0f)
            ) {
                items(colList) { colList ->
                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        state = listRowState,
                        modifier = modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                            .weight(1.0f)
                    ){
                        items(rowList) {rowList ->
                            Card(
                                border = if (markedRowIndex == rowList.row && markedColumnIndex == colList.column) {
                                    BorderStroke(8.dp, Color.Red)
                                } else if (viewModel2D.targetRowIndex == rowList.row && viewModel2D.targetColumnIndex == colList.column) {
                                    BorderStroke(8.dp, Color.Cyan)
                                } else if (viewModel2D.targetRowIndex == rowList.row || viewModel2D.targetColumnIndex == colList.column) {
                                    BorderStroke(8.dp, Color.Cyan)
                                } else {
                                    BorderStroke(8.dp, Color.LightGray)
                                },
                                colors = if (viewModel2D.targetRowIndex == rowList.row && viewModel2D.targetColumnIndex == colList.column) {
                                    CardDefaults.cardColors(Color.Cyan, Color.Black)
                                } else if (viewModel2D.targetRowIndex == rowList.row) {
                                    CardDefaults.cardColors(Color.LightGray, Color.Black)
                                } else if (viewModel2D.targetColumnIndex == colList.column) {
                                    CardDefaults.cardColors(Color.LightGray, Color.Black)
                                } else {
                                    CardDefaults.cardColors(Color.LightGray, Color.Black)
                                },
                                modifier = modifier
                                    .fillMaxHeight()
                                    .padding(2.dp)
                                    .clickable {
                                        clickedRowIndex = rowList.row
                                        clickedColumnIndex = colList.column
                                        markedRowIndex = rowList.row
                                        markedColumnIndex = colList.column
                                        log("User Event", "Clicked", "$markedRowIndex / $markedColumnIndex")
                                    }
                            ) {
                                Text(
                                    text = "${rowList.row} - ${colList.column}",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray,
                                    modifier = modifier
                                        .height(142.dp)
                                        .width(122.dp)
                                        .wrapContentHeight()
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(Color.DarkGray)
            )
            Card (
                colors = if (viewModel2D.targetColumnIndex > columnCardsOnScreen * 2) {
                    CardDefaults.cardColors(Color.LightGray, Color.Black)
                } else if (viewModel2D.targetColumnIndex > markedColumnIndex) {
                    CardDefaults.cardColors(Color.Cyan, Color.Black)
                } else if (viewModel2D.targetColumnIndex == markedColumnIndex) {
                    CardDefaults.cardColors(Color.Green, Color.Black)
                } else {
                    CardDefaults.cardColors(Color.LightGray, Color.Black)
                },
                modifier = modifier
                    .background(Color.DarkGray)
                    .fillMaxHeight()
                    .clickable {
                        log("User Event", "RIGHT clicked")
                        controlViewModel.controlUIState = ControlUiState.RIGHT
                    }
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .fillMaxHeight()
                ) {
                    Icon (
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Keyboard arrow right",
                        modifier = modifier
                            .size(64.dp)
                    )
                }
            }
            Spacer(modifier = modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(Color.DarkGray)
            )
        }
        Spacer(modifier = modifier
            .height(6.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
        )
        Card (
            colors = if (viewModel2D.targetRowIndex > rowCardsOnScreen * 2) {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            } else if (viewModel2D.targetRowIndex > markedRowIndex) {
                CardDefaults.cardColors(Color.Cyan, Color.Black)
            } else if (viewModel2D.targetRowIndex == markedRowIndex) {
                CardDefaults.cardColors(Color.Green, Color.Black)
            } else {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            },
            modifier = modifier
                .background(Color.DarkGray)
                .padding(horizontal = 4.dp)
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
    markedRowIndex += when (controlUIState) {
        ControlUiState.UP   -> -1
        ControlUiState.DOWN -> +1
        else                ->  0
    }
    if (markedRowIndex <= 0 ) markedRowIndex = 1
    if (markedRowIndex > numberOfRows) markedRowIndex = numberOfRows

    // must between 1 and numberOfColumns
    markedColumnIndex += when (controlUIState) {
        ControlUiState.LEFT    -> -1
        ControlUiState.RIGHT   -> +1
        else                   ->  0
    }
    if (markedColumnIndex <= 0 ) markedColumnIndex = 1
    if (markedColumnIndex > numberOfColumns) markedColumnIndex = numberOfColumns
}

private fun resetUI(viewModel2D: TwoDViewModel, coroutineScope: CoroutineScope, listRowState: LazyListState, listColumnState: LazyListState) {
    scrollRowPosition = numberOfRows / 4        // Index of the top list element for scrolling
    scrollColumnPosition = numberOfColumns / 4  // Index of the left list element for scrolling
    markedRowIndex = rowCardsOnScreen
    markedColumnIndex = columnCardsOnScreen
    viewModel2D.targetRowIndex = 100
    viewModel2D.targetColumnIndex = 100
    coroutineScope.launch {
        listRowState.scrollToItem(index = rowCardsOnScreen / 2, scrollOffset = 0)
        listColumnState.scrollToItem(index = columnCardsOnScreen / 2, scrollOffset = 0)
    }
}

private fun log(src : String, desc : String, index : String = "") {
    val isEvent = src in eventList

    if ( desc_old != desc || isEvent) {
        log.appendLog("2D", src, desc, index)
        desc_old = desc
    }
}