package com.example.study


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.study.model.ControlUiState

private var udp_log = ""

@Composable
fun UDP_Log(controlUIState: ControlUiState, modifier: Modifier = Modifier) {
    val msg = when (controlUIState) {
        is ControlUiState.IDLE -> "Idle"
        is ControlUiState.UP -> "Up"
        is ControlUiState.DOWN -> "Down"
        is ControlUiState.LEFT -> "Left"
        is ControlUiState.RIGHT -> "Right"
        is ControlUiState.SELECT -> "Select"
        is ControlUiState.ERROR -> controlUIState.msg
    }

    udp_log = msg + "\n" + udp_log

    Column {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = modifier.height(96.dp))
            Text(
                text = "UDP_Log",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            //Spacer(modifier = modifier.height(12.dp))
        }
        Text(
            text = udp_log,
            modifier = modifier.padding(start = 40.dp)
        )
    }
}