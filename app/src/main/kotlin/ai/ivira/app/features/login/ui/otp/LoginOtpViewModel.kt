package ai.ivira.app.features.login.ui.otp

import ai.ivira.app.features.login.data.LoginRepository
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginOtpViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel()