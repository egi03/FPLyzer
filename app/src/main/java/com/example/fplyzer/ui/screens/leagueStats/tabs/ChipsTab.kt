package com.example.fplyzer.ui.screens.leagueStats.tabs

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fplyzer.data.models.statistics.LeagueStatistics
import com.example.fplyzer.data.models.statistics.ManagerStatistics
import com.example.fplyzer.ui.components.GradientCard
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.theme.FplBlue
import com.example.fplyzer.ui.theme.FplGreen
import com.example.fplyzer.ui.theme.FplOrange
import com.example.fplyzer.ui.theme.FplPink
import com.example.fplyzer.ui.theme.FplRed
import com.example.fplyzer.ui.theme.FplSurface
import com.example.fplyzer.ui.theme.FplTextSecondary
import com.example.fplyzer.ui.theme.FplYellow

@Composable
fun ChipsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    val stats = uiState.leagueStatistics ?: return
    var selectedChip by remember { mutableStateOf(ChipType.BENCH_BOOST) }
    var showingDetail by remember { mutableStateOf(false) }
    var selectedUsage by remember { mutableStateOf<ChipUsage?>(null) }
    var showingInfoSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Theme-aware
                .shadow(
                    elevation = 5.dp,
                    spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) // Theme-aware
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Chip Analysis",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface // Theme-aware
                    )
                    Text(
                        text = "Special power usage and effectiveness",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                    )
                }

                IconButton(onClick = { showingInfoSheet = true }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        tint = FplBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ChipSelector(
                selectedChip = selectedChip,
                chipUsage = getChipUsageByType(stats),
                totalMembers = stats.managerStats.size,
                onChipSelected = { selectedChip = it }
            )

            // League Overview
            ChipLeagueOverview(
                totalMembers = stats.managerStats.size,
                chipUsage = getChipUsageByType(stats),
                unusedChips = calculateUnusedChips(stats)
            )
        }

        // Main Content
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val chipUsages = getChipUsageByType(stats)[selectedChip] ?: emptyList()
                val chipStats = calculateChipStats(chipUsages)

                ChipOverviewCard(
                    chipType = selectedChip,
                    stats = chipStats,
                    totalMembers = stats.managerStats.size
                )
            }

            // Usage Timeline
            item {
                val chipUsages = getChipUsageByType(stats)[selectedChip] ?: emptyList()
                if (chipUsages.isNotEmpty()) {
                    ChipTimelineView(
                        chipUsage = chipUsages,
                        chipType = selectedChip
                    )
                }
            }

            // Quality Distribution
            item {
                val chipUsages = getChipUsageByType(stats)[selectedChip] ?: emptyList()
                if (chipUsages.isNotEmpty()) {
                    ChipQualityDistribution(
                        chipUsage = chipUsages,
                        chipType = selectedChip
                    )
                }
            }

            // Individual Usage List
            item {
                Text(
                    text = "Individual Usage",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface, // Theme-aware
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            val chipUsages = getChipUsageByType(stats)[selectedChip] ?: emptyList()
            if (chipUsages.isEmpty()) {
                item {
                    EmptyChipView(chipType = selectedChip)
                }
            } else {
                itemsIndexed(chipUsages.sortedByDescending { it.second.points }) { index, (managerId, usage) ->
                    val manager = stats.managerStats[managerId]
                    if (manager != null) {
                        ChipUsageCard(
                            manager = manager,
                            usage = usage,
                            chipType = selectedChip,
                            rank = index + 1,
                            averageScore = chipUsages.map { it.second.points }.average(),
                            onTap = {
                                selectedUsage = usage
                                showingDetail = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showingInfoSheet) {
        ChipsInfoSheet(onDismiss = { showingInfoSheet = false })
    }
}

@Composable
private fun ChipSelector(
    selectedChip: ChipType,
    chipUsage: Map<ChipType, List<Pair<Int, ChipUsage>>>,
    totalMembers: Int,
    onChipSelected: (ChipType) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ChipType.entries) { chip ->
            ChipButton(
                chip = chip,
                isSelected = selectedChip == chip,
                count = chipUsage[chip]?.size ?: 0,
                totalPossible = totalMembers,
                action = { onChipSelected(chip) }
            )
        }
    }
}

@Composable
private fun ChipButton(
    chip: ChipType,
    isSelected: Boolean,
    count: Int,
    totalPossible: Int,
    action: () -> Unit
) {
    val usagePercentage = if (totalPossible > 0) count.toDouble() / totalPossible * 100 else 0.0

    Card(
        onClick = action,
        modifier = Modifier
            .width(110.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) chip.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant // Theme-aware
        ),
        border = if (isSelected) BorderStroke(2.dp, chip.color) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            Brush.radialGradient(
                                colors = listOf(chip.color, chip.color.copy(alpha = 0.7f))
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(chip.color.copy(alpha = 0.2f), chip.color.copy(alpha = 0.1f))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = chip.icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else chip.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chip.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface, // Theme-aware
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (count > 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$count/$totalPossible",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${usagePercentage.toInt()}% used",
                        style = MaterialTheme.typography.labelSmall,
                        color = chip.color,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = "Not used",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ChipLeagueOverview(
    totalMembers: Int,
    chipUsage: Map<ChipType, List<Pair<Int, ChipUsage>>>,
    unusedChips: Int
) {
    val totalChipsUsed = chipUsage.values.sumOf { it.size }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OverviewStat(
            modifier = Modifier.weight(1f),
            title = "Total Used",
            value = totalChipsUsed.toString(),
            subtitle = "across all types",
            icon = Icons.Default.Star,
            color = FplBlue
        )

        OverviewStat(
            modifier = Modifier.weight(1f),
            title = "Unused",
            value = unusedChips.toString(),
            subtitle = "chips remaining",
            icon = Icons.Default.Timer,
            color = FplOrange
        )

        OverviewStat(
            modifier = Modifier.weight(1f),
            title = "Managers",
            value = totalMembers.toString(),
            subtitle = "in league",
            icon = Icons.Default.Group,
            color = FplGreen
        )
    }
}

@Composable
private fun OverviewStat(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), // Theme-aware
        elevation = CardDefaults.cardElevation(2.dp) // Added slight elevation for better visibility
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun ChipOverviewCard(
    chipType: ChipType,
    stats: ChipStatistics,
    totalMembers: Int
) {
    val chipStrategy = when (chipType) {
        ChipType.BENCH_BOOST -> "Play when your bench has high-scoring players"
        ChipType.TRIPLE_CAPTAIN -> "Use on premium players with favorable fixtures"
        ChipType.FREE_HIT -> "Perfect for blank or double gameweeks"
        ChipType.WILDCARD -> "Rebuild your team when needed"
    }

    val effectivenessRating = when {
        stats.totalUses == 0 -> "No data"
        chipType == ChipType.BENCH_BOOST -> when {
            stats.averagePoints > 20 -> "🔥 Excellent timing"
            stats.averagePoints > 15 -> "✅ Good usage"
            else -> "📊 Average results"
        }
        chipType == ChipType.TRIPLE_CAPTAIN -> when {
            stats.averagePoints > 80 -> "🎯 Perfect picks"
            stats.averagePoints > 60 -> "👍 Solid choices"
            else -> "🤔 Mixed results"
        }
        else -> when {
            stats.averagePoints > 70 -> "⭐ Great performance"
            stats.averagePoints > 50 -> "✓ Decent returns"
            else -> "📈 Room to improve"
        }
    }

    GradientCard(
        modifier = Modifier.fillMaxWidth(),
        gradientColors = listOf(chipType.color.copy(alpha = 0.15f), MaterialTheme.colorScheme.surface), // Theme-aware
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = chipType.icon,
                    contentDescription = null,
                    tint = chipType.color,
                    modifier = Modifier.size(40.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chipType.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface // Theme-aware
                    )
                    Text(
                        text = chipStrategy,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    modifier = Modifier.weight(1f),
                    title = "Usage Rate",
                    value = "${(stats.totalUses.toDouble() / totalMembers * 100).toInt()}%",
                    subtitle = "${stats.totalUses}/$totalMembers managers",
                    color = chipType.color
                )

                StatBox(
                    modifier = Modifier.weight(1f),
                    title = "Average Return",
                    value = String.format("%.1f pts", stats.averagePoints),
                    subtitle = effectivenessRating,
                    color = chipType.color
                )

                StatBox(
                    modifier = Modifier.weight(1f),
                    title = "Best Usage",
                    value = "${stats.bestScore} pts",
                    subtitle = if (stats.bestScore > 100) "🏆 League best!" else "Peak score",
                    color = chipType.color
                )
            }
        }
    }
}

@Composable
private fun StatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface) // Theme-aware
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ChipTimelineView(
    chipUsage: List<Pair<Int, ChipUsage>>,
    chipType: ChipType
) {
    val usageByGameweek = chipUsage.groupBy { it.second.event }
        .mapValues { it.value.size }

    val popularGameweeks = usageByGameweek
        .entries
        .sortedByDescending { it.value }
        .take(3)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme-aware
        elevation = CardDefaults.cardElevation(2.dp) // Added slight elevation
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Usage Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )

                if (popularGameweeks.isNotEmpty()) {
                    Text(
                        text = "Popular: GW${popularGameweeks.map { it.key }.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Theme-aware
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    for (gw in 1..38) {
                        val count = usageByGameweek[gw] ?: 0
                        if (count > 0) {
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height((count * 15).dp.coerceAtMost(90.dp))
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(chipType.color)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("GW1", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("GW19", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("GW38", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ChipQualityDistribution(
    chipUsage: List<Pair<Int, ChipUsage>>,
    chipType: ChipType
) {
    val distribution = calculateQualityDistribution(chipUsage, chipType)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme-aware
        elevation = CardDefaults.cardElevation(2.dp) // Added slight elevation
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Usage Quality Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )

            Spacer(modifier = Modifier.height(16.dp))

            distribution.forEach { (quality, count) ->
                QualityBar(
                    label = quality.emoji + " " + quality.label,
                    count = count,
                    total = chipUsage.size,
                    color = quality.color
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun QualityBar(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val percentage = if (total > 0) count.toDouble() / total else 0.0

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface, // Theme-aware
            modifier = Modifier.width(100.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Theme-aware
            )

            val animatedPercentage by animateFloatAsState(
                targetValue = percentage.toFloat(),
                animationSpec = spring()
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedPercentage)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color),
                contentAlignment = Alignment.CenterStart
            ) {
                if (count > 0) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipUsageCard(
    manager: ManagerStatistics,
    usage: ChipUsage,
    chipType: ChipType,
    rank: Int,
    averageScore: Double,
    onTap: () -> Unit
) {
    val effectiveness = calculateEffectiveness(usage, chipType, averageScore)
    val performanceMessage = getPerformanceMessage(usage, chipType, effectiveness)
    val timingQuality = getTimingQuality(usage, chipType)

    Card(
        onClick = onTap,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme-aware
        elevation = CardDefaults.cardElevation(2.dp), // Added slight elevation
        border = BorderStroke(
            width = 1.dp,
            color = effectiveness.color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rank indicator
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(effectiveness.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = effectiveness.color
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = manager.teamName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface // Theme-aware
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                            )
                            Text(
                                text = manager.managerName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                            )
                            Text(
                                text = "GW ${usage.event}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                            )
                        }
                    }

                    Text(
                        text = timingQuality,
                        style = MaterialTheme.typography.labelSmall,
                        color = chipType.color
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = getDisplayValue(usage, chipType).first,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = effectiveness.color
                        )
                        Text(
                            text = getDisplayValue(usage, chipType).second,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Text(
                        text = performanceMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = effectiveness.color,
                        textAlign = TextAlign.End
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(effectiveness.color.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = effectiveness.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = effectiveness.color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            when (chipType) {
                ChipType.TRIPLE_CAPTAIN -> {
                    usage.captainName?.let { captainName ->
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant // Theme-aware
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = FplOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Captain: $captainName",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                                )
                            }

                            usage.captainActualPoints?.let { captainPoints ->
                                Text(
                                    text = "$captainPoints → ${captainPoints * 3} pts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FplOrange
                                )
                            }
                        }
                    }
                }
                ChipType.BENCH_BOOST -> {
                    usage.benchBoost?.let { benchPoints ->
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant // Theme-aware
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.EventSeat,
                                    contentDescription = null,
                                    tint = FplBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Bench Contribution",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                                )
                            }

                            Text(
                                text = "+$benchPoints pts",
                                style = MaterialTheme.typography.bodySmall,
                                color = FplBlue
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun EmptyChipView(chipType: ChipType) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                imageVector = chipType.icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = chipType.color.copy(alpha = 0.5f)
            )

            Text(
                text = "No ${chipType.displayName} used yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
            )

            Text(
                text = "This chip hasn't been played by any manager in the league",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChipsInfoSheet(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Understanding Chips",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChipType.entries.forEach { chip ->
                    ChipExplanation(chipType = chip)
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = FplBlue.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Timing Tips",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface // Theme-aware
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = """
                                • Bench Boost: Use during Double Gameweeks when all players play twice
                                • Triple Captain: Save for premium players with great fixtures
                                • Free Hit: Perfect for Blank Gameweeks or when many players are unavailable
                                • Wildcard: Use when your team needs major surgery
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

@Composable
private fun ChipExplanation(chipType: ChipType) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = chipType.icon,
            contentDescription = null,
            tint = chipType.color,
            modifier = Modifier.size(24.dp)
        )

        Column {
            Text(
                text = chipType.displayName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )
            Text(
                text = chipType.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
            )
            Text(
                text = chipType.scoreRanges,
                style = MaterialTheme.typography.labelSmall,
                color = chipType.color
            )
        }
    }
}

// Data classes and helper functions remain the same...
// (ChipUsage, ChipStatistics, ChipType, ChipEffectiveness, and helper functions)

// [Rest of the existing data classes and helper functions would remain unchanged]
data class ChipUsage(
    val id: String = "",
    val event: Int,
    val points: Int,
    val captainName: String? = null,
    val captainActualPoints: Int? = null,
    val benchBoost: Int? = null,
    val rank: Int = 0
)

data class ChipStatistics(
    val totalUses: Int,
    val averagePoints: Double,
    val bestScore: Int
)

enum class ChipType(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val description: String,
    val scoreRanges: String
) {
    BENCH_BOOST(
        "Bench Boost",
        Icons.Default.EventSeat,
        FplBlue,
        "All 15 players' points count. Best used when your bench has favorable fixtures.",
        "25+ excellent, 15-25 good, <15 average"
    ),
    TRIPLE_CAPTAIN(
        "Triple Captain",
        Icons.Default.StarBorder,
        FplYellow,
        "Captain scores triple points instead of double. Save for premium players in great form.",
        "90+ excellent, 60-90 good, <60 average"
    ),
    FREE_HIT(
        "Free Hit",
        Icons.Default.Refresh,
        FplPink,
        "Make unlimited transfers for one week only. Team reverts next gameweek.",
        "80+ excellent, 60-80 good, <60 average"
    ),
    WILDCARD(
        "Wildcard",
        Icons.Default.SwapHoriz,
        FplGreen,
        "Make unlimited transfers without point hits. Changes are permanent.",
        "70+ excellent, 50-70 good, <50 average"
    )
}

data class ChipEffectiveness(
    val label: String,
    val color: Color,
    val emoji: String
) {
    companion object {
        val EXCELLENT = ChipEffectiveness("Excellent", FplGreen, "🔥")
        val GOOD = ChipEffectiveness("Good", FplBlue, "✅")
        val AVERAGE = ChipEffectiveness("Average", FplOrange, "📊")
        val POOR = ChipEffectiveness("Poor", FplRed, "😔")
    }
}

// Helper functions remain the same...
private fun getChipUsageByType(stats: LeagueStatistics): Map<ChipType, List<Pair<Int, ChipUsage>>> {
    val usageMap = mutableMapOf<ChipType, MutableList<Pair<Int, ChipUsage>>>()

    stats.managerStats.forEach { (managerId, manager) ->
        manager.chipsUsed.forEach { chip ->
            val chipType = when (chip.name.lowercase()) {
                "wildcard" -> ChipType.WILDCARD
                "bboost" -> ChipType.BENCH_BOOST
                "3xc" -> ChipType.TRIPLE_CAPTAIN
                "freehit" -> ChipType.FREE_HIT
                else -> null
            }

            chipType?.let {
                val usage = ChipUsage(
                    event = chip.event,
                    points = manager.pointsHistory.getOrNull(chip.event - 1) ?: 0,
                    // Additional data would be populated from actual API
                )
                usageMap.getOrPut(it) { mutableListOf() }.add(managerId to usage)
            }
        }
    }

    return usageMap
}

private fun calculateUnusedChips(stats: LeagueStatistics): Int {
    val totalPossibleChips = stats.managerStats.size * 4 // 4 chips per manager
    val usedChips = stats.managerStats.values.sumOf { it.chipsUsed.size }
    return totalPossibleChips - usedChips
}

private fun calculateChipStats(usages: List<Pair<Int, ChipUsage>>): ChipStatistics {
    val points = usages.map { it.second.points }
    return ChipStatistics(
        totalUses = usages.size,
        averagePoints = if (points.isNotEmpty()) points.average() else 0.0,
        bestScore = points.maxOrNull() ?: 0
    )
}

private fun calculateEffectiveness(
    usage: ChipUsage,
    chipType: ChipType,
    averageScore: Double
): ChipEffectiveness {
    return when (chipType) {
        ChipType.TRIPLE_CAPTAIN -> {
            // For triple captain, we estimate captain points from total points
            // Assuming captain got roughly 1/3 of total points (since they're tripled)
            val estimatedCaptainPoints = usage.captainActualPoints ?: (usage.points / 3)
            when {
                estimatedCaptainPoints >= 15 -> ChipEffectiveness.EXCELLENT
                estimatedCaptainPoints >= 10 -> ChipEffectiveness.GOOD
                estimatedCaptainPoints >= 6 -> ChipEffectiveness.AVERAGE
                else -> ChipEffectiveness.POOR
            }
        }
        ChipType.BENCH_BOOST -> {
            // For bench boost, estimate bench contribution
            // If benchBoost data isn't available, estimate from total points vs average
            val benchContribution = usage.benchBoost ?: run {
                // Estimate bench contribution as points above average
                val pointsAboveAverage = usage.points - averageScore
                maxOf(0, pointsAboveAverage.toInt())
            }
            when {
                benchContribution >= 20 -> ChipEffectiveness.EXCELLENT
                benchContribution >= 12 -> ChipEffectiveness.GOOD
                benchContribution >= 6 -> ChipEffectiveness.AVERAGE
                else -> ChipEffectiveness.POOR
            }
        }
        ChipType.FREE_HIT -> {
            // For free hit, compare against average and adjust thresholds
            when {
                usage.points >= averageScore * 1.4 -> ChipEffectiveness.EXCELLENT
                usage.points >= averageScore * 1.15 -> ChipEffectiveness.GOOD
                usage.points >= averageScore * 0.9 -> ChipEffectiveness.AVERAGE
                else -> ChipEffectiveness.POOR
            }
        }
        ChipType.WILDCARD -> {
            // For wildcard, the effect might be spread over multiple weeks
            // For now, just compare against average with adjusted thresholds
            when {
                usage.points >= averageScore * 1.3 -> ChipEffectiveness.EXCELLENT
                usage.points >= averageScore * 1.1 -> ChipEffectiveness.GOOD
                usage.points >= averageScore * 0.85 -> ChipEffectiveness.AVERAGE
                else -> ChipEffectiveness.POOR
            }
        }
    }
}

private fun getPerformanceMessage(
    usage: ChipUsage,
    chipType: ChipType,
    effectiveness: ChipEffectiveness
): String {
    return when (chipType) {
        ChipType.TRIPLE_CAPTAIN -> {
            usage.captainName?.let { "$it: ${usage.captainActualPoints ?: 0} pts × 3" }
                ?: "Triple captain activated"
        }
        ChipType.BENCH_BOOST -> {
            usage.benchBoost?.let { "Bench: +$it pts" }
                ?: "Bench boost activated"
        }
        else -> when (effectiveness) {
            ChipEffectiveness.EXCELLENT -> "🔥 Excellent timing!"
            ChipEffectiveness.GOOD -> "✅ Good usage"
            ChipEffectiveness.AVERAGE -> "📊 Average result"
            ChipEffectiveness.POOR -> "😔 Poor timing"
            else -> ""
        }
    }
}

private fun getTimingQuality(usage: ChipUsage, chipType: ChipType): String {
    return when (chipType) {
        ChipType.TRIPLE_CAPTAIN -> {
            val captainPoints = usage.captainActualPoints ?: 0
            when {
                captainPoints >= 15 -> "Perfect captain choice!"
                captainPoints >= 10 -> "Good captain pick"
                captainPoints >= 6 -> "Decent captain"
                else -> "Captain let you down"
            }
        }
        ChipType.BENCH_BOOST -> {
            val benchPoints = usage.benchBoost ?: 0
            when {
                benchPoints >= 25 -> "Amazing bench!"
                benchPoints >= 18 -> "Strong bench"
                benchPoints >= 12 -> "Decent bench"
                else -> "Weak bench"
            }
        }
        else -> when {
            usage.points > 80 -> "Excellent week!"
            usage.points > 60 -> "Good choice"
            else -> "Tough week"
        }
    }
}

private fun getDisplayValue(usage: ChipUsage, chipType: ChipType): Pair<String, String> {
    return when (chipType) {
        ChipType.TRIPLE_CAPTAIN -> {
            usage.captainActualPoints?.let { "${it}" to "captain pts" }
                ?: ("${usage.points}" to "total pts")
        }
        ChipType.BENCH_BOOST -> {
            usage.benchBoost?.let { "$it" to "bench pts" }
                ?: ("${usage.points}" to "total pts")
        }
        else -> "${usage.points}" to "total pts"
    }
}

private fun calculateQualityDistribution(
    chipUsage: List<Pair<Int, ChipUsage>>,
    chipType: ChipType
): Map<ChipEffectiveness, Int> {
    val distribution = mutableMapOf(
        ChipEffectiveness.EXCELLENT to 0,
        ChipEffectiveness.GOOD to 0,
        ChipEffectiveness.AVERAGE to 0,
        ChipEffectiveness.POOR to 0
    )

    if (chipUsage.isEmpty()) return distribution

    val averageScore = chipUsage.map { it.second.points }.average()

    chipUsage.forEach { (_, usage) ->
        val effectiveness = calculateEffectiveness(usage, chipType, averageScore)
        distribution[effectiveness] = distribution[effectiveness]!! + 1
    }

    return distribution
}