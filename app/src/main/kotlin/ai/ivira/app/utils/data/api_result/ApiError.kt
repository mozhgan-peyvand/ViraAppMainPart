package ai.ivira.app.utils.data.api_result

import ai.ivira.app.utils.data.api_result.ApiError.EmptyBodyError
import ai.ivira.app.utils.data.api_result.ApiError.HttpError
import ai.ivira.app.utils.data.api_result.ApiError.IOError
import ai.ivira.app.utils.data.api_result.ApiError.JsonParseException
import ai.ivira.app.utils.data.api_result.ApiError.UnknownApiError
import java.io.IOException

sealed interface ApiError {
    data class HttpError(val code: Int, val body: String) : ApiError
    data class IOError(val exception: IOException) : ApiError
    data class JsonParseException(val throwable: Throwable) : ApiError
    data class UnknownApiError(val throwable: Throwable) : ApiError
    object EmptyBodyError : ApiError
}

fun ApiError.toAppException(): AppException =
    when (this) {
        is HttpError -> AppException.RemoteDataSourceException(this.body)
        is IOError -> AppException.IOException
        is JsonParseException -> AppException.IOException
        is UnknownApiError -> AppException.IOException
        is EmptyBodyError -> AppException.IOException
    }