package ai.ivira.app.features.login.data

import ai.ivira.app.utils.data.EmptyResponse
import ai.ivira.app.utils.data.ViraNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface LoginService {
    @POST
    suspend fun sendOtp(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String,
        @Header("system") system: String,
        @Body sendOtpBody: SendOtpRequestNetwork
    ): ApiResult<ViraNetwork<EmptyResponse>>

    @POST
    suspend fun verifyOtp(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String,
        @Header("system") system: String,
        @Body otpParams: VerifyOtpNetworkRequest
    ): ApiResult<ViraNetwork<VerifyOtpNetworkResponse>>

    @GET
    suspend fun logout(
        @Url url: String,
        @Header("gateway-token") gatewayToken: String,
        @Header("system") system: String
    ): ApiResult<ViraNetwork<EmptyResponse>>
}