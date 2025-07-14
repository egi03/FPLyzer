package com.example.fplyzer.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplOrange
import com.example.fplyzer.ui.theme.FplRed

@Composable
fun ConsistencyChart(
    data: List<Pair<String, Double>>, // Manager name to standard deviation
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
                text = "Consistency Analysis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ConsistencyLegendItem("0-5", "Elite", FplGreen)
                ConsistencyLegendItem("5-8", "Good", FplBlue)
                ConsistencyLegendItem("8-12", "Average", FplOrange)
                ConsistencyLegendItem("12+", "Poor", FplRed)
            }

            Spacer(modifier = Modifier.height(16.dp))

            data.forEach { (manager, stdDev) ->
                val color = when {
                    stdDev < 5 -> FplGreen
                    stdDev < 8 -> FplBlue
                    stdDev < 12 -> FplOrange
                    else -> FplRed
                }

                val consistencyText = when {
                    stdDev < 5 -> "Elite consistency"
                    stdDev < 8 -> "Good consistency"
                    stdDev < 12 -> "Average consistency"
                    else -> "Poor consistency"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = manager,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = consistencyText,
                            style = MaterialTheme.typography.bodySmall,
                            color = color
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(24.dp)
                    ) {
                        // Background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )

                        // Value bar
                        val animatedWidth by animateFloatAsState(
                            targetValue = (stdDev / 20f).coerceIn(0.0, 1.0).toFloat(),
                            animationSpec = tween(800)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedWidth)
                                .clip(RoundedCornerShape(12.dp))
                                .background(color)
                        )
                    }

                    Text(
                        text = String.format("%.1f", stdDev),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsistencyLegendItem(
    range: String,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Column {
            Text(
                text = range,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}