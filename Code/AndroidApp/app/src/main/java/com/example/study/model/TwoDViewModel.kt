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

sealed interface UiState2D {
    data object INIT : UiState2D
    data object START : UiState2D
    data object WAIT : UiState2D
    data object RESET : UiState2D
    data object TARGET : UiState2D
    data object END : UiState2D
}

class TwoDViewModel ( logCSV : LogCSV ) {
    private var targetDirectionsList : MutableList<String> = TargetDirections().targetDirections2D.toMutableList()

    var targetCounter = 0
    var targetRowIndex = 100
    var targetColumnIndex = 100
    var targetTimeStart = System.currentTimeMillis()
    var targetDirection = targetDirectionsList[0]
    var waitTime : Long = 20000

    /** The mutable State that stores the status of the most recent request */
    var uiState2D : UiState2D by mutableStateOf(UiState2D.INIT)

    private val log = logCSV

    init {
        loop()
    }

    private fun loop() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (uiState2D == UiState2D.INIT) {
                    // Do nothing --> wait for start
                    delay(200)
                }
                if (uiState2D == UiState2D.START) {
                    // wait for UI to reset
                    delay(2000)
                    targetDirectionsList.shuffle()
                    targetDirection = targetDirectionsList[targetCounter]
                    uiState2D = UiState2D.WAIT
                }
                if (uiState2D == UiState2D.WAIT) {
                    val waitRnd = (waitTime - 5000 .. waitTime + 5000).random()
                    delay(waitRnd)
                    uiState2D = UiState2D.RESET
                }
                if (uiState2D == UiState2D.RESET) {
                    targetDirection = targetDirectionsList[targetCounter]
                    delay(200)
                }
                if (uiState2D == UiState2D.TARGET) {
                    val targetTimeActive = System.currentTimeMillis() - targetTimeStart
                    if ( targetTimeActive > waitTime ) {
                        log.appendLog("2D", "User Event", "No target selected", "$targetRowIndex / $targetColumnIndex")
                        targetCounter++
                        uiState2D = if ( targetCounter >= 8 ) {
                            UiState2D.END
                        } else {
                            UiState2D.WAIT
                        }
                    }
                }

                delay(200)
            }
        }
    }

    fun resetModel() {
        targetCounter = 0
        targetRowIndex = 100
        targetColumnIndex = 100
        uiState2D = UiState2D.INIT
    }
}