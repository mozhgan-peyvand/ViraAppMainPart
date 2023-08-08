package ir.part.app.intelligentassistant.utils.data.api_result

import ir.part.app.intelligentassistant.utils.data.api_result.ApiError.EmptyBodyError
import ir.part.app.intelligentassistant.utils.data.api_result.ApiError.HttpError
import ir.part.app.intelligentassistant.utils.data.api_result.ApiError.IOError
import ir.part.app.intelligentassistant.utils.data.api_result.ApiError.JsonParseException
import ir.part.app.intelligentassistant.utils.data.api_result.ApiError.UnknownApiError
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
