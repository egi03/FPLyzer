package com.example.fplyzer.ui.screens.leagueStats.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.LeagueStatistics
import com.example.fplyzer.ui.components.GradientCard
import com.example.fplyzer.ui.theme.FplAccentLight
import com.example.fplyzer.ui.theme.FplSecondary
import com.example.fplyzer.ui.theme.FplSecondaryDark

@Composable
fun LeagueHeaderCard(stats: LeagueStatistics) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        gradientColors = listOf(FplSecondary, FplSecondaryDark)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stats.leagueInfo.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stats.standings.size} Managers",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FplAccentLight
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = String.format("%.1f", stats.leagueAverages.averagePoints),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Avg Points/GW",
                    style = MaterialTheme.typography.bodySmall,
                    color = FplAccentLight
                )
            }
        }
    }
}

