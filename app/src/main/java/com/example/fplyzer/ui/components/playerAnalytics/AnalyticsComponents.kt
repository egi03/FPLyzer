package com.example.fplyzer.ui.components.playerAnalytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.CaptaincyData
import com.example.fplyzer.data.models.statistics.DifferentialPick
import com.example.fplyzer.data.models.statistics.PlayerOwnership
import com.example.fplyzer.ui.components.GlassmorphicCard
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplOrange
import com.example.fplyzer.ui.theme.FplRed
import com.example.fplyzer.ui.theme.FplYellow

@Composable
fun PlayerOwnershipCard(
    player: PlayerOwnership,
    onClick: () -> Unit = {}
) {
    val ownershipColor = when {
        player.ownershipPercentage > 75 -> FplRed
        player.ownershipPercentage > 50 -> FplOrange
        player.ownershipPercentage > 25 -> FplYellow
        player.ownershipPercentage > 10 -> FplBlue
        else -> FplGreen
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Position indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (player.position) {
                            "GKP" -> Color(0xFFFFD700)
                            "DEF" -> Color(0xFF00D685)
                            "MID" -> Color(0xFF05F1FF)
                            "FWD" -> Color(0xFFE90052)
                            else -> MaterialTheme.colorScheme.primary
                        }.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.position,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (player.position) {
                        "GKP" -> Color(0xFFFFD700)
                        "DEF" -> Color(0xFF00D685)
                        "MID" -> Color(0xFF05F1FF)
                        "FWD" -> Color(0xFFE90052)
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            }

            // Player info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = player.playerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (player.isTemplate) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Template",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (player.isDifferential) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Differential",
                            modifier = Modifier.size(16.dp),
                            tint = FplYellow
                        )
                    }
                }
                Text(
                    text = "${player.teamName} • £${player.price}m • ${player.points} pts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Ownership visualization
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(ownershipColor.copy(alpha = 0.1f))
                        .border(
                            width = 2.dp,
                            color = ownershipColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${player.ownershipPercentage.toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ownershipColor
                        )
                        Text(
                            text = "${player.ownershipCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CaptaincyCard(
    captaincy: CaptaincyData
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(FplYellow, FplOrange)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.surface
                    )
                }

                Column {
                    Text(
                        text = captaincy.playerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${captaincy.captainCount} captains",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (captaincy.viceCaptainCount > 0) {
                            Text(
                                text = "• ${captaincy.viceCaptainCount} VC",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${captaincy.totalPointsEarned} pts",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FplGreen
                )
                Text(
                    text = "${String.format("%.1f", captaincy.captainPercentage)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DifferentialCard(
    differential: DifferentialPick,
    onShowManagers: () -> Unit = {}
) {
    Card(
        onClick = onShowManagers,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            FplGreen.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = "Differential",
                            tint = FplGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = differential.playerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${differential.teamName} • ${differential.ownershipPercentage.toInt()}% owned",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Owned by: ${differential.managers.take(3).joinToString(", ")}${if (differential.managers.size > 3) " +${differential.managers.size - 3}" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${differential.points} pts",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = FplGreen
                    )
                    Text(
                        text = "${String.format("%.1f", differential.pointsPerMillion)} pts/£m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FplGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Score: ${differential.differentialScore.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = FplGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateTeamCard(
    templatePlayers: List<PlayerOwnership>,
    templateOwnership: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = "Template Team",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Template Team",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = "${(templateOwnership * 100).toInt()}% have full template",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group by position
            val grouped = templatePlayers.groupBy { it.position }
            listOf("GKP", "DEF", "MID", "FWD").forEach { position ->
                grouped[position]?.let { players ->
                    PositionRow(position, players)
                    if (position != "FWD") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PositionRow(
    position: String,
    players: List<PlayerOwnership>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = position,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
            modifier = Modifier.width(40.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            players.forEach { player ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = player.playerName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun OwnershipDistributionChart(
    playerOwnership: List<PlayerOwnership>,
    position: String? = null
) {
    val filtered = if (position != null) {
        playerOwnership.filter { it.position == position }
    } else {
        playerOwnership
    }

    val distribution = listOf(
        "0-10%" to filtered.count { it.ownershipPercentage < 10 },
        "10-25%" to filtered.count { it.ownershipPercentage in 10.0..25.0 },
        "25-50%" to filtered.count { it.ownershipPercentage in 25.0..50.0 },
        "50-75%" to filtered.count { it.ownershipPercentage in 50.0..75.0 },
        "75%+" to filtered.count { it.ownershipPercentage > 75 }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Ownership Distribution${position?.let { " - $it" } ?: ""}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            distribution.forEach { (range, count) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = range,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(80.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )

                        val maxCount = distribution.maxOf { it.second }.toFloat()
                        val animatedWidth by animateFloatAsState(
                            targetValue = if (maxCount > 0) count / maxCount else 0f,
                            animationSpec = tween(800)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedWidth)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = when (range) {
                                            "0-10%" -> listOf(FplGreen, FplGreen.copy(alpha = 0.8f))
                                            "10-25%" -> listOf(FplBlue, FplBlue.copy(alpha = 0.8f))
                                            "25-50%" -> listOf(FplYellow, FplYellow.copy(alpha = 0.8f))
                                            "50-75%" -> listOf(FplOrange, FplOrange.copy(alpha = 0.8f))
                                            else -> listOf(FplRed, FplRed.copy(alpha = 0.8f))
                                        }
                                    )
                                )
                        )
                    }

                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(
    metric: String,
    value1: String,
    value2: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value1,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = metric,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = value2,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}