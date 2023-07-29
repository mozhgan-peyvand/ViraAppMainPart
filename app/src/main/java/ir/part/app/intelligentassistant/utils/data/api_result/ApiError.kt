package ir.part.app.intelligentassistant.utils.data.api_result

import java.io.IOException

sealed interface ApiError {
    data class HttpError(val code: Int, val body: String) : ApiError
    data class IOError(val exception: IOException) : ApiError
    data class JsonParseException(val throwable: Throwable) : ApiError
    data class UnknownApiError(val throwable: Throwable) : ApiError
    object EmptyBodyError : ApiError
}
