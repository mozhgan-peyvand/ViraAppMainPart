package ai.ivira.app.utils.data.api_result

sealed interface AppException {
    object IOException : AppException
    data class RemoteDataSourceException(val body: String) : AppException
    data class NetworkConnectionException(val message: String = "Network Connection Error") :
        AppException
}