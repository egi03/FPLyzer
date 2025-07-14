package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.SortingOption
import com.example.fplyzer.ui.components.ModernChip
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.screens.leagueStats.cards.ManagerRankingCard
import com.example.fplyzer.ui.screens.leagueStats.cards.PerformanceCard

@Composable
fun OverviewTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sorting options
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SortingOption.values()) { option ->
                    ModernChip(
                        text = option.name.replace("_", " ").lowercase().capitalize(),
                        selected = uiState.currentSortOption == option,
                        onClick = { viewModel.setSortOption(option) }
                    )
                }
            }
        }

        // Top performers cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.leagueStatistics?.leagueAverages?.topPerformers?.forEach { metric ->
                    PerformanceCard(
                        modifier = Modifier.weight(1f),
                        metric = metric
                    )
                }
            }
        }

        // Manager rankings
        item {
            Text(
                text = "Manager Rankings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )
        }

        itemsIndexed(uiState.sortedManagers) { index, manager ->
            ManagerRankingCard(
                manager = manager,
                rank = index + 1,
                sortOption = uiState.currentSortOption
            )
        }
    }
}