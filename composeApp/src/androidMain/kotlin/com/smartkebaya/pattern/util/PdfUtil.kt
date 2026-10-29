package com.smartkebaya.pattern.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.smartkebaya.pattern.model.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt

object PdfUtil {

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

        addPatternPages(document, pageWidth, pageHeight, patternSet.front, unit)
        addPatternPages(document, pageWidth, pageHeight, patternSet.back, unit)
        addPatternPages(document, pageWidth, pageHeight, patternSet.sleeve, unit)
        addPatternPages(document, pageWidth, pageHeight, patternSet.kutubaru, unit)

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
        unit: UnitSystem
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

        drawVectorPattern(canvas1, drawing, RectF(margin, currentY, w - margin, h - margin - 30f))
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

    /** Mirror of PatternDrawingCanvas for the PDF: the same drafted points, drawn as vectors. */
    private fun drawVectorPattern(canvas: Canvas, drawing: PatternDrawing, area: RectF) {
        val (minX, minY, maxX, maxY) = drawing.bounds()
        val mirror = minX >= 0.0
        val scale = min(area.width() / (maxX - minX), area.height() / (maxY - minY)).toFloat() // pt per cm
        val offsetX = area.left + (area.width() - ((maxX - minX) * scale).toFloat()) / 2
        fun sx(x: Double) = offsetX + ((if (mirror) maxX - x else x - minX) * scale).toFloat()
        fun sy(y: Double) = area.top + ((y - minY) * scale).toFloat()

        val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 1.6f; color = Color.rgb(176, 32, 48); strokeJoin = Paint.Join.ROUND }
        val guide = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 0.6f; color = Color.GRAY; pathEffect = DashPathEffect(floatArrayOf(4f, 3f), 0f) }
        val dart = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 1f; color = Color.rgb(30, 90, 160) }
        val label = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 8f; isFakeBoldText = true; color = Color.BLACK }

        drawing.polylines().forEach { (path, line) ->
            val shape = Path()
            line.forEachIndexed { i, (x, y) -> if (i == 0) shape.moveTo(sx(x), sy(y)) else shape.lineTo(sx(x), sy(y)) }
            canvas.drawPath(shape, when (path.stroke) { Stroke.OUTLINE -> outline; Stroke.CONSTRUCTION -> guide; Stroke.DART -> dart })
        }
        drawing.points.filter { it.id != "neck" && it.id != "armpit" }.forEach { point ->
            canvas.drawCircle(sx(point.x), sy(point.y), 1.5f, label)
            canvas.drawText(point.id.removeSuffix("2"), sx(point.x) + 3f, sy(point.y) - 3f, label)
        }

        // 10 cm scale bar and the drawing's scale (1 cm = 28.35 pt on paper).
        val barY = area.bottom + 20f
        canvas.drawLine(area.left, barY, area.left + 10 * scale, barY, outline)
        canvas.drawText("10 cm   (skala ±1:${(28.35f / scale).roundToInt()})", area.left + 10 * scale + 6f, barY + 3f, label)
    }
}
