package com.example.fplyzer.ui.components.playerAnalytics

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fplyzer.data.models.*
import com.example.fplyzer.data.models.statistics.*
import com.example.fplyzer.ui.components.GradientCard
import com.example.fplyzer.ui.theme.*

@Composable
fun PlayerTrendsCard(
    trends: TransferTrends,
    onPlayerClick: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Transfer Trends",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price risers
            if (trends.priceRisers.isNotEmpty()) {
                TrendSection(
                    title = "Price Risers",
                    icon = Icons.Default.TrendingUp,
                    color = FplGreen,
                    items = trends.priceRisers.take(3).map { price ->
                        TrendItem(
                            playerId = price.playerId,
                            playerName = price.playerName,
                            value = "+£${String.format("%.1f", price.totalChange)}m",
                            subtitle = "${price.leagueOwnership.toInt()}% owned"
                        )
                    },
                    onItemClick = onPlayerClick
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Bandwagons
            if (trends.bandwagons.isNotEmpty()) {
                TrendSection(
                    title = "Bandwagon Alert",
                    icon = Icons.Default.LocalFireDepartment,
                    color = FplOrange,
                    items = trends.bandwagons.take(3).map { bandwagon ->
                        TrendItem(
                            playerId = bandwagon.playerId,
                            playerName = bandwagon.playerName,
                            value = "+${bandwagon.managersJoining}",
                            subtitle = "managers joining"
                        )
                    },
                    onItemClick = onPlayerClick
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Knee-jerk transfers
            if (trends.kneejerk.isNotEmpty()) {
                TrendSection(
                    title = "Knee-jerk Reactions",
                    icon = Icons.Default.SwapHoriz,
                    color = FplRed,
                    items = trends.kneejerk.take(3).map { kneejerk ->
                        TrendItem(
                            playerId = kneejerk.playerIn,
                            playerName = "${kneejerk.playerInName} IN",
                            value = kneejerk.playerOutName,
                            subtitle = "OUT • ${kneejerk.managersCount} managers"
                        )
                    },
                    onItemClick = onPlayerClick
                )
            }
        }
    }
}

@Composable
private fun TrendSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    items: List<TrendItem>,
    onItemClick: (Int) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item.playerId) }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.playerName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
                Text(
                    text = item.value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

private data class TrendItem(
    val playerId: Int,
    val playerName: String,
    val value: String,
    val subtitle: String
)

@Composable
fun PositionStrategyCard(
    positionAnalysis: Map<String, PositionAnalysis>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Position Strategy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            val positions = listOf("GKP", "DEF", "MID", "FWD")
            positions.forEach { position ->
                positionAnalysis[position]?.let { analysis ->
                    PositionStrategyRow(
                        position = position,
                        analysis = analysis
                    )
                    if (position != "FWD") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = FplDivider)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PositionStrategyRow(
    position: String,
    analysis: PositionAnalysis
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when (position) {
                                "GKP" -> Color(0xFFFFD700)
                                "DEF" -> Color(0xFF00D685)
                                "MID" -> Color(0xFF05F1FF)
                                "FWD" -> Color(0xFFE90052)
                                else -> FplPrimary
                            }.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = position,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (position) {
                            "GKP" -> Color(0xFFFFD700)
                            "DEF" -> Color(0xFF00D685)
                            "MID" -> Color(0xFF05F1FF)
                            "FWD" -> Color(0xFFE90052)
                            else -> FplPrimary
                        }
                    )
                }
                Text(
                    text = "£${String.format("%.1f", analysis.positionTrends.averageSpend)}m avg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                analysis.bestValue.firstOrNull()?.let { player ->
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Best Value",
                            style = MaterialTheme.typography.labelSmall,
                            color = FplTextSecondary
                        )
                        Text(
                            text = player.playerName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BenchAnalysisCard(
    benchAnalysis: BenchAnalysis
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bench Analysis",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${benchAnalysis.totalBenchPoints} pts",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = FplOrange
                    )
                    Text(
                        text = "Total bench points",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Most benched players
            if (benchAnalysis.mostBenchedPlayers.isNotEmpty()) {
                Text(
                    text = "Most Benched",
                    style = MaterialTheme.typography.titleMedium,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                benchAnalysis.mostBenchedPlayers.take(3).forEach { benched ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = benched.playerName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${benched.benchCount}x • ${benched.pointsMissed} pts missed",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplError
                        )
                    }
                }
            }

            // Costliest benching
            benchAnalysis.costliestBenching.firstOrNull()?.let { costly ->
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(FplError.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Costliest Benching",
                            style = MaterialTheme.typography.labelMedium,
                            color = FplError
                        )
                        Text(
                            text = "${costly.playerName} • GW${costly.gameweek}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "by ${costly.managerName} • ${costly.pointsBenched} pts",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerRadarComparison(
    players: List<PlayerLeaguePerformance>,
    selectedPlayerIds: Set<Int>
) {
    val selectedPlayers = players.filter { selectedPlayerIds.contains(it.playerId) }
    if (selectedPlayers.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Player Performance Radar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier.size(250.dp)
            ) {
                drawPlayerRadar(selectedPlayers)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            selectedPlayers.forEachIndexed { index, player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(getPlayerColor(index))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = player.playerName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawPlayerRadar(
    players: List<PlayerLeaguePerformance>
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = size.minDimension / 2 * 0.8f

    val metrics = listOf(
        "Points", "Consistency", "Explosiveness",
        "Ownership ROI", "Captain ROI"
    )
    val numMetrics = metrics.size
    val angleStep = 360f / numMetrics

    // Draw grid
    for (i in 1..5) {
        val gridRadius = radius * i / 5
        val path = Path()

        for (j in 0 until numMetrics) {
            val angle = Math.toRadians((angleStep * j - 90).toDouble())
            val x = centerX + gridRadius * kotlin.math.cos(angle).toFloat()
            val y = centerY + gridRadius * kotlin.math.sin(angle).toFloat()

            if (j == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }
        path.close()

        drawPath(
            path = path,
            color = FplDivider,
            style = Stroke(width = 1f)
        )
    }

    // Draw axes
    for (i in 0 until numMetrics) {
        val angle = Math.toRadians((angleStep * i - 90).toDouble())
        val endX = centerX + radius * kotlin.math.cos(angle).toFloat()
        val endY = centerY + radius * kotlin.math.sin(angle).toFloat()

        drawLine(
            color = FplDivider,
            start = Offset(centerX, centerY),
            end = Offset(endX, endY),
            strokeWidth = 1f
        )
    }

//    // Draw player data
//    players.forEachIndexed { playerIndex, player ->
//        val path = Path()
//        val values = listOf(
//            player.totalPoints / 100f,
//            1f - player.consistency.toFloat(),
//            player.explosiveness.toFloat(),
//            player.ownershipWeightedPoints / 100f,
//            player.captaincyROI.toFloat()
//        ).map { it.coerceIn(0f, 1f) }
//
//        values.forEachIndexed { index, value ->
//            val angle = Math.toRadians((angleStep * index - 90).toDouble())
//            val x = centerX + radius * value * kotlin.math.cos(angle).toFloat()
//            val y = centerY + radius * value * kotlin.math.sin(angle).toFloat()
//
//            if (index == 0) path.moveTo(x, y)
//            else path.lineTo(x, y)
//        }
//        path.close()
//
//        val color = getPlayerColor(playerIndex)
//
//        // Fill
//        drawPath(
//            path = path,
//            color = color.copy(alpha = 0.3f)
//        )
//
//        // Outline
//        drawPath(
//            path = path,
//            color = color,
//            style = Stroke(width = 2f)
//        )
//    }
}

private fun getPlayerColor(index: Int): Color {
    return when (index) {
        0 -> FplAccent
        1 -> FplSecondary
        2 -> FplBlue
        3 -> FplOrange
        else -> FplPink
    }
}