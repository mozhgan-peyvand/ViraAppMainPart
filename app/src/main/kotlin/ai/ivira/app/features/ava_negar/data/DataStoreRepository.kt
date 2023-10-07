package ai.ivira.app.features.ava_negar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "on_boarding_pref")

class DataStoreRepository(context: Context) {
    private val dataStore = context.dataStore

    suspend fun saveOnBoardingState(completed: Boolean, key: Preferences.Key<Boolean>) {
        dataStore.edit { preferences ->
            preferences[key] = completed
        }
    }

    fun readOnBoardingState(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val onBoardingState =
                    preferences[key] ?: false
                onBoardingState
            }
    }
}