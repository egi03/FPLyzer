package com.example.fplyzer.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fplyzer.ui.theme.FplAccent
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplDivider
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplOrange
import com.example.fplyzer.ui.theme.FplSecondary
import com.example.fplyzer.ui.theme.FplSurface
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary


@Composable
fun LineChart(
    data: Map<Int, List<Int>>, // Manager ID to points history
    labels: Map<Int, String>, // Manager ID to name
    modifier: Modifier = Modifier,
    title: String = "",
    colors: List<Color> = listOf(FplAccent, FplSecondary, FplBlue, FplGreen, FplOrange)
) {
    var selectedPoint by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                val width = size.width
                val height = size.height
                val padding = 40.dp.toPx()
                val chartWidth = width - (2 * padding)
                val chartHeight = height - (2 * padding)

                val allPoints = data.values.flatten()
                val minValue = allPoints.minOrNull() ?: 0
                val maxValue = allPoints.maxOrNull() ?: 100
                val valueRange = maxValue - minValue

                // Draw grid lines
                val gridLines = 5
                for (i in 0..gridLines) {
                    val y = padding + (chartHeight * i / gridLines)
                    drawLine(
                        color = FplDivider,
                        start = Offset(padding, y),
                        end = Offset(width - padding, y),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw value labels
                    val value = maxValue - (valueRange * i / gridLines)
                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            textSize = 10.sp.toPx()
                            color = FplTextSecondary.toArgb()
                        }
                        canvas.nativeCanvas.drawText(
                            value.toString(),
                            padding - 30.dp.toPx(),
                            y + 4.dp.toPx(),
                            paint
                        )
                    }
                }

                // Draw lines for each manager
                data.entries.forEachIndexed { index, (managerId, points) ->
                    if (points.isNotEmpty()) {
                        val color = colors[index % colors.size]
                        val path = Path()

                        points.forEachIndexed { gwIndex, point ->
                            val x = padding + (chartWidth * gwIndex / (points.size - 1))
                            val y = padding + chartHeight * (1 - (point - minValue).toFloat() / valueRange)

                            if (gwIndex == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }

                            drawCircle(
                                color = color,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            // Legend
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                data.entries.forEachIndexed { index, (managerId, _) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(colors[index % colors.size])
                        )
                        Text(
                            text = labels[managerId] ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplTextSecondary
                        )
                    }
                }
            }
        }
    }
}