package ai.ivira.app.utils.ui

import android.content.Intent

fun openAudioSelector(): Intent {
    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "audio/*"
    val mimetypes = arrayOf("audio/mpeg")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
    return intent
}