package com.smartkebaya.pattern.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.smartkebaya.pattern.util.PlatformUtil
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkebaya.pattern.model.PatternDrawing
import com.smartkebaya.pattern.model.UnitSystem
import org.jetbrains.compose.resources.DrawableResource

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

@Composable
fun PatternCanvasCard(
    drawing: PatternDrawing,
    imageRes: DrawableResource,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    drawing.title.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "PREVIEW POLA",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = drawing.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                
                val currentLabels = labelPositions[drawing.title]
                if (currentLabels != null) {
                    drawing.allValues.forEach { (key, value) ->
                        currentLabels[key]?.let { pos ->
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = (this.maxWidth * pos.first) - 20.dp,
                                        y = (this.maxHeight * pos.second) - 10.dp
                                    ),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(4.dp),
                                shadowElevation = 2.dp
                            ) {
                                Text(
                                    text = "$key: ${((value * 10).toInt() / 10.0)}",
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(4.dp, 16.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)))
                Spacer(Modifier.width(8.dp))
                Text(
                    "KETERANGAN UKURAN DETAIL",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.5.sp
                )
            }
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
                    val displayValue = value * factor
                    val unitLabel = if (unit == UnitSystem.INCH) "in" else "cm"
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "$label: ${((displayValue * 100).toInt() / 100.0)} $unitLabel",
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
