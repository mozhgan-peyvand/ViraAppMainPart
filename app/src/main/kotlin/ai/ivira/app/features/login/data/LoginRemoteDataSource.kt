package ai.ivira.app.features.login.data

import ai.ivira.app.utils.data.api_result.ApiError
import ai.ivira.app.utils.data.api_result.ApiResult
import com.squareup.moshi.Moshi
import javax.inject.Inject

class LoginRemoteDataSource @Inject constructor(
    private val loginService: LoginService,
    private val moshi: Moshi
) {
    init {
        System.loadLibrary("vira")
    }

    suspend fun sendOtp(sendOtpRequestNetwork: SendOtpRequestNetwork): ApiResult<Unit> {
        return when (
            val result = loginService.sendOtp(
                url = lbu() + "sendOtp",
                gatewaySystem = lgs(),
                system = ls(),
                sendOtpBody = sendOtpRequestNetwork
            )
        ) {
            is ApiResult.Error -> {
                when (result.error) {
                    is ApiError.HttpError -> {
                        val processedApiError = replaceErrorBodyWithServerCode(result.error)
                        ApiResult.Error(processedApiError)
                    }
                    ApiError.EmptyBodyError,
                    is ApiError.IOError,
                    is ApiError.JsonParseException,
                    is ApiError.UnknownApiError -> ApiResult.Error(result.error)
                }
            }
            is ApiResult.Success -> ApiResult.Success(Unit)
        }
    }

    suspend fun verifyOtp(otpParams: VerifyOtpNetworkRequest): ApiResult<VerifyOtpNetworkResponse> {
        return when (val result = loginService.verifyOtp(
            url = lbu() + "verifyOtp",
            gatewaySystem = lgs(),
            system = ls(),
            otpParams = otpParams
        )) {
            is ApiResult.Error -> {
                when (result.error) {
                    is ApiError.HttpError -> {
                        val processedApiError = replaceErrorBodyWithServerCode(result.error)
                        ApiResult.Error(processedApiError)
                    }
                    ApiError.EmptyBodyError,
                    is ApiError.IOError,
                    is ApiError.JsonParseException,
                    is ApiError.UnknownApiError -> ApiResult.Error(result.error)
                }
            }
            is ApiResult.Success -> ApiResult.Success(result.data.data)
        }
    }

    private fun replaceErrorBodyWithServerCode(error: ApiError.HttpError): ApiError.HttpError {
        val adapter = moshi.adapter(ErrorResponse::class.java)
        return ApiError.HttpError(
            error.code,
            adapter.fromJson(error.body)?.meta?.code.orEmpty()
        )
    }

    private external fun lbu(): String
    private external fun lgs(): String
    private external fun ls(): String
}