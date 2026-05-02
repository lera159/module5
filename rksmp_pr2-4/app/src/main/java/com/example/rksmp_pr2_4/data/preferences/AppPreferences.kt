package com.example.rksmp_pr2_4.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppPreferences(private val context: Context) {

	companion object {
		val COMPLETED_COLOR_ENABLED = booleanPreferencesKey("completed_color_enabled")
		val COMPLETED_BACKGROUND_COLOR = longPreferencesKey("completed_background_color")
	}

	val isCompletedColorEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
		preferences[COMPLETED_COLOR_ENABLED] ?: true
	}

	val completedBackgroundColor: Flow<Long> = context.dataStore.data.map { preferences ->
		preferences[COMPLETED_BACKGROUND_COLOR] ?: 0xFF4CAF50
	}

	suspend fun setCompletedColorEnabled(enabled: Boolean) {
		context.dataStore.edit { preferences ->
			preferences[COMPLETED_COLOR_ENABLED] = enabled
		}
	}

	suspend fun setCompletedBackgroundColor(color: Long) {
		context.dataStore.edit { preferences ->
			preferences[COMPLETED_BACKGROUND_COLOR] = color
		}
	}
}