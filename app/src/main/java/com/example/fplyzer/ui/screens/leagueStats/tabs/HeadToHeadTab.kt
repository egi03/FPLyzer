package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.screens.leagueStats.cards.ComparisonStatsCard
import com.example.fplyzer.ui.screens.leagueStats.cards.SelectableManagerChip
import com.example.fplyzer.ui.theme.FplTextSecondary

@Composable
fun HeadToHeadTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Select managers to compare (max 5)",
                style = MaterialTheme.typography.titleMedium,
                color = FplTextSecondary
            )
        }

        // Manager selection
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.leagueStatistics?.managerStats?.values?.toList() ?: emptyList()) { manager ->
                    SelectableManagerChip(
                        manager = manager,
                        isSelected = uiState.selectedManagerIds.contains(manager.managerId),
                        onClick = { viewModel.toggleManagerSelection(manager.managerId) }
                    )
                }
            }
        }

        if (uiState.selectedManagerIds.size >= 2) {
            val selectedManagers = uiState.selectedManagerIds.mapNotNull { id ->
                uiState.leagueStatistics?.managerStats?.get(id)
            }

            // Comparison stats
            item {
                ComparisonStatsCard(selectedManagers)
            }
        }
    }
}
