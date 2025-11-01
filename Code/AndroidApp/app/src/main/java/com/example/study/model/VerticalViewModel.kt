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

sealed interface VerticalUiState {
    data object INIT : VerticalUiState
    data object START : VerticalUiState
    data object WAIT : VerticalUiState
    data object RESET : VerticalUiState
    data object TARGET : VerticalUiState
    data object END : VerticalUiState
}

class VerticalViewModel ( logCSV : LogCSV) {
    private var targetDirectionsList : MutableList<String> = TargetDirections().targetDirectionsVertical.toMutableList()

    var targetIndex = 100
    var targetCounter = 0
    var targetTimeStart = System.currentTimeMillis()
    var targetDirection = targetDirectionsList[0]
    var waitTime : Long = 20000

    /** The mutable State that stores the status of the most recent request */
    var verticalUIState : VerticalUiState by mutableStateOf(VerticalUiState.INIT)

    private val log = logCSV

    init {
        loop()
    }

    private fun loop() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (verticalUIState == VerticalUiState.INIT) {
                    // Do nothing --> wait for start
                    delay(200)
                }
                if (verticalUIState == VerticalUiState.START) {
                    // wait for UI to reset
                    delay(2000)
                    targetDirectionsList.shuffle()
                    targetDirection = targetDirectionsList[targetCounter]
                    verticalUIState = VerticalUiState.WAIT
                }
                if (verticalUIState == VerticalUiState.WAIT) {
                    val waitRnd = (waitTime - 5000 .. waitTime + 5000).random()
                    delay(waitRnd)
                    verticalUIState = VerticalUiState.RESET
                }
                if (verticalUIState == VerticalUiState.RESET) {
                    targetDirection = targetDirectionsList[targetCounter]
                    delay(200)
                }
                if (verticalUIState == VerticalUiState.TARGET) {
                    val targetTimeActive = System.currentTimeMillis() - targetTimeStart
                    if ( targetTimeActive > waitTime ) {
                        log.appendLog("Vertical", "User Event", "No target selected", targetIndex.toString())
                        targetCounter++
                        verticalUIState = if ( targetCounter >= 8 ) {
                            VerticalUiState.END
                        } else {
                            VerticalUiState.WAIT
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
        verticalUIState = VerticalUiState.INIT
    }
}