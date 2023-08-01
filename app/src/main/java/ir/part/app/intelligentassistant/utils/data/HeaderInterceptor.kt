package ir.part.app.intelligentassistant.utils.data

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val TOKEN =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzeXN0ZW0iOiJzYWhhYiIsImNyZWF0ZVRpbWUiOiIxNDAyMDUxMDEwMTUzNzQxOCIsInVuaXF1ZUZpZWxkcyI6eyJ1c2VybmFtZSI6Ijc2YjY1ZDRmLTI5OWYtNGYyYy1hMzlmLTVjYTg3OGQyZmU1YSJ9LCJkYXRhIjp7InNlcnZpY2VJRCI6IjlmMjE1NjVjLTcxZmEtNDViMy1hZDQwLTM4ZmY2YTZjNWM2OCIsInJhbmRvbVRleHQiOiJFM3ZXQiJ9LCJncm91cE5hbWUiOiJhNzk0MWM5ZWMyMjA3NTc4ODQwYTgxNGQ0NTI3YTI2OCJ9.9mB7v8nBqrEvmq8qvqMHpxjls6gVP50XU0AKDDVg01c"

@Singleton
class HeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val requestBuilder = request.newBuilder()

        if (request.header("gateway-token") == null)
            requestBuilder.addHeader(
                "gateway-token",
                TOKEN
            )
        return chain.proceed(requestBuilder.build())
    }
}