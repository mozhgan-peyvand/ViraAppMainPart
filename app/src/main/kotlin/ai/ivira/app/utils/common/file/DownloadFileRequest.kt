package ai.ivira.app.utils.common.file

import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.api_result.ApiError.EmptyBodyError
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.ApiResult.Error
import ai.ivira.app.utils.data.api_result.ApiResult.Success
import android.app.Application
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadFileRequest @Inject constructor(
    private val httpClient: HttpClient,
    private val application: Application
) {
    suspend fun downloadFile(
        url: String,
        file: File,
        token: String,
        progress: (byteReceived: Long, totalSize: Long) -> Unit
    ): ApiResult<Unit> {
        var channel: ByteReadChannel?

        val tmpFile = File(application.cacheDir, "${UUID.randomUUID()}")
        return try {
            httpClient.prepareGet(url) {
                header("ApiKey", token)
            }.execute { httpResponse ->
                channel = httpResponse.body()
                channel?.let {
                    while (!it.isClosedForRead) {
                        val packet = it.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.isEmpty) {
                            val bytes = packet.readBytes()
                            tmpFile.appendBytes(bytes)
                            progress(tmpFile.length(), httpResponse.contentLength().orZero())
                        }
                    }
                }
            }

            tmpFile.renameTo(file)
            Success(Unit)
        } catch (e: Exception) {
            runCatching { tmpFile.delete() }
            channel = null
            Timber.d(e)
            if (file.exists()) file.delete()
            Error(EmptyBodyError)
        }
    }
}