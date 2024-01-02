package ai.ivira.app.utils.data

import ai.ivira.app.utils.common.event.ViraEvent
import ai.ivira.app.utils.common.event.ViraPublisher
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvalidTokenInterceptor @Inject constructor(
    private val aiEventPublisher: ViraPublisher
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        val response = chain.proceed(requestBuilder.build())

        // TODO check to make sure 401 really happen - should show update bottomSheet
        if (response.code == 401) {
            aiEventPublisher.publishEvent(iaEvent = ViraEvent.TokenExpired)
        }
        return response
    }
}