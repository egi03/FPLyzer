package com.example.fplyzer.ui.screens.leagueStats.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.PerformanceMetric
import com.example.fplyzer.ui.components.GlassmorphicCard
import com.example.fplyzer.ui.theme.FplSecondary
import com.example.fplyzer.ui.theme.FplTextPrimary
import com.example.fplyzer.ui.theme.FplTextSecondary

@Composable
fun PerformanceCard(
    modifier: Modifier = Modifier,
    metric: PerformanceMetric
) {
    GlassmorphicCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = metric.metric,
                style = MaterialTheme.typography.labelMedium,
                color = FplTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%.1f", metric.value),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = FplSecondary
            )
            Text(
                text = metric.managerName,
                style = MaterialTheme.typography.bodySmall,
                color = FplTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
