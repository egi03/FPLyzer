package com.example.fplyzer.ui.components.charts

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fplyzer.data.models.statistics.ManagerStatistics
import com.example.fplyzer.ui.theme.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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

@SuppressLint("DefaultLocale")
@Composable
fun BarChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    title: String = "",
    barColor: Color = FplAccent,
    showValues: Boolean = true
) {
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

            val maxValue = data.maxOfOrNull { it.second } ?: 1f

            data.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplTextPrimary,
                        modifier = Modifier.width(120.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    ) {
                        // Background bar
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(FplDivider.copy(alpha = 0.3f))
                        )

                        // Value bar with animation
                        val animatedValue by animateFloatAsState(
                            targetValue = value / maxValue,
                            animationSpec = tween(
                                durationMillis = 800,
                                easing = FastOutSlowInEasing
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedValue)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(barColor, barColor.copy(alpha = 0.8f))
                                    )
                                )
                        )
                    }

                    if (showValues) {
                        Text(
                            text = String.format("%.1f", value),
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplTextSecondary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RadarChart(
    data: Map<String, Float>, // Metric name to normalized value (0-1)
    modifier: Modifier = Modifier,
    title: String = "",
    lineColor: Color = FplAccent,
    fillColor: Color = FplAccent.copy(alpha = 0.3f)
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier.size(200.dp)
            ) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = min(centerX, centerY) * 0.8f
                val numAxes = data.size

                if (numAxes < 3) return@Canvas

                // Draw grid circles
                for (i in 1..5) {
                    drawCircle(
                        color = FplDivider,
                        radius = radius * i / 5,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Draw axes
                val angleStep = 360f / numAxes
                data.entries.forEachIndexed { index, _ ->
                    val angle = Math.toRadians((angleStep * index - 90).toDouble())
                    val endX = centerX + radius * kotlin.math.cos(angle).toFloat()
                    val endY = centerY + radius * kotlin.math.sin(angle).toFloat()

                    drawLine(
                        color = FplDivider,
                        start = Offset(centerX, centerY),
                        end = Offset(endX, endY),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw data polygon
                val path = Path()
                data.entries.forEachIndexed { index, (_, value) ->
                    val angle = Math.toRadians((angleStep * index - 90).toDouble())
                    val x = centerX + radius * value * kotlin.math.cos(angle).toFloat()
                    val y = centerY + radius * value * kotlin.math.sin(angle).toFloat()

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                path.close()

                // Fill polygon
                drawPath(
                    path = path,
                    color = fillColor
                )

                // Draw polygon outline
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Draw data points
                data.entries.forEachIndexed { index, (_, value) ->
                    val angle = Math.toRadians((angleStep * index - 90).toDouble())
                    val x = centerX + radius * value * kotlin.math.cos(angle).toFloat()
                    val y = centerY + radius * value * kotlin.math.sin(angle).toFloat()

                    drawCircle(
                        color = lineColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            // Labels
            Spacer(modifier = Modifier.height(16.dp))
            data.entries.forEach { (metric, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = metric,
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                    Text(
                        text = "${(value * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    title: String = "",
    colors: List<Color> = listOf(FplAccent, FplSecondary, FplBlue, FplGreen, FplOrange, FplPink)
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

            val total = data.sumOf { it.second.toDouble() }.toFloat()
            var startAngle = -90f

            Canvas(
                modifier = Modifier.size(200.dp)
            ) {
                val radius = size.minDimension / 2
                val center = Offset(size.width / 2, size.height / 2)

                data.forEachIndexed { index, (_, value) ->
                    val sweepAngle = (value / total) * 360f
                    val color = colors[index % colors.size]

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    startAngle += sweepAngle
                }
            }

            // Legend
            Spacer(modifier = Modifier.height(16.dp))
            data.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(colors[index % colors.size])
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplTextPrimary
                        )
                    }
                    Text(
                        text = "${((value / total) * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplTextSecondary
                    )
                }
            }
        }
    }
}