package com.victorkirui.meetnote.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension to create the DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val PROFILE_SETUP_KEY = booleanPreferencesKey("is_profile_setup_complete")
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[ONBOARDING_COMPLETED_KEY] != true }


    val isProfileSetupComplete: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PROFILE_SETUP_KEY] ?: false }


    suspend fun markFirstLaunchComplete() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    suspend fun markProfileSetupComplete() {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_SETUP_KEY] = true
        }
    }
}