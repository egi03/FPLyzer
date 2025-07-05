package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.screens.leagueStats.cards.LeagueAveragesCard
import com.example.fplyzer.ui.screens.leagueStats.cards.MonthlyPerformanceCard
import com.example.fplyzer.ui.screens.leagueStats.cards.TransferEfficiencyCard

@Composable
fun AnalyticsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // League averages
        item {
            LeagueAveragesCard(uiState.leagueStatistics?.leagueAverages)
        }

        // Monthly performance
        item {
            MonthlyPerformanceCard(uiState.leagueStatistics)
        }

        // Transfer efficiency
        item {
            TransferEfficiencyCard(uiState.leagueStatistics)
        }
    }
}