package com.example.fplyzer.ui.screens.leagueStats.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.ManagerStatistics
import com.example.fplyzer.data.models.statistics.SortingOption
import com.example.fplyzer.ui.theme.FplChipBackground
import com.example.fplyzer.ui.theme.FplChipText
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplOrange
import com.example.fplyzer.ui.theme.FplRed
import com.example.fplyzer.ui.theme.FplSurface
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary
import com.example.fplyzer.ui.theme.FplYellow

@Composable
fun ManagerRankingCard(
    manager: ManagerStatistics,
    rank: Int,
    sortOption: SortingOption
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> Brush.radialGradient(listOf(FplYellow, FplOrange))
                            2 -> Brush.radialGradient(listOf(Color(0xFFC0C0C0), Color(0xFF9E9E9E)))
                            3 -> Brush.radialGradient(listOf(Color(0xFFCD7F32), Color(0xFFA0522D)))
                            else -> Brush.radialGradient(listOf(FplChipBackground, FplChipBackground))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) Color.White else FplChipText
                )
            }

            // Manager info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = manager.teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = manager.managerName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }

            // Stat based on sort option
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val (value, label) = when (sortOption) {
                    SortingOption.TOTAL_POINTS -> "${manager.totalPoints}" to "pts"
                    SortingOption.AVERAGE_POINTS -> String.format("%.1f", manager.averagePoints) to "avg"
                    SortingOption.CONSISTENCY -> String.format("%.2f", manager.consistency) to "con"
                    SortingOption.BEST_WEEK -> "${manager.bestWeek?.points ?: 0}" to "best"
                    SortingOption.WORST_WEEK -> "${manager.worstWeek?.points ?: 0}" to "worst"
                    SortingOption.FORM -> "${manager.currentStreak}" to "form"
                    SortingOption.TRANSFERS -> "${manager.totalTransfers}" to "trans"
                    SortingOption.TEAM_VALUE -> "£${manager.teamValue / 10.0}m" to ""
                    SortingOption.BENCH_POINTS -> "${manager.benchPoints}" to "bench"
                    SortingOption.CAPTAIN_POINTS -> "${manager.captainPoints}" to "cap"
                }

                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = when (sortOption) {
                        SortingOption.FORM -> if (manager.currentStreak > 0) FplGreen else FplRed
                        else -> FplTextPrimary
                    }
                )
                if (label.isNotEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = FplTextSecondary
                    )
                }
            }
        }
    }
}