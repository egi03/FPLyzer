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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fplyzer.ui.components.charts.LineChart
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.theme.FplTextSecondary

private enum class ChartType(val displayName: String) {
    CUMULATIVE_POINTS("Total Points"),
    GAMEWEEK_POINTS("GW Points"),
    RANK_PROGRESSION("Rank")
}

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