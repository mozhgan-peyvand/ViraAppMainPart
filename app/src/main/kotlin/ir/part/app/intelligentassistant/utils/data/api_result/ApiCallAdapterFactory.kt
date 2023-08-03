package ir.part.app.intelligentassistant.utils.data.api_result

import com.squareup.moshi.JsonDataException
import okhttp3.Request
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) { "Return type must be parameterized type" }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResult::class.java) return null
        check(responseType is ParameterizedType) { "Response type must be a parameterized type" }

        val successType = getParameterUpperBound(0, responseType)
        return ApiResultCallAdapter<Any>(successType)
    }
}

private class ApiResultCallAdapter<R>(
    private val successType: Type
) : CallAdapter<R, Call<ApiResult<R>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<R>): Call<ApiResult<R>> {
        return ApiResultCall(call, successType)
    }
}

private class ApiResultCall<R>(
    private val delegate: Call<R>,
    private val successType: Type
) : Call<ApiResult<R>> {
    override fun clone(): Call<ApiResult<R>> {
        return ApiResultCall<R>(
            delegate = delegate.clone(),
            successType = successType
        )
    }

    override fun execute(): Response<ApiResult<R>> {
        return try {
            Response.success(toApiResult(delegate.execute()))
        } catch (t: Exception) {
            val error = when (t) {
                is IOException -> ApiError.IOError(t)
                is JsonDataException -> ApiError.JsonParseException(t)
                else -> ApiError.UnknownApiError(t)
            }
            Response.success(ApiResult.Error<R>(error))
        }
    }

    override fun enqueue(callback: Callback<ApiResult<R>>) {
        delegate.enqueue(
            object : Callback<R> {
                override fun onResponse(call: Call<R>, response: Response<R>) {
                    callback.onResponse(this@ApiResultCall, Response.success(toApiResult(response)))
                }

                override fun onFailure(call: Call<R>, t: Throwable) {
                    val error = when (t) {
                        is IOException -> ApiError.IOError(t)
                        is JsonDataException -> ApiError.JsonParseException(t)
                        else -> ApiError.UnknownApiError(t)
                    }
                    callback.onResponse(
                        this@ApiResultCall,
                        Response.success(ApiResult.Error<R>(error))
                    )
                }
            }
        )
    }

    private fun toApiResult(response: Response<R>): ApiResult<R> {
        with(response) {
            if (!isSuccessful) {
                val errorBody = errorBody()?.string().orEmpty()
                return ApiResult.Error(ApiError.HttpError(code(), errorBody))
            }

            body()?.let { body -> return ApiResult.Success(body) }

            return if (successType == Unit::class.java) {
                @Suppress("UNCHECKED_CAST")
                ApiResult.Success(Unit) as ApiResult<R>
            } else {
                ApiResult.Error(ApiError.EmptyBodyError)
            }
        }
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}