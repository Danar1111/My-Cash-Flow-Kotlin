package com.example.mymoneynotes.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.mymoneynotes.model.Transaction
import java.io.File
import java.io.FileOutputStream
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ExportUtils {
    fun export(context: Context, transactions: List<Transaction>) {
        try {
            val doc = PdfDocument()
            val page = doc.startPage(PdfDocument.PageInfo.Builder(300, 600, 1).create())
            val canvas = page.canvas
            var y = 20
            val paint = android.graphics.Paint()
            transactions.forEach { t ->
                val date = DateTimeFormatter.ISO_DATE.format(
                    java.time.Instant.ofEpochMilli(t.date).atZone(ZoneId.systemDefault()).toLocalDate()
                )
                canvas.drawText("$date - ${t.category} : ${t.amount}", 10f, y.toFloat(), paint)
                y += 20
            }
            doc.finishPage(page)
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (dir != null && !dir.exists()) dir.mkdirs()
            val file = File(dir, "transactions.pdf")
            FileOutputStream(file).use { out -> doc.writeTo(out) }
            doc.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
