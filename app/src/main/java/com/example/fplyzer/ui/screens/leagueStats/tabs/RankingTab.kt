package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.ManagerStatistics
import com.example.fplyzer.data.models.statistics.PerformanceMetric
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplOrange
import com.example.fplyzer.ui.theme.FplPink
import com.example.fplyzer.ui.theme.FplPrimary
import com.example.fplyzer.ui.theme.FplRed
import com.example.fplyzer.ui.theme.FplSecondary
import com.example.fplyzer.ui.theme.FplYellow

@Composable
fun RankingsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    var selectedSortType by remember { mutableStateOf(RankingSortType.AVERAGE_POINTS) }

    Column {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(RankingSortType.values()) { sortType ->
                SortChip(
                    title = sortType.displayName,
                    isSelected = selectedSortType == sortType,
                    onClick = { selectedSortType = sortType }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val sortedManagers = when (selectedSortType) {
                RankingSortType.AVERAGE_POINTS -> uiState.sortedManagers.sortedByDescending { it.averagePoints }
                RankingSortType.CONSISTENCY -> uiState.sortedManagers.sortedBy { it.standardDeviation }
                RankingSortType.BEST_WEEK -> uiState.sortedManagers.sortedByDescending { it.bestWeek?.points ?: 0 }
                RankingSortType.BENCH_WASTE -> uiState.sortedManagers.sortedBy { it.benchPoints }
                RankingSortType.TRANSFERS -> uiState.sortedManagers.sortedByDescending { it.totalTransfers }
            }

            itemsIndexed(sortedManagers) { index, manager ->
                RankingCard(
                    rank = index + 1,
                    statistics = manager,
                    sortType = selectedSortType
                )
            }
        }
    }
}

@Composable
private fun RankingCard(
    rank: Int,
    statistics: ManagerStatistics,
    sortType: RankingSortType
) {
    var isSelected by remember { mutableStateOf(false) }

    val rankColor = when (rank) {
        1 -> FplYellow
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color.Transparent
    }

    Card(
        onClick = { isSelected = !isSelected },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), 
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (rank <= 3) rankColor else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.primary 
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = statistics.teamName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface 
                    )
                    Text(
                        text = statistics.managerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant 
                    )

                    getDescription(statistics, sortType)?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.labelSmall,
                            color = getSortTypeColor(sortType),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Stat Value
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = getStatValue(statistics, sortType),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface 
                    )
                    Text(
                        text = sortType.unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant 
                    )
                }
            }

            // Expanded details
            AnimatedVisibility(visible = isSelected) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant) 
                    Spacer(modifier = Modifier.height(8.dp))

                    if (sortType == RankingSortType.CONSISTENCY) {
                        ConsistencyDetails(statistics)
                    } else {
                        StatisticsBreakdown(statistics)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsistencyDetails(statistics: ManagerStatistics) {
    val consistencyRating = when {
        statistics.standardDeviation < 5 -> "Top tier consistency"
        statistics.standardDeviation < 10 -> "Very reliable"
        statistics.standardDeviation < 18 -> "Moderately consistent"
        statistics.standardDeviation < 30 -> "Inconsistent"
        else -> "Highly unpredictable"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(FplSecondary.copy(alpha = 0.05f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Consistency Analysis",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface 
            )

            Text(
                text = consistencyRating,
                style = MaterialTheme.typography.labelMedium,
                color = FplSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Standard deviation measures how much your scores vary from your ${String.format("%.1f", statistics.averagePoints)} average.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant 
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Best Week",
                value = "${statistics.bestWeek?.points ?: 0} pts"
            )
            StatItem(
                label = "Range",
                value = "${statistics.worstWeek?.points ?: 0}-${statistics.bestWeek?.points ?: 0}"
            )
            StatItem(
                label = "Worst Week",
                value = "${statistics.worstWeek?.points ?: 0} pts"
            )
        }
    }
}

@Composable
private fun StatisticsBreakdown(statistics: ManagerStatistics) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Average",
                value = String.format("%.1f", statistics.averagePoints),
                color = FplGreen
            )
            StatItem(
                label = "Consistency",
                value = String.format("%.1f", statistics.standardDeviation),
                color = FplSecondary
            )
            StatItem(
                label = "Best",
                value = "${statistics.bestWeek?.points ?: 0}",
                color = FplBlue
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Transfers",
                value = "${statistics.totalTransfers}",
                color = Color.Gray
            )
            StatItem(
                label = "Chips Used",
                value = "${statistics.chipsUsed.size}/4",
                color = FplOrange
            )
            StatItem(
                label = "Bench Waste",
                value = String.format("%.1f", statistics.benchPoints.toDouble() / statistics.pointsHistory.size),
                color = FplRed
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
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


// Helpers
private data class RecordData(
    val type: String,
    val manager: PerformanceMetric?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

private enum class RankingSortType(val displayName: String, val unit: String) {
    AVERAGE_POINTS("Average Points", "avg"),
    CONSISTENCY("Consistency", "σ"),
    BEST_WEEK("Best Week", "pts"),
    BENCH_WASTE("Bench Waste", "pts"),
    TRANSFERS("Transfers", "")
}

private fun getStatValue(statistics: ManagerStatistics, sortType: RankingSortType): String {
    return when (sortType) {
        RankingSortType.AVERAGE_POINTS -> String.format("%.1f", statistics.averagePoints)
        RankingSortType.CONSISTENCY -> String.format("%.1f", statistics.standardDeviation)
        RankingSortType.BEST_WEEK -> "${statistics.bestWeek?.points ?: 0}"
        RankingSortType.BENCH_WASTE -> "${statistics.benchPoints}"
        RankingSortType.TRANSFERS -> "${statistics.totalTransfers}"
    }
}

private fun getDescription(statistics: ManagerStatistics, sortType: RankingSortType): String? {
    return when (sortType) {
        RankingSortType.CONSISTENCY -> when {
            statistics.standardDeviation < 10 -> "Rock solid performer"
            statistics.standardDeviation < 18 -> "Fairly consistent"
            else -> "Rollercoaster ride"
        }
        RankingSortType.AVERAGE_POINTS -> when {
            statistics.averagePoints > 60 -> "🔥 Elite performance"
            statistics.averagePoints > 50 -> "✅ Strong manager"
            statistics.averagePoints > 40 -> "📊 Average performance"
            else -> "📉 Needs improvement"
        }
        else -> null
    }
}

private fun getSortTypeColor(sortType: RankingSortType): Color {
    return when (sortType) {
        RankingSortType.CONSISTENCY -> FplSecondary
        RankingSortType.AVERAGE_POINTS -> FplGreen
        RankingSortType.BENCH_WASTE -> FplRed
        RankingSortType.BEST_WEEK -> FplBlue
        RankingSortType.TRANSFERS -> Color.Gray
    }
}

internal fun getColorForMember(id: Int): Color {
    val colors = listOf(FplBlue, FplGreen, FplOrange, FplSecondary, FplRed, FplPink)
    return colors[id % colors.size]
}

@Composable
private fun SortChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) FplPrimary else MaterialTheme.colorScheme.surfaceVariant, 
        modifier = Modifier.animateContentSize()
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, 
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}