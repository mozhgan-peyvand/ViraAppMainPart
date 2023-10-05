package ir.part.app.intelligentassistant.features.ava_negar.data

import androidx.datastore.preferences.core.booleanPreferencesKey

object PreferencesKey {
    val mainOnBoardingKey = booleanPreferencesKey(name = "main_on_boarding_Key")
    val onBoardingKey = booleanPreferencesKey(name = "on_boarding_completed")
}