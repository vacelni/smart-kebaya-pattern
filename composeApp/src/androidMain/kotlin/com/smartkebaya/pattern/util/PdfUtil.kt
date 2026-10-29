package com.smartkebaya.pattern.util

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.smartkebaya.pattern.R
import com.smartkebaya.pattern.model.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfUtil {

    private val labelPositions = mapOf(
        "Pola Depan" to mapOf(
            "A-B" to (0.75f to 0.12f),
            "A-D" to (0.75f to 0.20f),
            "B-C" to (0.45f to 0.04f),
            "C-I" to (0.45f to 0.12f),
            "D-G" to (0.75f to 0.30f),
            "G-H" to (0.55f to 0.25f),
            "F-J" to (0.55f to 0.40f),
            "D-E" to (0.75f to 0.40f),
            "E-F" to (0.75f to 0.48f),
            "E-L" to (0.30f to 0.65f),
            "E-T" to (0.75f to 0.68f),
            "T-U" to (0.45f to 0.70f),
            "T-V" to (0.45f to 0.77f),
            "E-R" to (0.75f to 0.55f),
            "R-S" to (0.30f to 0.50f),
            "E-M" to (0.75f to 0.80f),
            "M-P" to (0.45f to 0.85f),
            "E-N" to (0.75f to 0.90f),
            "N-O" to (0.45f to 0.98f),
            "N-Q" to (0.75f to 0.99f)
        ),
        "Pola Belakang" to mapOf(
            "X-N" to (0.77f to 0.01f),
            "X-K" to (0.77f to 0.10f),
            "N-D" to (0.55f to 0.10f),
            "D-Q" to (0.55f to 0.18f),
            "J-P" to (0.55f to 0.27f),
            "B-T" to (0.28f to 0.35f),
            "K-M" to (0.77f to 0.40f),
            "M-B" to (0.77f to 0.50f),
            "M-Y" to (0.28f to 0.60f),
            "M-M'" to (0.77f to 0.60f),
            "B'-O" to (0.45f to 0.40f),
            "M'-O'" to (0.65f to 0.77f),
            "M'-Y'" to (0.45f to 0.68f),
            "M-C" to (0.77f to 0.70f),
            "C-H" to (0.28f to 0.70f),
            "H-I" to (0.50f to 0.87f),
            "C-N" to (0.28f to 0.80f),
            "K-U" to (0.33f to 0.15f)
        ),
        "Pola Lengan" to mapOf(
            "A-F" to (0.50f to 0.80f),
            "A-B" to (0.50f to 0.20f),
            "A-D" to (0.78f to 0.10f),
            "A-C" to (0.22f to 0.10f),
            "M" to (0.35f to 0.01f),
            "N" to (0.68f to 0.01f),
            "P" to (0.90f to 0.22f),
            "R" to (0.12f to 0.22f),
            "A-E" to (0.50f to 0.45f),
            "I-E-J" to (0.50f to 0.60f),
            "F-G" to (0.15f to 0.88f),
            "F-H" to (0.85f to 0.88f),
            "G-H" to (0.50f to 0.88f)
        )
    )



    fun generatePatternPdf(
        context: Context,
        patternSet: KebayaPatternSet,
        inputs: MeasurementInputs,
        unit: UnitSystem
    ): File? {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

        val patternsDir = File(context.cacheDir, "patterns").apply {
            if (!exists()) mkdirs()
        }

        addPatternPages(document, pageWidth, pageHeight, patternSet.front, unit, context)
        addPatternPages(document, pageWidth, pageHeight, patternSet.back, unit, context)
        addPatternPages(document, pageWidth, pageHeight, patternSet.sleeve, unit, context)
        addPatternPages(document, pageWidth, pageHeight, patternSet.kutubaru, unit, context)

        val fileName = "kebaya_pattern_${System.currentTimeMillis()}.pdf"
        val file = File(patternsDir, fileName)
        
        return try {
            val outputStream = FileOutputStream(file)
            document.writeTo(outputStream)
            outputStream.close()
            document.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            document.close()
            null
        }
    }

    fun openPdf(context: Context, file: File?) {
        if (file == null) return
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addPatternPages(
        document: PdfDocument,
        w: Int,
        h: Int,
        drawing: PatternDrawing,
        unit: UnitSystem,
        context: Context
    ) {
        val margin = 50f
        val titlePaint = Paint().apply { 
            textSize = 22f 
            isFakeBoldText = true 
            color = Color.BLACK 
        }
        val subTitlePaint = Paint().apply {
            textSize = 10f
            color = Color.GRAY
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        val dateString = "Smart Kebaya Professional Pattern Generator - ${dateFormat.format(Date())}"

        // --- Page 1: Pattern Image with Labels ---
        val pageInfo1 = PdfDocument.PageInfo.Builder(w, h, document.pages.size + 1).create()
        val page1 = document.startPage(pageInfo1)
        val canvas1 = page1.canvas
        
        var currentY = margin + 30f
        canvas1.drawText(drawing.title.uppercase(), margin, currentY, titlePaint)
        currentY += 15f
        canvas1.drawText(dateString, margin, currentY, subTitlePaint)
        currentY += 20f
        canvas1.drawLine(margin, currentY, w - margin, currentY, linePaint)
        currentY += 40f

        val imageRes = when (drawing.title) {
            "Pola Depan" -> R.drawable.pola_depan
            "Pola Belakang" -> R.drawable.pola_belakang
            "Pola Lengan" -> R.drawable.pola_lengan
            else -> R.drawable.pola_kutubaru
        }
        
        val bitmap = BitmapFactory.decodeResource(context.resources, imageRes)
        if (bitmap != null) {
            val bitmapWidth = bitmap.width.toFloat()
            val bitmapHeight = bitmap.height.toFloat()
            val bitmapAspectRatio = bitmapHeight / bitmapWidth

            val availableWidth = w - 2 * margin
            val availableHeight = h - currentY - margin - 40f // leave some space at bottom
            
            val scale: Float
            if (availableWidth * bitmapAspectRatio <= availableHeight) {
                scale = availableWidth / bitmapWidth
            } else {
                scale = availableHeight / bitmapHeight
            }

            val finalImageWidth = bitmapWidth * scale
            val finalImageHeight = bitmapHeight * scale
            val imageX = margin + (availableWidth - finalImageWidth) / 2
            val imageY = currentY

            val rect = RectF(imageX, imageY, imageX + finalImageWidth, imageY + finalImageHeight)
            
            // Background area
            val bgPaint = Paint().apply { color = Color.rgb(248, 250, 252) }
            canvas1.drawRoundRect(RectF(margin, currentY - 10f, w - margin, imageY + finalImageHeight + 10f), 12f, 12f, bgPaint)
            
            canvas1.drawBitmap(bitmap, null, rect, null)
            
            val currentLabels = labelPositions[drawing.title]
            if (currentLabels != null) {
                val labelPaint = Paint().apply {
                    textSize = 7f
                    color = Color.BLACK
                    isFakeBoldText = true
                }
                val labelBgPaint = Paint().apply { color = Color.WHITE; alpha = 200 }
                val labelBorderPaint = Paint().apply {
                    color = Color.rgb(59, 130, 246)
                    style = Paint.Style.STROKE
                    strokeWidth = 0.5f
                }

                drawing.allValues.forEach { (key, value) ->
                    currentLabels[key]?.let { pos ->
                        // Match PatternCanvas offset logic: (maxWidth * pos.first) - 20.dp
                        val lx = imageX + (finalImageWidth * pos.first) - 20f
                        val ly = imageY + (finalImageHeight * pos.second) - 10f
                        val text = "$key: ${"%.1f".format(value)}"
                        val textWidth = labelPaint.measureText(text)
                        
                        // Box with padding matching UI: horizontal=5.dp, vertical=2.dp
                        val lRect = RectF(lx, ly, lx + textWidth + 10f, ly + 12f)
                        canvas1.drawRect(lRect, labelBgPaint)
                        canvas1.drawRect(lRect, labelBorderPaint)
                        canvas1.drawText(text, lx + 5f, ly + 9f, labelPaint)
                    }
                }
            }
        }
        document.finishPage(page1)

        // --- Page 2: Detail Measurements Table ---
        val pageInfo2 = PdfDocument.PageInfo.Builder(w, h, document.pages.size + 1).create()
        val page2 = document.startPage(pageInfo2)
        val canvas2 = page2.canvas

        currentY = margin + 30f
        canvas2.drawText(drawing.title.uppercase(), margin, currentY, titlePaint)
        currentY += 15f
        canvas2.drawText(dateString, margin, currentY, subTitlePaint)
        currentY += 20f
        canvas2.drawLine(margin, currentY, w - margin, currentY, linePaint)
        currentY += 50f

        val headerPaint = Paint().apply { 
            textSize = 16f 
            isFakeBoldText = true 
            color = Color.rgb(30, 41, 59) 
        }
        val textPaint = Paint().apply { 
            textSize = 11f 
            color = Color.BLACK 
        }
        val labelBgPaint = Paint().apply { color = Color.rgb(241, 245, 249) }

        canvas2.drawText("RINCIAN UKURAN POLA (${unit.name})", margin, currentY, headerPaint)
        currentY += 40f
        
        val factor = if (unit == UnitSystem.INCH) 1 / 2.54 else 1.0
        val entries = drawing.allValues.entries.toList()
        val colWidth = (w - 2 * margin - 20f) / 2
        
        entries.forEachIndexed { index, entry ->
            val isSecondCol = index >= (entries.size + 1) / 2
            val colX = if (isSecondCol) margin + colWidth + 20f else margin
            val rowIdx = if (isSecondCol) index - (entries.size + 1) / 2 else index
            val rowY = currentY + (rowIdx * 30f)
            
            if (rowY < h - margin) {
                val displayVal = "%.2f".format(entry.value * factor)
                val unitStr = if (unit == UnitSystem.INCH) "in" else "cm"
                
                val itemRect = RectF(colX, rowY - 20f, colX + colWidth, rowY + 5f)
                canvas2.drawRoundRect(itemRect, 6f, 6f, labelBgPaint)
                canvas2.drawText("${entry.key}: $displayVal $unitStr", colX + 10f, rowY - 4f, textPaint)
            }
        }
        
        document.finishPage(page2)
    }
}
