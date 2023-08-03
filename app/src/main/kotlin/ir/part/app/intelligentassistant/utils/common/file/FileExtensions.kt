package ir.part.app.intelligentassistant.utils.common.file

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.annotation.RawRes
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import ir.part.app.intelligentassistant.R
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream


fun File.toMultiPart(partName: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        partName,
        this.name,
        this.asRequestBody(
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension)!!.toMediaType()
        )
    )
}

fun File.toMultiPart(listener: UploadProgressCallback): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        "file",
        this.name,
        UploadFileRequestBody(this, listener)
    )
}

private fun readFileFromRawFolder(
    context: Context,
    fileName: String,
    @RawRes fileInRawFolder: Int
): File {
    val file = File(context.filesDir, fileName)
    if (!file.exists()) {
        val stream = context.resources.openRawResource(fileInRawFolder)
        file.writeBytes(stream.readBytes())
        stream.close()
    }

    return file
}

suspend fun convertTextToPdf(
    fileName: String,
    text: String,
    context: Context,
): File? = withContext(IO) {

    val document = Document()

    val outputFile = File(context.filesDir, "$fileName.pdf")

    if (!outputFile.exists()) {

        try {

            PdfWriter.getInstance(document, FileOutputStream(outputFile.absolutePath))
            document.open()

            //todo read font from font
            //todo set appropriate font
            val fontPath = readFileFromRawFolder(
                context,
                "iran_yekan_regular.ttf",
                R.raw.iran_yekan_regular
            ).absolutePath
            val persianFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

            val font = Font(persianFont, 12f)

            val p = Paragraph(text, font)

            p.alignment = Paragraph.ALIGN_RIGHT // Right-align the text
            p.spacingBefore = 5f
            p.spacingAfter = 5f
            p.alignment = Paragraph.ALIGN_CENTER
            val table = PdfPTable(1)
            val cell = PdfPCell(p)
            cell.border = 0
            cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
            table.addCell(cell)
            document.add(table)

            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            document.close()
        }
    } else outputFile
}