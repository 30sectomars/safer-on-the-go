package com.example.study.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val context: Context, val navController: NavHostController) {
    var waitTime : Long = 30000
    val balanceLatinSquare = BalanceLatinSquare()
    var participantID: String = DataSource().getParticipantIDs()[0]
    var selectionMethod: String = DataSource().getSelectionMethods()[0]
    var remoteMountingSide: String = DataSource().getRemoteMountingSides()[0]

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        val VERTICAL_NUMBER = intPreferencesKey("vertical_number")
        val HORIZONTAL_NUMBER = intPreferencesKey("horizontal_number")
        val TWOD_NUMBER_OF_ROWS = intPreferencesKey("2D_number_of_rows")
        val TWOD_NUMBER_OF_COLUMNS = intPreferencesKey("2D_number_of_columns")
        val WAIT_TIME = longPreferencesKey("wait_time")
    }

    val getVerticalNumber: Flow<Int?> = context.dataStore.data.map {
        it[VERTICAL_NUMBER] ?: 15
    }

    suspend fun saveVerticalNumber(number: Int) {
        context.dataStore.edit {
            it[VERTICAL_NUMBER] = number
        }
    }

    val getHorizontalNumber: Flow<Int?> = context.dataStore.data.map {
        it[HORIZONTAL_NUMBER] ?: 15
    }

    suspend fun saveHorizontalNumber(number: Int) {
        context.dataStore.edit {
            it[HORIZONTAL_NUMBER] = number
        }
    }

    val getTwoDNumberOfRows: Flow<Int?> = context.dataStore.data.map {
        it[TWOD_NUMBER_OF_ROWS] ?: 15
    }

    suspend fun saveTwoDNumberOfRows(number: Int) {
        context.dataStore.edit {
            it[TWOD_NUMBER_OF_ROWS] = number
        }
    }

    val getTwoDNumberOfColumns: Flow<Int?> = context.dataStore.data.map {
        it[TWOD_NUMBER_OF_COLUMNS] ?: 15
    }

    suspend fun saveTwoDNumberOfColumns(number: Int) {
        context.dataStore.edit {
            it[TWOD_NUMBER_OF_COLUMNS] = number
        }
    }

    val getWaitTime: Flow<Long?> = context.dataStore.data.map {
        it[WAIT_TIME] ?: (30 * 1000)
    }

    suspend fun saveWaitTime(number: Long) {
        waitTime = number
        context.dataStore.edit {
            it[WAIT_TIME] = number
        }
    }

}