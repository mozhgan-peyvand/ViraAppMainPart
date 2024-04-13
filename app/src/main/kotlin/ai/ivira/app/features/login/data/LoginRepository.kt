package ai.ivira.app.features.login.data

import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val localDataSource: LoginLocalDataSource,
    private val remoteDataSource: LoginRemoteDataSource
) {
    suspend fun sendOtp() {}

    suspend fun verifyOtp() {}
}