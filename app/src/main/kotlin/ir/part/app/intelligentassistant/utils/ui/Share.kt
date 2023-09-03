package ir.part.app.intelligentassistant.utils.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import ir.part.app.intelligentassistant.R
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
