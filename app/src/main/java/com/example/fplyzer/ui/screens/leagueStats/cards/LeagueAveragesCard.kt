package com.example.fplyzer.ui.screens.leagueStats.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.LeagueAverages
import com.example.fplyzer.ui.theme.FplSurface
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary

@Composable
fun LeagueAveragesCard(averages: LeagueAverages?) {
    averages?.let {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = FplSurface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "League Averages",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                val stats = listOf(
                    "Points per GW" to it.averagePoints,
                    "Transfers per Manager" to it.averageTransfers,
                    "Hits per Manager" to it.averageHits,
                    "Team Value" to it.averageTeamValue / 10.0,
                    "Bench Points" to it.averageBenchPoints
                )

                stats.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplTextSecondary
                        )
                        Text(
                            text = if (label.contains("Value")) "£${String.format("%.1f", value)}m"
                            else String.format("%.1f", value),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = FplTextPrimary
                        )
                    }
                }
            }
        }
    }
}
