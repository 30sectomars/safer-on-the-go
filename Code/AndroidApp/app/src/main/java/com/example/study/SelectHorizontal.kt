package com.example.study

import android.annotation.SuppressLint
import android.media.RingtoneManager
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
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
import com.example.study.model.HorizontalUiState
import com.example.study.model.HorizontalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val cardsOnScreen = 9 // number of card which can be seen on screen

private var horizontalNumber = 17
private var markedIndex = cardsOnScreen
private var cardTopIndex = cardsOnScreen / 2       // Index of the left list element for scrolling

private lateinit var log : LogCSV
private var desc_old = ""
private var eventList = listOf("User Event", "Control Event", "App Event")

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SelectHorizontalApp(controlViewModel: ControlViewModel, horizontalViewModel : HorizontalViewModel, settingsDataStore: SettingsDataStore, logCSV : LogCSV, modifier: Modifier = Modifier) {
    log = logCSV

    // Read current control state
    // Control events coming from the Arduino device
    val controlUIState = controlViewModel.controlUIState

    // Read the number of columns from the settings datastore
    val numberOfColumnsSettings by settingsDataStore.getHorizontalNumber.collectAsState(initial = 17)
    horizontalNumber = numberOfColumnsSettings ?: 17

    // Read the wait time from the settings datastore
    val waitTimeSettings by settingsDataStore.getWaitTime.collectAsState(initial = 20000)
    val waitTime = waitTimeSettings ?: 20000
    horizontalViewModel.waitTime = waitTime

    // Get a predefined array to generate the list automatically
    val list = DataSource().loadColumnData(horizontalNumber)

    // Remember variables over recomposition
    var clickedIndex by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Evaluate select event
    if (controlUIState == ControlUiState.SELECT) {
        clickedIndex = markedIndex
        log("Control Event", "SELECT", clickedIndex.toString())
    }
    if (clickedIndex == horizontalViewModel.targetIndex && horizontalViewModel.horizontalUIState == HorizontalUiState.TARGET) {
        log("App Event", "Target selected")
        // deactivate the destination index by setting it on a high value
        horizontalViewModel.targetIndex = 100
        // Add 1 to target counter for next target direction
        horizontalViewModel.targetCounter++
        // If all 8 directions are done, go to the end state
        horizontalViewModel.horizontalUIState = if ( horizontalViewModel.targetCounter >= 8 ) HorizontalUiState.END else HorizontalUiState.WAIT
    }

    // State machine changes
    if ( horizontalViewModel.horizontalUIState == HorizontalUiState.END ) {
        log("State", "END")
        log("App Event", "Horizontal assignment finished")
        Toast.makeText(context, "Congratulations! You have passed the horizontal assignment!", Toast.LENGTH_SHORT).show()
        horizontalViewModel.resetModel()
        // go to next assignment or home
        if ( !settingsDataStore.balanceLatinSquare.walkthrough.isEmpty() ) {
            val route = when (settingsDataStore.balanceLatinSquare.walkthrough.removeFirstOrNull()) {
                "Vertical" -> NavDrawerItem.Vertical.route
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
    if ( horizontalViewModel.horizontalUIState == HorizontalUiState.INIT ) {
        log("State", "INIT")
        resetUI(horizontalViewModel, coroutineScope, listState)
        horizontalViewModel.horizontalUIState = HorizontalUiState.START
    }
    if ( horizontalViewModel.horizontalUIState == HorizontalUiState.START ) {
        log("State", "START")
    }
    if ( horizontalViewModel.horizontalUIState == HorizontalUiState.WAIT ) {
        log("State", "WAIT")
        horizontalViewModel.targetIndex = 100
        clickedIndex = 101
    }
    if ( horizontalViewModel.horizontalUIState == HorizontalUiState.RESET ) {
        // play notification sound
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        resetUI(horizontalViewModel, coroutineScope, listState)
        horizontalViewModel.targetTimeStart = System.currentTimeMillis()
        horizontalViewModel.horizontalUIState = HorizontalUiState.TARGET
        horizontalViewModel.targetIndex = when ( horizontalViewModel.targetDirection ) {
            "Left_out" -> (1 .. (cardsOnScreen/2)).random()
            "Left" -> ((cardsOnScreen / 2) + 1 until cardsOnScreen ).random()
            "Right" -> (cardsOnScreen + 1 .. cardsOnScreen + (cardsOnScreen / 2) ).random()
            "Right_out" -> ((cardsOnScreen + (cardsOnScreen / 2) + 1) until cardsOnScreen * 2).random()
            else -> (1 .. cardsOnScreen * 2).random()
        }
        log("State", "RESET")
        log("App Event", "Target direction[" + horizontalViewModel.targetDirection + "]", horizontalViewModel.targetIndex.toString())
    }

    // change the indicated position of the marked card and scroll the list if needed
    changeMarkedIndexByControl(controlUIState)
    coroutineScope.launch {
        if ( controlUIState == ControlUiState.LEFT ) {
            log("Control Event", "LEFT", markedIndex.toString())
            if ( markedIndex < cardTopIndex + 1) {
                listState.scrollToItem(index = markedIndex - 1, scrollOffset = 0)
                cardTopIndex -= 1
            }
        }
        if ( controlUIState == ControlUiState.RIGHT ) {
            log("Control Event", "RIGHT", markedIndex.toString())
            if ( markedIndex > cardTopIndex + cardsOnScreen ) {
                listState.scrollToItem(index = cardTopIndex + 1, scrollOffset = 0)
                cardTopIndex += 1
            }
        }
    }

    // Top Row element which is the frame of the layout
    Row (
        modifier = modifier.padding(top = 60.dp)
    ) {
        Spacer(modifier = modifier
            .width(3.dp)
            .fillMaxHeight()
            .background(Color.DarkGray)
        )
        Card (
            colors = if (horizontalViewModel.targetIndex > cardsOnScreen * 2) {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            } else if (horizontalViewModel.targetIndex < markedIndex) {
                CardDefaults.cardColors(Color.Cyan, Color.Black)
            } else if (horizontalViewModel.targetIndex == markedIndex) {
                CardDefaults.cardColors(Color.Green, Color.Black)
            } else {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            },
            modifier = modifier
                .background(Color.DarkGray)
                .padding(6.dp)
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
            .width(3.dp)
            .fillMaxHeight()
            .background(Color.DarkGray)
        )
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .background(Color.DarkGray)
                .weight(1.0f)
        ) {
            items(list) {content ->
                Card(
                    border = when(content.column) {
                        markedIndex -> BorderStroke(8.dp, Color.Red)
                        horizontalViewModel.targetIndex -> BorderStroke(2.dp, Color.Cyan)
                        else -> BorderStroke(2.dp, Color.LightGray)
                    },
                    colors = if (horizontalViewModel.targetIndex == content.column) {
                        CardDefaults.cardColors(Color.Cyan, Color.Black)
                    } else {
                        CardDefaults.cardColors(Color.LightGray, Color.Black)
                    },
                    modifier = modifier
                        .fillMaxHeight(0.75f)
                        .clickable {
                            clickedIndex = content.column
                            markedIndex = content.column
                            log("User Event", "Clicked", clickedIndex.toString())
                        }
                ) {
                    Text(
                        text = "${content.column}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        modifier = modifier
                            .fillMaxHeight()
                            .width(64.dp)
                            .wrapContentHeight()
                    )
                }
            }
        }
        Spacer(modifier = modifier
            .width(3.dp)
            .fillMaxHeight()
            .background(Color.DarkGray)
        )
        Card (
            colors = if (horizontalViewModel.targetIndex > cardsOnScreen * 2) {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            } else if (horizontalViewModel.targetIndex > markedIndex) {
                CardDefaults.cardColors(Color.Cyan, Color.Black)
            } else if (horizontalViewModel.targetIndex == markedIndex) {
                CardDefaults.cardColors(Color.Green, Color.Black)
            } else {
                CardDefaults.cardColors(Color.LightGray, Color.Black)
            },
            modifier = modifier
                .background(Color.DarkGray)
                .padding(6.dp)
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
            .width(3.dp)
            .fillMaxHeight()
            .background(Color.DarkGray)
        )
    }

    // Resets UI-state to IDLE after the event is handled and the input is worked out
    controlViewModel.controlUIState = ControlUiState.IDLE
}

private fun changeMarkedIndexByControl(controlUiState: ControlUiState) {
    // must between 1 and numberOfColumns
    markedIndex += when (controlUiState) {
        ControlUiState.LEFT   -> -1
        ControlUiState.RIGHT  -> +1
        else                  ->  0
    }
    if (markedIndex < 1 ) markedIndex = 1
    if (markedIndex > horizontalNumber) markedIndex = horizontalNumber
}

private fun resetUI(horizontalViewModel: HorizontalViewModel, coroutineScope: CoroutineScope, listState: LazyListState) {
    cardTopIndex = cardsOnScreen / 2
    markedIndex = cardsOnScreen
    horizontalViewModel.targetIndex = 100
    coroutineScope.launch {
        listState.scrollToItem(index = cardsOnScreen / 2, scrollOffset = 0)
    }
}

private fun log(src : String, desc : String, index : String = "") {
    val isEvent = src in eventList

    if ( desc_old != desc || isEvent) {
        log.appendLog("Horizontal", src, desc, index)
        desc_old = desc
    }
}