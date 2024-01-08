package ai.ivira.app.utils.data.api_result

import ai.ivira.app.utils.data.api_result.ApiError.EmptyBodyError
import ai.ivira.app.utils.data.api_result.ApiError.HttpError
import ai.ivira.app.utils.data.api_result.ApiError.IOError
import ai.ivira.app.utils.data.api_result.ApiError.JsonParseException
import ai.ivira.app.utils.data.api_result.ApiError.UnknownApiError
import ai.ivira.app.utils.data.exceptions.EmptyBodyException
import ai.ivira.app.utils.data.exceptions.RemoteDataSourceException
import java.io.IOException

sealed interface ApiError {
    data class HttpError(val code: Int, val body: String) : ApiError
    data class IOError(val exception: IOException) : ApiError
    data class JsonParseException(val throwable: Throwable) : ApiError
    data class UnknownApiError(val throwable: Throwable) : ApiError
    data object EmptyBodyError : ApiError

    fun getException(): Throwable {
        return when (this) {
            EmptyBodyError -> EmptyBodyException()
            is HttpError -> RemoteDataSourceException(code, body)
            is IOError -> exception
            is JsonParseException -> throwable
            is UnknownApiError -> throwable
        }
    }
}

fun ApiError.toAppException(): AppException =
    when (this) {
        is HttpError -> AppException.RemoteDataSourceException(this.body)
        is IOError -> AppException.IOException(exception)
        is JsonParseException -> AppException.IOException(throwable)
        is UnknownApiError -> AppException.IOException(throwable)
        is EmptyBodyError -> AppException.IOException(IllegalStateException("empty body"))
    }