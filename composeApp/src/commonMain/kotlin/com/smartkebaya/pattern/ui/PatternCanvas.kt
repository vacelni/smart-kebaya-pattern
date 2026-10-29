package com.smartkebaya.pattern.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkebaya.pattern.model.PatternDrawing
import com.smartkebaya.pattern.model.Stroke
import com.smartkebaya.pattern.model.UnitSystem
import kotlin.math.min

/** Helper points that shape a curve but are not drafting points a tailor marks. */
private val hiddenPoints = setOf("neck", "armpit")

/** Mirrored points share a letter with their twin; "W2" is labelled "W". */
private fun labelOf(id: String) = id.removeSuffix("2")

/**
 * Draws a pattern piece to scale from its drafted points. Bodice pieces are mirrored so the centre line
 * is on the right, as in the reference drafts.
 */
@Composable
fun PatternDrawingCanvas(drawing: PatternDrawing, modifier: Modifier = Modifier) {
    val (minX, minY, maxX, maxY) = drawing.bounds()
    // Bodice pieces have every drafting point on one side of the centre line (x >= 0). Curve samples
    // can overshoot a hair past it, so decide on the points, not the samples.
    val mirror = drawing.points.all { it.x >= 0.0 }
    val widthCm = maxX - minX
    val heightCm = maxY - minY
    val outline = MaterialTheme.colorScheme.primary
    val guide = MaterialTheme.colorScheme.outline
    val dart = MaterialTheme.colorScheme.tertiary
    val ink = MaterialTheme.colorScheme.onSurface
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ink)

    Canvas(
        modifier
            .clipToBounds()
            .semantics { contentDescription = "${drawing.title}, digambar sesuai ukuran" }
    ) {
        val pad = 28.dp.toPx()
        val scaleBarSpace = 24.dp.toPx()
        val scale = min((size.width - 2 * pad) / widthCm, (size.height - 2 * pad - scaleBarSpace) / heightCm).toFloat()
        // Centre the piece horizontally in whatever width is left over.
        val left = (size.width - (widthCm * scale).toFloat()) / 2
        fun toScreen(x: Double, y: Double) = Offset(
            left + ((if (mirror) maxX - x else x - minX) * scale).toFloat(),
            pad + ((y - minY) * scale).toFloat()
        )

        drawing.polylines().sortedBy { (path, _) -> path.stroke != Stroke.CONSTRUCTION }.forEach { (path, line) ->
            val shape = Path().apply {
                line.forEachIndexed { i, (x, y) ->
                    val o = toScreen(x, y)
                    if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y)
                }
            }
            when (path.stroke) {
                Stroke.CONSTRUCTION -> drawPath(
                    shape, guide.copy(alpha = 0.7f),
                    style = DrawStroke(1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())))
                )
                Stroke.DART -> drawPath(shape, dart, style = DrawStroke(1.8.dp.toPx(), join = StrokeJoin.Round))
                Stroke.OUTLINE -> drawPath(shape, outline, style = DrawStroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }

        drawing.points.filter { it.id !in hiddenPoints }.forEach { point ->
            val o = toScreen(point.x, point.y)
            drawCircle(ink, radius = 2.5.dp.toPx(), center = o)
            drawText(textMeasurer, labelOf(point.id), topLeft = o + Offset(4.dp.toPx(), -14.dp.toPx()), style = labelStyle)
        }

        // Scale bar: 10 cm at the drawing's scale.
        val barY = size.height - 12.dp.toPx()
        val barStart = Offset(left, barY)
        val barEnd = Offset(left + 10f * scale, barY)
        drawLine(ink, barStart, barEnd, strokeWidth = 2.dp.toPx())
        drawLine(ink, barStart - Offset(0f, 4.dp.toPx()), barStart + Offset(0f, 4.dp.toPx()), strokeWidth = 2.dp.toPx())
        drawLine(ink, barEnd - Offset(0f, 4.dp.toPx()), barEnd + Offset(0f, 4.dp.toPx()), strokeWidth = 2.dp.toPx())
        drawText(textMeasurer, "10 cm", topLeft = barEnd + Offset(6.dp.toPx(), -8.dp.toPx()), style = labelStyle.copy(fontWeight = FontWeight.Normal))
    }
}

@Composable
fun PatternCanvasCard(
    drawing: PatternDrawing,
    unit: UnitSystem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                drawing.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Garis tebal: garis potong. Putus-putus: garis bantu. Garis tipis berwarna: kupnat.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                PatternDrawingCanvas(drawing, Modifier.fillMaxWidth().height(560.dp))
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Rincian ukuran",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(12.dp))
            PatternValueTable(drawing.allValues, unit)
        }
    }
}

@Composable
private fun PatternValueTable(values: Map<String, Double>, unit: UnitSystem) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        values.entries.chunked(2).forEach { rowEntries ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowEntries.forEach { (label, value) ->
                    val factor = if (unit == UnitSystem.INCH) 1 / 2.54 else 1.0
                    val unitLabel = if (unit == UnitSystem.INCH) "in" else "cm"
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "$label: ${((value * factor * 100).toInt() / 100.0)} $unitLabel",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (rowEntries.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
