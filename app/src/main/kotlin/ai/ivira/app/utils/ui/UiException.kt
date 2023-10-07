package ai.ivira.app.utils.ui

import ai.ivira.app.R
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppException.IOException
import ai.ivira.app.utils.data.api_result.AppException.NetworkConnectionException
import ai.ivira.app.utils.data.api_result.AppException.RemoteDataSourceException
import ai.ivira.app.utils.ui.ApiErrorCodes.InvalidInputData
import ai.ivira.app.utils.ui.ApiErrorCodes.InvalidToken
import ai.ivira.app.utils.ui.ApiErrorCodes.TokenNotProvided
import ai.ivira.app.utils.ui.ApiErrorCodes.UrlNotFound
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// TODO set appropriate error message
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

    fun getErrorMessageInvalidFile(): String = context.getString(R.string.msg_invalid_file)
}