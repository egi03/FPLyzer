package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fplyzer.ui.components.ModernChip
import com.example.fplyzer.ui.components.playerAnalytics.CaptaincyCard
import com.example.fplyzer.ui.components.playerAnalytics.OwnershipDistributionChart
import com.example.fplyzer.ui.components.playerAnalytics.PlayerOwnershipCard
import com.example.fplyzer.ui.components.playerAnalytics.TemplateTeamCard
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.theme.FplSecondary

@Composable
fun PlayersTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    if (uiState.isLoadingPlayers) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = FplSecondary)
        }
        return
    }

    val playerAnalytics = uiState.playerAnalytics ?: return

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoBanner()
        }
        // Position filter
        item {
            val positions = listOf("All", "GKP", "DEF", "MID", "FWD")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(positions) { position ->
                    ModernChip(
                        text = position,
                        selected = (position == "All" && uiState.selectedPosition == null) ||
                                position == uiState.selectedPosition,
                        onClick = {
                            viewModel.setSelectedPosition(if (position == "All") null else position)
                        }
                    )
                }
            }
        }

        // Template team
        item {
            val templatePlayers = playerAnalytics.playerOwnership.filter { it.isTemplate }
            val templateOwnership = uiState.leagueStatistics?.managerStats?.values?.count { manager ->
                // Check if manager has all template players
                true // todo: CALCULATE
            }?.toDouble()?.div(uiState.leagueStatistics.managerStats.size) ?: 0.0

            TemplateTeamCard(
                templatePlayers = templatePlayers,
                templateOwnership = templateOwnership
            )
        }

        // Ownership distribution
        item {
            OwnershipDistributionChart(
                playerOwnership = playerAnalytics.playerOwnership,
                position = uiState.selectedPosition
            )
        }

        // Most captained
        item {
            Text(
                text = "Most Captained",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        items(playerAnalytics.captaincy.take(5)) { captaincy ->
            CaptaincyCard(captaincy)
        }

        item {
            Text(
                text = "All Players by Ownership",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val filteredPlayers = if (uiState.selectedPosition != null) {
            playerAnalytics.playerOwnership.filter { it.position == uiState.selectedPosition }
        } else {
            playerAnalytics.playerOwnership
        }

        items(filteredPlayers) { player ->
            PlayerOwnershipCard(player)
        }
    }
}