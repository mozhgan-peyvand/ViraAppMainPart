package ai.ivira.app.utils.ui

import android.content.Intent

fun openAudioSelector(): Intent {
    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "audio/mpeg"
    val mimetypes = arrayOf("audio/mpeg")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
    return intent
}

fun openFileIntent(type: String, mimeType: String): Intent {
    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = type
    val mimetypes = arrayOf(mimeType)
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
    return intent
}

fun openFileIntentAndroidTiramisu() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
    addCategory(Intent.CATEGORY_OPENABLE)
    type = "text/*"
    putExtra(Intent.EXTRA_MIME_TYPES, "text/plain")
}