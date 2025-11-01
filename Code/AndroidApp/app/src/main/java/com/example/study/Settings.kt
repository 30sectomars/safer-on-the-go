package com.example.study

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.study.data.SettingsDataStore
import com.example.study.model.HorizontalViewModel
import com.example.study.model.TwoDViewModel
import com.example.study.model.VerticalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Settings(verticalViewModel: VerticalViewModel, horizontalViewModel: HorizontalViewModel, viewModel2D: TwoDViewModel, settingsDataStore: SettingsDataStore, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val initVertical by settingsDataStore.getVerticalNumber.collectAsState(initial = 15)
    val initHorizontal by settingsDataStore.getHorizontalNumber.collectAsState(initial = 15)
    val initTwoDRows by settingsDataStore.getTwoDNumberOfRows.collectAsState(initial = 15)
    val initTwoDColumns by settingsDataStore.getTwoDNumberOfColumns.collectAsState(initial = 15)
    val initWaitTime by settingsDataStore.getWaitTime.collectAsState(initial = 30000)

    var verticalNumber by remember (initVertical) { mutableStateOf( initVertical.toString() )}
    var horizontalNumber by remember (initHorizontal) { mutableStateOf( initHorizontal.toString() )}
    var twoDNumberOfRows by remember (initTwoDRows) { mutableStateOf( initTwoDRows.toString() )}
    var twoDNumberOfColumns by remember (initTwoDColumns) { mutableStateOf( initTwoDColumns.toString() )}
    var waitTime by remember (initWaitTime) { mutableStateOf( ((initWaitTime ?: 30000).toFloat() / 1000).toString() )}

    Column {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = modifier.height(96.dp))
            Text(
                text = "Settings",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = modifier.height(12.dp))
            Text(
                text = "Here you can set up the number of rows and columns:",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = modifier.height(16.dp))
            Row {
                Text(
                    text = "Vertical: ",
                    modifier = Modifier
                        .width(128.dp)
                        .wrapContentSize()
                        .align(CenterVertically)
                )
                TextField(
                    value = verticalNumber,
                    modifier = Modifier.width(200.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { verticalNumber = it }
                )
            }
            Spacer(modifier = modifier.height(4.dp))
            Row {
                Text(
                    text = "Horizontal: ",
                    modifier = Modifier
                        .width(128.dp)
                        .wrapContentSize()
                        .align(CenterVertically)
                )
                TextField(
                    value = horizontalNumber,
                    modifier = Modifier.width(200.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { horizontalNumber = it }
                )
            }
            Spacer(modifier = modifier.height(4.dp))
            Row {
                Text(
                    text = "2D Rows: ",
                    modifier = Modifier
                        .width(128.dp)
                        .wrapContentSize()
                        .align(CenterVertically)
                )
                TextField(
                    value = twoDNumberOfRows,
                    modifier = Modifier.width(200.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { twoDNumberOfRows = it }
                )
            }
            Spacer(modifier = modifier.height(4.dp))
            Row {
                Text(
                    text = "2D Columns: ",
                    modifier = Modifier
                        .width(128.dp)
                        .wrapContentSize()
                        .align(CenterVertically)
                )
                TextField(
                    value = twoDNumberOfColumns,
                    modifier = Modifier.width(200.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { twoDNumberOfColumns = it }
                )
            }
            Spacer(modifier = modifier.height(4.dp))
            Row {
                Text(
                    text = "Wait time [s]: ",
                    modifier = Modifier
                        .width(128.dp)
                        .wrapContentSize()
                        .align(CenterVertically)
                )
                TextField(
                    value = waitTime,
                    modifier = Modifier.width(200.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { waitTime = it }
                )
            }
            Spacer(modifier = modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        verticalViewModel.resetModel()
                        horizontalViewModel.resetModel()
                        viewModel2D.resetModel()
                        Toast.makeText(context, "UI states reset!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = "Reset UI states!")
                }
                Spacer(modifier = modifier.width(4.dp))
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            settingsDataStore.saveVerticalNumber(verticalNumber.toInt())
                            settingsDataStore.saveHorizontalNumber(horizontalNumber.toInt())
                            settingsDataStore.saveTwoDNumberOfRows(twoDNumberOfRows.toInt())
                            settingsDataStore.saveTwoDNumberOfColumns(twoDNumberOfColumns.toInt())
                            settingsDataStore.saveWaitTime((waitTime.toFloat() * 1000).toLong())
                        }
                        Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = "Save settings!")
                }
            }
        }
    }
}