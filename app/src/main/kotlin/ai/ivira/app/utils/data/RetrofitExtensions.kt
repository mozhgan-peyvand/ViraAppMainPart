package ai.ivira.app.utils.data

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

val String.asPlainTextRequestBody: RequestBody
    get() = this.toRequestBody("text/plain".toMediaTypeOrNull())