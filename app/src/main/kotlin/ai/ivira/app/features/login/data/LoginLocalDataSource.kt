package ai.ivira.app.features.login.data

import ai.ivira.app.utils.common.di.qualifier.EncryptedSharedPref
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class LoginLocalDataSource @Inject constructor(@EncryptedSharedPref private val sharePrf: SharedPreferences) {
    fun saveToken(token: String, mobile: String) {
        sharePrf.edit {
            putString("loginToken", token)
            putString("mobile", mobile)
        }
    }

    fun getToken(): String? {
        return sharePrf.getString("loginToken", null)
    }
}