package ai.ivira.app.utils.data.api_result

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error<out T>(val error: ApiError) : ApiResult<T>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$data]"
            is Error -> "Error[error=$error]"
        }
    }
}

fun <T> ApiResult<T>.toAppResult() =
    when (this) {
        is ApiResult.Success -> AppResult.Success(data)
        is ApiResult.Error -> AppResult.Error(error.toAppException())
    }