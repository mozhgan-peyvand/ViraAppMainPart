package ai.ivira.app.features.login.data

import ai.ivira.app.utils.common.di.qualifier.EncryptedSharedPref
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val KEY_TOKEN = "loginToken"

private const val KEY_MOBILE = "mobile"

class LoginLocalDataSource @Inject constructor(@EncryptedSharedPref private val sharePrf: SharedPreferences) {
    fun tokenFlow(): Flow<String?> = callbackFlow<String?> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_TOKEN) {
                trySend(sharePrf.getString(key, null))
            }
        }
        sharePrf.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            sharePrf.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    fun saveToken(token: String, mobile: String) {
        sharePrf.edit {
            putString(KEY_TOKEN, token)
            putString(KEY_MOBILE, mobile)
        }
    }

    fun getToken(): String? {
        return sharePrf.getString(KEY_TOKEN, null)
    }

    fun getMobile(): String? {
        return sharePrf.getString(KEY_MOBILE, null)
    }

    fun saveLoginRequiredIsShown(isShown: Boolean) {
        sharePrf.edit {
            putBoolean("loginRequiredIsShown", isShown)
        }
    }

    fun getLoginRequiredIsShown(): Boolean {
        return sharePrf.getBoolean("loginRequiredIsShown", false)
    }

    fun resetToken() {
        sharePrf.edit {
            putString(KEY_TOKEN, null)
        }
    }
}