package com.example.study.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.study.data.SettingsDataStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket

sealed interface ControlUiState {
    object IDLE : ControlUiState
    object UP : ControlUiState
    object DOWN : ControlUiState
    object LEFT : ControlUiState
    object RIGHT : ControlUiState
    object SELECT : ControlUiState
    data class ERROR(val msg: String) : ControlUiState
}

class ControlViewModel(settingsDataStore: SettingsDataStore) {
    /** The mutable State that stores the status of the most recent request */
    var controlUIState : ControlUiState by mutableStateOf(ControlUiState.IDLE)

    private lateinit var udpJob: Job

    init {
        getUDPMsg(settingsDataStore)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getUDPMsg(settingsDataStore: SettingsDataStore) {
        val socket = DatagramSocket(8888)
        val packet = DatagramPacket(ByteArray(1024), 1024)

        udpJob = GlobalScope.launch(Dispatchers.IO){
            while (udpJob.isActive) {
                val startTimer = System.currentTimeMillis()
                controlUIState = try {
                    socket.receive(packet)
                    val msg = String(packet.data, 0, packet.length)

                    // switch directions based on remote mounting side
                    if (settingsDataStore.remoteMountingSide == "Left" || settingsDataStore.selectionMethod == "Direct Touch")
                    {
                        when (msg) {
                            "Idle" -> ControlUiState.IDLE
                            "Up" -> ControlUiState.UP
                            "Down" -> ControlUiState.DOWN
                            "Left" -> ControlUiState.LEFT
                            "Right" -> ControlUiState.RIGHT
                            "Select" -> ControlUiState.SELECT
                            else -> ControlUiState.ERROR("No valid message received!")
                        }
                    }
                    else {
                        when (msg) {
                            "Idle" -> ControlUiState.IDLE
                            "Left" -> ControlUiState.DOWN
                            "Right" -> ControlUiState.UP
                            "Down" -> ControlUiState.RIGHT
                            "Up" -> ControlUiState.LEFT
                            "Select" -> ControlUiState.SELECT
                            else -> ControlUiState.ERROR("No valid message received!")
                        }
                    }
                } catch (e: Exception) {
                    ControlUiState.ERROR(e.message ?: "")
                }

                val processTime = System.currentTimeMillis() - startTimer
                val remainingTime = maxOf(0L, 150 - processTime)
                delay(remainingTime)
            }
            socket.close()
        }
    }
}