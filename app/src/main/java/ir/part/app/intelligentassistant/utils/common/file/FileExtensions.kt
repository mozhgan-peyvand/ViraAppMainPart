package ir.part.app.intelligentassistant.utils.common.file

import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.toMultiPart(partName: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        partName,
        this.name,
        this.asRequestBody(
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension)!!.toMediaType()
        )
    )
}