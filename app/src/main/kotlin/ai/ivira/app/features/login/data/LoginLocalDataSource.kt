package ai.ivira.app.features.login.data

import ai.ivira.app.features.ava_negar.ui.record.VoiceRecordingViewModel.Companion.KEY_DEFAULT_NAME_COUNTER
import ai.ivira.app.features.avasho.ui.file_creation.AvashoFileCreationViewModel.Companion.KEY_DEFAULT_VOICE_NAME_COUNTER
import ai.ivira.app.utils.common.di.qualifier.EncryptedSharedPref
import ai.ivira.app.utils.common.file.FileCache
import ai.ivira.app.utils.data.db.ViraDb
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val KEY_TOKEN = "loginToken"

private const val KEY_MOBILE = "mobile"

class LoginLocalDataSource @Inject constructor(
    @EncryptedSharedPref private val encryptedSharedPref: SharedPreferences,
    private val sharedPrf: SharedPreferences,
    private val fileCache: FileCache,
    private val db: ViraDb
) {
    fun tokenFlow(): Flow<String?> = callbackFlow<String?> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_TOKEN) {
                trySend(encryptedSharedPref.getString(key, null))
            }
        }
        encryptedSharedPref.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            encryptedSharedPref.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    fun saveToken(token: String, mobile: String) {
        encryptedSharedPref.edit {
            putString(KEY_TOKEN, token)
            putString(KEY_MOBILE, mobile)
        }
    }

    fun getToken(): String? {
        return encryptedSharedPref.getString(KEY_TOKEN, null)
    }

    fun getMobile(): String? {
        return encryptedSharedPref.getString(KEY_MOBILE, null)
    }

    fun saveLoginRequiredIsShown(isShown: Boolean) {
        encryptedSharedPref.edit {
            putBoolean("loginRequiredIsShown", isShown)
        }
    }

    fun getLoginRequiredIsShown(): Boolean {
        return encryptedSharedPref.getBoolean("loginRequiredIsShown", false)
    }

    fun resetToken() {
        encryptedSharedPref.edit {
            putString(KEY_TOKEN, null)
        }
    }

    suspend fun cleanAllUserData() {
        fileCache.removeAllViraFiles()
        withContext(IO) { runCatching { db.clearAllTables() } }
        deleteFileBasedKeys()
    }

    private fun deleteFileBasedKeys() {
        sharedPrf.edit {
            remove(KEY_DEFAULT_NAME_COUNTER)
            remove(KEY_DEFAULT_VOICE_NAME_COUNTER)
            // FIXME: Replace it wth const key after Hamahang merged
            remove("hamahangDefaultCounter")
        }
    }
}