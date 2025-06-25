package com.example.fplyzer.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fplyzer.data.models.statistics.*
import com.example.fplyzer.ui.theme.*

@Composable
fun LeaguePlayerInsightsCard(
    playerAnalytics: LeaguePlayerAnalytics?,
    onNavigateToDetails: () -> Unit
) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        gradientColors = listOf(FplPrimary, FplPrimaryDark),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "PLAYER INSIGHTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = FplAccentLight,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "League Analytics",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onNavigateToDetails,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(FplGlass.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "View Details",
                        tint = FplAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (playerAnalytics != null) {
                // Key insights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InsightItem(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Groups,
                        label = "Template Size",
                        value = "${playerAnalytics.templateTeam.size}",
                        color = FplAccent
                    )
                    InsightItem(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Star,
                        label = "Differentials",
                        value = "${playerAnalytics.differentials.size}",
                        color = FplYellow
                    )
                    InsightItem(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.EmojiEvents,
                        label = "Top Captain",
                        value = playerAnalytics.captaincy.firstOrNull()?.playerName ?: "-",
                        color = FplOrange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Most owned player
                playerAnalytics.playerOwnership.firstOrNull()?.let { mostOwned ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(FplGlass.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Most Owned",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = FplAccentLight
                                )
                                Text(
                                    text = mostOwned.playerName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${mostOwned.ownershipPercentage.toInt()}%",
                                style = MaterialTheme.typography.headlineSmall,
                                color = FplAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Loading or empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Enter a league ID to see player analytics",
                        style = MaterialTheme.typography.bodyLarge,
                        color = FplAccentLight
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightItem(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = FplAccentLight
        )
    }
}

@Composable
fun QuickPlayerStatsRow(
    playerAnalytics: LeaguePlayerAnalytics
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Most transferred in
        QuickStatCard(
            modifier = Modifier.weight(1f),
            title = "Hot Pick",
            value = playerAnalytics.transferTrends.mostTransferredIn.firstOrNull()?.playerName ?: "-",
            subtitle = "+${playerAnalytics.transferTrends.mostTransferredIn.firstOrNull()?.transferCount ?: 0}",
            icon = Icons.Default.TrendingUp,
            color = FplGreen
        )

        // Best differential
        QuickStatCard(
            modifier = Modifier.weight(1f),
            title = "Top Diff",
            value = playerAnalytics.differentials.firstOrNull()?.playerName ?: "-",
            subtitle = "${playerAnalytics.differentials.firstOrNull()?.points ?: 0} pts",
            icon = Icons.Default.Rocket,
            color = FplSecondary
        )
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = FplTextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
        }
    }
}