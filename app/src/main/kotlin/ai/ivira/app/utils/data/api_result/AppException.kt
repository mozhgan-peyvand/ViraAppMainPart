package ai.ivira.app.utils.data.api_result

sealed interface AppException {
    data class IOException(val cause: Throwable) : AppException
    data class RemoteDataSourceException(val body: String) : AppException
    data class NetworkConnectionException(val message: String = "Network Connection Error") :
        AppException
}