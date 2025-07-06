package com.example.fplyzer.ui.components.charts

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplDivider
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplOrange
import com.example.fplyzer.ui.theme.FplPink
import com.example.fplyzer.ui.theme.FplRed
import com.example.fplyzer.ui.theme.FplSecondary
import com.example.fplyzer.ui.theme.FplSurface
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary
import com.example.fplyzer.ui.theme.FplYellow


@Composable
fun ChipSuccessChart(
    chipData: Map<String, List<Pair<String, Int>>>, // Chip type to list of (manager, points)
    modifier: Modifier = Modifier
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
            Text(
                text = "Chip Success Rate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            val chipColors = mapOf(
                "wildcard" to FplGreen,
                "bboost" to FplBlue,
                "3xc" to FplYellow,
                "freehit" to FplPink
            )

            chipData.forEach { (chipType, usages) ->
                if (usages.isNotEmpty()) {
                    val avgPoints = usages.map { it.second }.average()
                    val color = chipColors[chipType] ?: FplSecondary

                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getChipDisplayName(chipType),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = String.format("%.1f avg pts", avgPoints),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Usage distribution
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(FplDivider.copy(alpha = 0.3f))
                            )

                            Row(modifier = Modifier.fillMaxSize()) {
                                usages.sortedByDescending { it.second }.forEach { (_, points) ->
                                    val width = (points / 150f).coerceIn(0f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(width)
                                            .background(
                                                when {
                                                    points >= 100 -> FplGreen
                                                    points >= 70 -> FplBlue
                                                    points >= 50 -> FplOrange
                                                    else -> FplRed
                                                }
                                            )
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${usages.size} managers used",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplTextSecondary
                        )
                    }
                }
            }
        }
    }
}

private fun getChipDisplayName(chipType: String): String {
    return when (chipType) {
        "wildcard" -> "Wildcard"
        "bboost" -> "Bench Boost"
        "3xc" -> "Triple Captain"
        "freehit" -> "Free Hit"
        else -> chipType
    }
}