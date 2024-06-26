package ai.ivira.app.utils.ui

import ai.ivira.app.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "application/pdf"
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.lbl_share_file)
            )
        )
    } catch (e: Exception) {
        Toast.makeText(
            context,
            context.getString(R.string.msg_convert_error),
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun shareMp3(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "audio/mpeg"
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.lbl_share_file)
            )
        )
    } catch (e: Exception) {
        Toast.makeText(
            context,
            context.getString(R.string.msg_convert_error),
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun shareTXT(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.lbl_share_file)
            )
        )
    } catch (e: Exception) {
        Toast.makeText(
            context,
            context.getString(R.string.msg_convert_error),
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun shareText(context: Context, text: String) {
    if (text.isEmpty()) return

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.lbl_share_file)
        )
    )
}

fun shareMultipleImage(context: Context, fileUris: ArrayList<Uri>) {
    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
    shareIntent.type = "image/*"

    runCatching {
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
        context.startActivity(shareIntent)
    }
}