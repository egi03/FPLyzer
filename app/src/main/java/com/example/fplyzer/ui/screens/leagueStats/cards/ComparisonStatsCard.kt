package com.example.fplyzer.ui.screens.leagueStats.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.ManagerStatistics
import com.example.fplyzer.ui.theme.FplSurface
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary

@Composable
fun ComparisonStatsCard(managers: List<ManagerStatistics>) {
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
                text = "Head to Head Comparison",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            val metrics = listOf(
                "Total Points" to managers.map { it.totalPoints.toFloat() },
                "Average Points" to managers.map { it.averagePoints.toFloat() },
                "Transfers" to managers.map { it.totalTransfers.toFloat() },
                "Hits Taken" to managers.map { it.totalHits.toFloat() },
                "Bench Points" to managers.map { it.benchPoints.toFloat() }
            )

            metrics.forEach { (metric, values) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = metric,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplTextPrimary,
                        modifier = Modifier.width(100.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        values.forEachIndexed { index, value ->
                            val manager = managers[index]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = String.format("%.1f", value),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FplTextPrimary
                                )
                                Text(
                                    text = manager.managerName.split(" ").first(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FplTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

