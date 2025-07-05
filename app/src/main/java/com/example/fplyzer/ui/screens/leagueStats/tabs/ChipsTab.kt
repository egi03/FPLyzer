package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.screens.leagueStats.cards.ChipSectionCard
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplRed
import com.example.fplyzer.ui.theme.FplYellow

@Composable
fun ChipsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val chipAnalysis = uiState.leagueStatistics?.chipAnalysis

        // Best chips section
        item {
            ChipSectionCard(
                title = "Best Wildcard Usage",
                chips = chipAnalysis?.bestWildcards ?: emptyList(),
                icon = Icons.Default.SwapHoriz,
                color = FplGreen
            )
        }

        item {
            ChipSectionCard(
                title = "Best Bench Boost",
                chips = chipAnalysis?.bestBenchBoosts ?: emptyList(),
                icon = Icons.Default.Groups,
                color = FplBlue
            )
        }

        item {
            ChipSectionCard(
                title = "Best Triple Captain",
                chips = chipAnalysis?.bestTripleCaptains ?: emptyList(),
                icon = Icons.Default.Stars,
                color = FplYellow
            )
        }

        item {
            ChipSectionCard(
                title = "Worst Chip Usage",
                chips = chipAnalysis?.worstChips ?: emptyList(),
                icon = Icons.Default.Error,
                color = FplRed
            )
        }
    }
}
