package com.example.study.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.study.data.LogCSV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface HorizontalUiState {
    data object INIT : HorizontalUiState
    data object START : HorizontalUiState
    data object WAIT : HorizontalUiState
    data object RESET : HorizontalUiState
    data object TARGET : HorizontalUiState
    data object END : HorizontalUiState
}

class HorizontalViewModel ( logCSV : LogCSV ) {
    private var targetDirectionsList : MutableList<String> = TargetDirections().targetDirectionsHorizontal.toMutableList()

    var targetCounter = 0
    var targetIndex = 100
    var targetTimeStart = System.currentTimeMillis()
    var targetDirection = targetDirectionsList[0]
    var waitTime : Long = 20000

    /** The mutable State that stores the status of the most recent request */
    var horizontalUIState : HorizontalUiState by mutableStateOf(HorizontalUiState.INIT)

    private val log = logCSV

    init {
        loop()
    }

    private fun loop() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (horizontalUIState == HorizontalUiState.INIT) {
                    // Do nothing --> wait for start
                    delay(200)
                }
                if (horizontalUIState == HorizontalUiState.START) {
                    // wait for UI to reset
                    delay(2000)
                    targetDirectionsList.shuffle()
                    targetDirection = targetDirectionsList[targetCounter]
                    horizontalUIState = HorizontalUiState.WAIT
                }
                if (horizontalUIState == HorizontalUiState.WAIT) {
                    val waitRnd = (waitTime - 5000 .. waitTime + 5000).random()
                    delay(waitRnd)
                    horizontalUIState = HorizontalUiState.RESET
                }
                if (horizontalUIState == HorizontalUiState.RESET) {
                    targetDirection = targetDirectionsList[targetCounter]
                    delay(200)
                }
                if (horizontalUIState == HorizontalUiState.TARGET) {
                    val targetTimeActive = System.currentTimeMillis() - targetTimeStart
                    if ( targetTimeActive > waitTime ) {
                        log.appendLog("Horizontal", "User Event", "No target selected", targetIndex.toString())
                        targetCounter++
                        horizontalUIState = if ( targetCounter >= 8 ) {
                            HorizontalUiState.END
                        } else {
                            HorizontalUiState.WAIT
                        }
                    }
                }

                delay(200)
            }
        }
    }

    fun resetModel() {
        targetCounter = 0
        targetIndex = 100
        horizontalUIState = HorizontalUiState.INIT
    }
}