package ir.part.app.intelligentassistant.utils.data.api_result

sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error<out T>(val error: AppException) : AppResult<T>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$data]"
            is Error -> "Error[error=$error]"
        }
    }
}