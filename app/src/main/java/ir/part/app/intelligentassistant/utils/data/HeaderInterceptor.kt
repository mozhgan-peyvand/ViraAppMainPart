package ir.part.app.intelligentassistant.utils.data

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val TOKEN =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzeXN0ZW0iOiJzYWhhYiIsImNyZWF0ZVRpbWUiOiIxNDAyMDQyNDE0MjkyNTk3OSIsInVuaXF1ZUZpZWxkcyI6eyJ1c2VybmFtZSI6IjdlMjVhYTM1LWQ1NGYtNDYzNi1iOTVkLWQ1MzBhNjhkZjZmNCJ9LCJkYXRhIjp7InNlcnZpY2VJRCI6IjlmMjE1NjVjLTcxZmEtNDViMy1hZDQwLTM4ZmY2YTZjNWM2OCIsInJhbmRvbVRleHQiOiJPcWpteCJ9LCJncm91cE5hbWUiOiIyMDQxNDgzNDE1YzJhZGJmNjgzMDhiMjFkZGQyODcxNCJ9.T726PvHEAbMyvtEP00IU5YWKawNp7NhSMaPq04def9s"

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