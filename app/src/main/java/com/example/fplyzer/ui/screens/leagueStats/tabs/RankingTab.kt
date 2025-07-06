package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.*
import com.example.fplyzer.ui.components.charts.*
import com.example.fplyzer.ui.theme.*
import com.example.fplyzer.ui.screens.leagueStats.*

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
                RankingSortType.CAPTAIN_SUCCESS -> uiState.sortedManagers.sortedByDescending { it.captainPoints }
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
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (rank <= 3) rankColor else FplChipBackground
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (rank <= 3) Color.White else FplChipText
                    )
                }

                // Manager Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = statistics.teamName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = statistics.managerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplTextSecondary
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
                        color = FplTextPrimary
                    )
                    Text(
                        text = sortType.unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
            }

            // Expanded details
            AnimatedVisibility(visible = isSelected) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Divider(color = FplDivider)
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
        statistics.standardDeviation < 8 -> "Very reliable"
        statistics.standardDeviation < 12 -> "Moderately consistent"
        statistics.standardDeviation < 18 -> "Inconsistent"
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
                fontWeight = FontWeight.Bold
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
            color = FplTextSecondary
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
    color: Color = FplTextPrimary
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
            color = FplTextSecondary
        )
    }
}

// Trends Tab - Matching iOS TrendsTab
@Composable
fun TrendsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    val stats = uiState.leagueStatistics ?: return
    var selectedMembers by remember { mutableStateOf(setOf<String>()) }
    var chartType by remember { mutableStateOf(ChartType.CUMULATIVE_POINTS) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chart type selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ChartType.values().forEach { type ->
                FilterChip(
                    selected = chartType == type,
                    onClick = { chartType = type },
                    label = {
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // Member selector
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stats.managerStats.values.take(10).toList()) { member ->
                MemberChip(
                    name = member.managerName,
                    isSelected = selectedMembers.contains(member.managerName),
                    color = getColorForMember(member.managerId),
                    onClick = {
                        selectedMembers = if (selectedMembers.contains(member.managerName)) {
                            selectedMembers - member.managerName
                        } else {
                            selectedMembers + member.managerName
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart
        if (selectedMembers.isEmpty()) {
            EmptyChartView()
        } else {
            when (chartType) {
                ChartType.CUMULATIVE_POINTS -> {
                    val data = stats.managerStats.values
                        .filter { selectedMembers.contains(it.managerName) }
                        .associate { manager ->
                            manager.managerId to manager.pointsHistory.runningFold(0) { acc, points -> acc + points }.drop(1)
                        }

                    LineChart(
                        data = data,
                        labels = stats.managerStats.mapValues { it.value.managerName },
                        title = "Cumulative Points",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                ChartType.GAMEWEEK_POINTS -> {
                    val data = stats.managerStats.values
                        .filter { selectedMembers.contains(it.managerName) }
                        .associate { it.managerId to it.pointsHistory }

                    LineChart(
                        data = data,
                        labels = stats.managerStats.mapValues { it.value.managerName },
                        title = "Gameweek Points",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                ChartType.RANK_PROGRESSION -> {
                    val data = stats.managerStats.values
                        .filter { selectedMembers.contains(it.managerName) }
                        .associate { it.managerId to it.rankHistory.map { -it } }

                    LineChart(
                        data = data,
                        labels = stats.managerStats.mapValues { it.value.managerName },
                        title = "Rank Progression",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MemberChip(
    name: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) color else color.copy(alpha = 0.2f),
        modifier = Modifier.animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.White else color,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyChartView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShowChart,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.Gray
            )
            Text(
                text = "Select members to view trends",
                style = MaterialTheme.typography.bodyLarge,
                color = FplTextSecondary
            )
        }
    }
}

// Helper functions and data classes
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
    CAPTAIN_SUCCESS("Captain Success", "%"),
    BENCH_WASTE("Bench Waste", "pts"),
    TRANSFERS("Transfers", "")
}

private enum class ChartType(val displayName: String) {
    CUMULATIVE_POINTS("Total Points"),
    GAMEWEEK_POINTS("GW Points"),
    RANK_PROGRESSION("Rank")
}

private fun getStatValue(statistics: ManagerStatistics, sortType: RankingSortType): String {
    return when (sortType) {
        RankingSortType.AVERAGE_POINTS -> String.format("%.1f", statistics.averagePoints)
        RankingSortType.CONSISTENCY -> String.format("%.1f", statistics.standardDeviation)
        RankingSortType.BEST_WEEK -> "${statistics.bestWeek?.points ?: 0}"
        RankingSortType.CAPTAIN_SUCCESS -> String.format("%.1f", 0.0) // TODO caluclate
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
        RankingSortType.CAPTAIN_SUCCESS -> FplOrange
        RankingSortType.BENCH_WASTE -> FplRed
        RankingSortType.BEST_WEEK -> FplBlue
        RankingSortType.TRANSFERS -> Color.Gray
    }
}

private fun getColorForMember(id: Int): Color {
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
        color = if (isSelected) FplPrimary else FplChipBackground,
        modifier = Modifier.animateContentSize()
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.White else FplChipText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}