package ai.ivira.app.features.login.data

import javax.inject.Inject

class LoginRemoteDataSource @Inject constructor() {
    init {
        System.loadLibrary("vira")
    }

    suspend fun sendOtp() {}

    suspend fun verifyOtp() {}

    private external fun lbu(): String
    private external fun lgs(): String
    private external fun ls(): String
}