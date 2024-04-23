package ai.ivira.app.features.login.data

import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppException
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val localDataSource: LoginLocalDataSource,
    private val remoteDataSource: LoginRemoteDataSource,
    private val networkHandler: NetworkHandler
) {
    suspend fun sendOtp(phoneNumber: String): AppResult<Unit> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        return when (val result = remoteDataSource.sendOtp(SendOtpRequestNetwork(phoneNumber))) {
            is ApiResult.Error -> AppResult.Error(result.error.toAppException())
            is ApiResult.Success -> AppResult.Success(result.data)
        }
    }

    suspend fun verifyOtp(
        mobile: String,
        otpCode: String
    ): AppResult<Unit> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }
        return when (val result = remoteDataSource.verifyOtp(
            VerifyOtpNetworkRequest(mobile = mobile, otp = otpCode)
        )) {
            is ApiResult.Error -> AppResult.Error(result.error.toAppException())
            is ApiResult.Success -> {
                localDataSource.saveToken(token = result.data.token, mobile = mobile)
                AppResult.Success(Unit)
            }
        }
    }

    fun getToken() = localDataSource.getToken()
}