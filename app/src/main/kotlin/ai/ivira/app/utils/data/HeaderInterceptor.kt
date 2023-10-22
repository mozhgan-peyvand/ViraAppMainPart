package ai.ivira.app.utils.data

import ai.ivira.app.utils.common.event.ViraEvent
import ai.ivira.app.utils.common.event.ViraPublisher
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val API_KEY =
    "adff393c9ff5c559d4a6ec03c60de0950100f190845cb51f77eef9d132f87afefbfe3248b2de91baa6baeec9b0b4a531c4430b9ac934bcc09ec08560fa934274"

@Singleton
class HeaderInterceptor @Inject constructor(
    private val aiEventPublisher: ViraPublisher
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        if (request.header("ApiKey") == null) {
            requestBuilder.addHeader(
                "ApiKey",
                API_KEY
            )
        }

        val response = chain.proceed(requestBuilder.build())

        // TODO check to make sure 401 really happen - should show update bottomSheet
        if (response.code == 401) {
            aiEventPublisher.publishEvent(iaEvent = ViraEvent.TokenExpired)
        }
        return response
    }
}