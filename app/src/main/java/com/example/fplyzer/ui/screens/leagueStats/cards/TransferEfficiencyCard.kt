package com.example.fplyzer.ui.screens.leagueStats.cards

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fplyzer.data.models.statistics.LeagueStatistics
import com.example.fplyzer.ui.components.charts.BarChart
import com.example.fplyzer.ui.theme.FplGreen

@Composable
fun TransferEfficiencyCard(stats: LeagueStatistics?) {
    stats?.let {
        val data = it.managerStats.values.map { manager ->
            val efficiency = if (manager.totalTransfers > 0) {
                (manager.totalPoints - manager.totalHits) / manager.totalTransfers.toFloat()
            } else 0f
            manager.managerName to efficiency
        }.sortedByDescending { it.second }.take(10)

        BarChart(
            data = data,
            title = "Transfer Efficiency (Points per Transfer)",
            barColor = FplGreen,
            modifier = Modifier.fillMaxWidth()
        )
    }
}