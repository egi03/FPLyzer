package com.example.fplyzer.ui.components.charts

import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fplyzer.ui.theme.FplAccent
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplOrange
import com.example.fplyzer.ui.theme.FplPink
import com.example.fplyzer.ui.theme.FplSecondary

@Composable
fun GameweekPointsChart(
    gameweekData: Map<Int, List<Pair<String, Int>>>, // GW to list of (manager, points)
    selectedManagers: Set<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Gameweek Performance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Extract theme colors before Canvas
            val gridLineColor = MaterialTheme.colorScheme.outline
            val gridTextColor = MaterialTheme.colorScheme.onSurfaceVariant

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

                // Get data for selected managers
                val managerData = mutableMapOf<String, List<Int>>()
                gameweekData.forEach { (gw, managerPoints) ->
                    managerPoints.forEach { (manager, points) ->
                        if (selectedManagers.contains(manager)) {
                            managerData.getOrPut(manager) { mutableListOf() }
                                .let { (it as MutableList).add(points) }
                        }
                    }
                }

                if (managerData.isEmpty()) return@Canvas

                val allPoints = managerData.values.flatten()
                val minPoints = allPoints.minOrNull() ?: 0
                val maxPoints = allPoints.maxOrNull() ?: 100
                val pointsRange = maxPoints - minPoints

                // Use theme-aware colors for grid
                drawGrid(
                    padding,
                    chartWidth,
                    chartHeight,
                    minPoints,
                    maxPoints,
                    lineColor = gridLineColor,
                    textColor = gridTextColor
                )

                // Draw lines for each manager
                val colors = listOf(FplAccent, FplSecondary, FplBlue, FplOrange, FplPink)
                managerData.entries.forEachIndexed { index, (manager, points) ->
                    val color = colors[index % colors.size]
                    val path = Path()

                    points.forEachIndexed { gwIndex, point ->
                        val x = padding + (chartWidth * gwIndex / (points.size - 1).coerceAtLeast(1))
                        val y = padding + chartHeight * (1 - (point - minPoints).toFloat() / pointsRange)

                        if (gwIndex == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }

                        // Draw point
                        drawCircle(
                            color = color,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y),
                            style = Fill
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = Offset(x, y),
                            style = Fill
                        )
                    }

                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // Legend
            if (selectedManagers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val colors = listOf(FplAccent, FplSecondary, FplBlue, FplOrange, FplPink)
                    selectedManagers.forEachIndexed { index, manager ->
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
                                text = manager,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGrid(
    padding: Float,
    chartWidth: Float,
    chartHeight: Float,
    minValue: Int,
    maxValue: Int,
    lineColor: Color,
    textColor: Color
) {
    val gridLines = 5
    val valueRange = maxValue - minValue

    for (i in 0..gridLines) {
        val y = padding + (chartHeight * i / gridLines)
        drawLine(
            color = lineColor,
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = 1.dp.toPx()
        )

        val value = maxValue - (valueRange * i / gridLines)
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                textSize = 10.sp.toPx()
                color = textColor.toArgb()
                typeface = Typeface.DEFAULT
            }
            canvas.nativeCanvas.drawText(
                value.toString(),
                padding - 30.dp.toPx(),
                y + 4.dp.toPx(),
                paint
            )
        }
    }
}