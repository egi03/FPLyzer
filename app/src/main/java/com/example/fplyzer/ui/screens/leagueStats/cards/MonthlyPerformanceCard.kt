package com.example.fplyzer.ui.screens.leagueStats.cards

import androidx.compose.foundation.layout.Column
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
import com.example.fplyzer.data.models.statistics.LeagueStatistics
import com.example.fplyzer.ui.theme.FplSurface
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary

@Composable
fun MonthlyPerformanceCard(stats: LeagueStatistics?) {
    stats?.let {
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
                    text = "Monthly Performance Leaders",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Coming soon...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }
        }
    }
}

