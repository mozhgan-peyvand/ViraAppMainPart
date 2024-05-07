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
    fun tokenFlow() = localDataSource.tokenFlow()

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

    suspend fun logout(): AppResult<Unit> {
        val token = localDataSource.getToken().orEmpty()

        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        return when (val result = remoteDataSource.logout(token)) {
            is ApiResult.Error -> AppResult.Error(result.error.toAppException())
            is ApiResult.Success -> AppResult.Success(result.data).also {
                localDataSource.resetToken()
            }
        }
    }

    fun getToken() = localDataSource.getToken()

    fun getMobile() = localDataSource.getMobile()

    fun saveLoginRequiredIsShown(isShown: Boolean) =
        localDataSource.saveLoginRequiredIsShown(isShown)

    fun getLoginRequiredIsShown() = localDataSource.getLoginRequiredIsShown()

    suspend fun cleanPreviousUserData(): AppResult<Unit> {
        localDataSource.cleanAllUserData()
        return AppResult.Success(Unit)
    }
}