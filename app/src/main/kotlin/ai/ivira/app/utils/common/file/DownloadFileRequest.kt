package ai.ivira.app.utils.common.file

import ai.ivira.app.utils.common.orZero
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
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
    ) {
        var channel: ByteReadChannel? = null

        try {
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
        } catch (e: Exception) {
            channel?.closedCause

            if (file.exists()) file.delete()
        }
    }
}