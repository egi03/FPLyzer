package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.fplyzer.ui.components.charts.LineChart
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.theme.*

private enum class ChartType(val displayName: String) {
    CUMULATIVE_POINTS("Total Points"),
    RANK_PROGRESSION("Overall Rank"),
    LEAGUE_RANK("League Rank")
}

private val managerColors = listOf(
    FplBlue,
    FplGreen,
    FplOrange,
    FplSecondary,
    FplRed,
    FplPink,
    FplYellow,
    FplPurple,
    Color(0xFF2196F3), // Blue
    Color(0xFF4CAF50), // Green
    Color(0xFFFF9800), // Orange
    Color(0xFF9C27B0), // Purple
    Color(0xFFE91E63), // Pink
    Color(0xFF00BCD4), // Cyan
    Color(0xFF795548), // Brown
    Color(0xFF607D8B), // Blue Grey
    Color(0xFF3F51B5), // Indigo
    Color(0xFF009688), // Teal
    Color(0xFFCDDC39), // Lime
    Color(0xFFFF5722)  // Deep Orange
)


private fun getManagerColor(managerId: Int): Color {
    return managerColors[managerId % managerColors.size]
}


private fun createColorMap(
    selectedManagerIds: List<Int>
): List<Color> {
    return selectedManagerIds.map { getManagerColor(it) }
}

@Composable
fun TrendsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    val stats = uiState.leagueStatistics ?: return
    var selectedMembers by remember { mutableStateOf(setOf<String>()) }
    var chartType by remember { mutableStateOf(ChartType.CUMULATIVE_POINTS) }
    var showLimitMessage by remember { mutableStateOf(false) }

    LaunchedEffect(showLimitMessage) {
        if (showLimitMessage) {
            delay(3000)
            showLimitMessage = false
        }
    }

    val managerNameToId = remember(stats) {
        stats.managerStats.values.associate { it.managerName to it.managerId }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Managers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${selectedMembers.size}/5 selected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedMembers.size >= 5) FplOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (selectedMembers.isNotEmpty()) {
                        Surface(
                            onClick = { selectedMembers = emptySet() },
                            shape = RoundedCornerShape(4.dp),
                            color = FplRed.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Clear All",
                                style = MaterialTheme.typography.labelSmall,
                                color = FplRed,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Limit message
            if (showLimitMessage) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = FplOrange.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = FplOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Maximum of 5 managers can be selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplOrange
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stats.managerStats.values.toList()) { member ->
                    val isSelected = selectedMembers.contains(member.managerName)
                    val canSelect = isSelected || selectedMembers.size < 5

                    MemberChip(
                        name = member.managerName,
                        isSelected = isSelected,
                        color = getManagerColor(member.managerId),
                        enabled = canSelect,
                        onClick = {
                            when {
                                isSelected -> {
                                    selectedMembers = selectedMembers - member.managerName
                                }
                                selectedMembers.size < 5 -> {
                                    selectedMembers = selectedMembers + member.managerName
                                }
                                else -> {
                                    showLimitMessage = true
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedMembers.isEmpty()) {
            EmptyChartView()
        } else {
            val selectedManagerData = stats.managerStats.values
                .filter { selectedMembers.contains(it.managerName) }
                .sortedBy { it.managerId }

            // Ensure we have valid data before creating charts
            if (selectedManagerData.isNotEmpty()) {
                val chartColors = selectedManagerData.map { getManagerColor(it.managerId) }

                when (chartType) {
                    ChartType.CUMULATIVE_POINTS -> {
                        val data = selectedManagerData.mapNotNull { manager ->
                            if (manager.pointsHistory.isNotEmpty()) {
                                val cumulativePoints = manager.pointsHistory.runningFold(0) { acc, points -> acc + points }.drop(1)
                                manager.managerId to cumulativePoints
                            } else {
                                null
                            }
                        }.toMap()

                        if (data.isNotEmpty()) {
                            LineChart(
                                data = data,
                                labels = stats.managerStats.mapValues { it.value.managerName },
                                title = "Cumulative Points",
                                colors = chartColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                    ChartType.RANK_PROGRESSION -> {
                        val data = selectedManagerData.mapNotNull { manager ->
                            if (manager.rankHistory.isNotEmpty()) {
                                manager.managerId to manager.rankHistory.map { -it }
                            } else {
                                null
                            }
                        }.toMap()

                        if (data.isNotEmpty()) {
                            LineChart(
                                data = data,
                                labels = stats.managerStats.mapValues { it.value.managerName },
                                title = "Overall Rank Progression",
                                colors = chartColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                    ChartType.LEAGUE_RANK -> {
                        val leagueRankData = calculateLeagueRankProgression(stats)
                        val selectedData = selectedManagerData.mapNotNull { manager ->
                            leagueRankData[manager.managerId]?.let { rankData ->
                                if (rankData.isNotEmpty()) {
                                    manager.managerId to rankData.map { -it }
                                } else {
                                    null
                                }
                            }
                        }.toMap()

                        if (selectedData.isNotEmpty()) {
                            LineChart(
                                data = selectedData,
                                labels = stats.managerStats.mapValues { it.value.managerName },
                                title = "League Rank Progression",
                                colors = chartColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
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
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> color
        enabled -> color.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    }

    val textColor = when {
        isSelected -> Color.White
        enabled -> color
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    Surface(
        onClick = {
            if (enabled) {
                onClick()
            }
        },
        shape = RoundedCornerShape(50),
        color = backgroundColor,
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
                color = textColor,
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) 
            )
            Text(
                text = "Select up to 5 managers to view trends",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant 
            )
            Text(
                text = "Choose managers from the list above to compare their performance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) 
            )
        }
    }
}


private fun calculateLeagueRankProgression(stats: com.example.fplyzer.data.models.statistics.LeagueStatistics): Map<Int, List<Int>> {
    val managers = stats.managerStats.values.toList()

    if (managers.isEmpty()) return emptyMap()

    val maxGameweeks = managers.maxOfOrNull { it.pointsHistory.size } ?: 0
    val leagueRanks = mutableMapOf<Int, MutableList<Int>>()

    managers.forEach { manager ->
        leagueRanks[manager.managerId] = mutableListOf()
    }

    for (gameweek in 1..maxGameweeks) {
        val managerCumulativePoints = managers.mapNotNull { manager ->
            val cumulativePoints = manager.pointsHistory.take(gameweek).sum()
            if (gameweek <= manager.pointsHistory.size) {
                manager.managerId to cumulativePoints
            } else null
        }

        val sortedManagers = managerCumulativePoints.sortedByDescending { it.second }

        sortedManagers.forEachIndexed { index, (managerId, _) ->
            val rank = index + 1
            leagueRanks[managerId]?.add(rank)
        }

        managers.forEach { manager ->
            if (gameweek > manager.pointsHistory.size) {
                val previousRank = leagueRanks[manager.managerId]?.lastOrNull() ?: managers.size
                leagueRanks[manager.managerId]?.add(previousRank)
            }
        }
    }

    return leagueRanks.mapValues { it.value.toList() }
}