package ai.ivira.app.utils.data

import ai.ivira.app.utils.data.api_result.ApiResult

suspend fun <T, R> makeRequest(
    action: suspend () -> ApiResult<T>,
    onSuccess: suspend ApiResult.Success<T>.() -> ApiResult<R>
): ApiResult<R> {
    return when (val result = action()) {
        is ApiResult.Success<T> -> onSuccess(result)
        is ApiResult.Error<T> -> ApiResult.Error(result.error)
    }
}