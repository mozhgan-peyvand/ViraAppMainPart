package ai.ivira.app.utils.common.file

import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.api_result.ApiError.EmptyBodyError
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.ApiResult.Error
import ai.ivira.app.utils.data.api_result.ApiResult.Success
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadFileRequest @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun downloadFile(
        url: String,
        file: File,
        progress: (byteReceived: Long, totalSize: Long) -> Unit
    ): ApiResult<Unit> {
        var channel: ByteReadChannel?

        return try {
            httpClient.prepareGet(url).execute { httpResponse ->
                channel = httpResponse.body()
                channel?.let {
                    while (!it.isClosedForRead) {
                        val packet = it.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.isEmpty) {
                            val bytes = packet.readBytes()
                            file.appendBytes(bytes)
                            progress(file.length(), httpResponse.contentLength().orZero())
                        }
                    }
                }
            }

            Success(Unit)
        } catch (e: Exception) {
            channel = null
            Timber.d(e)
            if (file.exists()) file.delete()
            Error(EmptyBodyError)
        }
    }
}