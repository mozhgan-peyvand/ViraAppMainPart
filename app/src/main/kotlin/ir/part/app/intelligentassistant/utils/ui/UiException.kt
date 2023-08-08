package ir.part.app.intelligentassistant.utils.ui

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.data.api_result.AppException
import ir.part.app.intelligentassistant.utils.data.api_result.AppException.IOException
import ir.part.app.intelligentassistant.utils.data.api_result.AppException.NetworkConnectionException
import ir.part.app.intelligentassistant.utils.data.api_result.AppException.RemoteDataSourceException
import ir.part.app.intelligentassistant.utils.ui.ApiErrorCodes.InvalidInputData
import ir.part.app.intelligentassistant.utils.ui.ApiErrorCodes.InvalidToken
import ir.part.app.intelligentassistant.utils.ui.ApiErrorCodes.TokenNotProvided
import ir.part.app.intelligentassistant.utils.ui.ApiErrorCodes.UrlNotFound
import javax.inject.Inject

//TODO set appropriate error message
class UiException @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getErrorMessage(appException: AppException): String {
        val defaultMessage = context.getString(R.string.msg_there_is_a_problem)
        val message: String = when (appException) {

            is IOException -> context.getString(R.string.msg_server_error)
            is NetworkConnectionException -> context.getString(R.string.msg_connection_error)
            is RemoteDataSourceException -> {
                when (appException.body) {
                    InvalidInputData.value -> context.getString(R.string.msg_invalid_input_data)
                    TokenNotProvided.value -> context.getString(R.string.msg_token_not_provided)
                    InvalidToken.value -> context.getString(R.string.msg_invalid_token)
                    UrlNotFound.value -> context.getString(R.string.msg_url_not_found)
                    else -> defaultMessage
                }
            }
        }

        return message
    }
}