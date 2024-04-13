package ai.ivira.app.features.login.data

import javax.inject.Inject

class LoginRemoteDataSource @Inject constructor() {
    suspend fun sendOtp() {}

    suspend fun verifyOtp() {}
}