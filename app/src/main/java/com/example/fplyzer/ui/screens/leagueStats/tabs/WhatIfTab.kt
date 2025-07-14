package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fplyzer.data.models.whatif.*
import com.example.fplyzer.ui.components.GlassmorphicCard
import com.example.fplyzer.ui.components.ModernChip
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.theme.*

@Composable
fun WhatIfTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    var selectedScenario by remember { mutableStateOf<String?>(null) }
    var filterType by remember { mutableStateOf(ScenarioFilterType.ALL) }
    var showingInfoSheet by remember { mutableStateOf(false) }

    val filteredScenarios = remember(filterType, uiState.whatIfScenarios) {
        if (filterType.scenarioType != null) {
            uiState.whatIfScenarios.filter { it.type == filterType.scenarioType }
        } else {
            uiState.whatIfScenarios
        }
    }

    val scenarioSummary = remember(uiState.whatIfScenarios) {
        if (uiState.whatIfScenarios.isNotEmpty()) {
            calculateWhatIfSummary(uiState.whatIfScenarios)
        } else {
            null
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoBanner()
        }
        // Header Section
        item {
            if (scenarioSummary != null) {
                WhatIfHeaderCard(
                    summary = scenarioSummary,
                    onInfoClick = { showingInfoSheet = true }
                )
            }
        }

        // Loading State
        if (uiState.isLoadingWhatIf) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = FplSecondary)
                        Text(
                            text = "Analyzing what-if scenarios...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                        )
                    }
                }
            }
        } else {
            // Filter Options
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ScenarioFilterType.values()) { type ->
                        WhatIfFilterChip(
                            type = type,
                            isSelected = filterType == type,
                            count = getScenarioCount(uiState.whatIfScenarios, type),
                            onClick = { filterType = type }
                        )
                    }
                }
            }

            // Scenario Cards
            if (filteredScenarios.isEmpty()) {
                item {
                    EmptyWhatIfView()
                }
            } else {
                items(filteredScenarios) { scenario ->
                    WhatIfScenarioCard(
                        scenario = scenario,
                        isExpanded = selectedScenario == scenario.id,
                        onTap = {
                            selectedScenario = if (selectedScenario == scenario.id) null else scenario.id
                        }
                    )
                }
            }
        }
    }

    if (showingInfoSheet) {
        WhatIfInfoDialog(
            onDismiss = { showingInfoSheet = false }
        )
    }
}

enum class ScenarioFilterType(val label: String, val icon: String) {
    ALL("All Scenarios", "list.bullet"),
    CAPTAIN("Captain Choices", "star.circle"),
    CHIPS("Chip Timing", "wand.and.stars"),
    TRANSFERS("Transfers", "arrow.left.arrow.right");

    val scenarioType: ScenarioType?
        get() = when (this) {
            ALL -> null
            CAPTAIN -> ScenarioType.CAPTAIN_CHANGE
            CHIPS -> ScenarioType.CHIP_TIMING
            TRANSFERS -> ScenarioType.TRANSFER_CHANGE
        }
}

@Composable
private fun WhatIfHeaderCard(
    summary: WhatIfSummary?,
    onInfoClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "WHAT-IF SCENARIOS",
                        style = MaterialTheme.typography.labelLarge,
                        color = FplAccentLight,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Explore alternative timelines",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface, // Theme-aware
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) // Theme-aware
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        tint = FplAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Stats
            if (summary != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WhatIfStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Scenarios",
                        value = "${summary.totalScenariosAnalyzed}",
                        subtitle = "analyzed",
                        icon = Icons.Default.List,
                        color = FplBlue
                    )
                    WhatIfStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Biggest Gain",
                        value = "${summary.biggestPotentialGain}",
                        subtitle = "points",
                        icon = Icons.Default.KeyboardArrowUp,
                        color = FplGreen
                    )
                    WhatIfStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Biggest Miss",
                        value = "${summary.biggestMissedOpportunity}",
                        subtitle = "points",
                        icon = Icons.Default.KeyboardArrowDown,
                        color = FplRed
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stability Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "League Stability",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface // Theme-aware
                        )
                        Text(
                            text = summary.stabilityDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = getStabilityColor(summary.stabilityRating)
                        )
                    }

                    summary.mostVolatileGameweek?.let { gw ->
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "GW$gw",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FplOrange
                            )
                            Text(
                                text = "Most volatile",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                            )
                        }
                    }
                }
            } else {
                // Placeholder when no data
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WhatIfStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Scenarios",
                        value = "-",
                        subtitle = "analyzing",
                        icon = Icons.Default.List,
                        color = FplBlue
                    )
                    WhatIfStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Biggest Gain",
                        value = "-",
                        subtitle = "calculating",
                        icon = Icons.Default.KeyboardArrowUp,
                        color = FplGreen
                    )
                    WhatIfStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Biggest Miss",
                        value = "-",
                        subtitle = "calculating",
                        icon = Icons.Default.KeyboardArrowDown,
                        color = FplRed
                    )
                }
            }
        }
    }
}

@Composable
private fun WhatIfStatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(color.copy(alpha = 0.1f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface // Theme-aware
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WhatIfFilterChip(
    type: ScenarioFilterType,
    isSelected: Boolean,
    count: Int,
    onClick: () -> Unit
) {
    ModernChip(
        text = if (count > 0) "${type.label} ($count)" else type.label,
        selected = isSelected,
        onClick = onClick
    )
}

@Composable
private fun WhatIfScenarioCard(
    scenario: WhatIfScenario,
    isExpanded: Boolean,
    onTap: () -> Unit
) {
    val typeColor = getScenarioTypeColor(scenario.type)
    val impactSummary = getImpactSummary(scenario)
    val biggestWinner = scenario.results.maxByOrNull { -it.rankChange } // Lower rank is better
    val biggestLoser = scenario.results.maxByOrNull { it.rankChange }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme-aware
        elevation = CardDefaults.cardElevation(2.dp) // Added elevation
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = getScenarioTypeIcon(scenario.type),
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = scenario.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface // Theme-aware
                        )
                    }

                    Text(
                        text = scenario.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        scenario.gameweek?.let { gw ->
                            Text(
                                text = "GW$gw",
                                style = MaterialTheme.typography.labelMedium,
                                color = FplBlue,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(FplBlue.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = impactSummary,
                            style = MaterialTheme.typography.labelMedium,
                            color = typeColor,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(typeColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${scenario.results.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                    Text(
                        text = "managers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                    )
                    Text(
                        text = scenario.impact.impactLevel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
                        textAlign = TextAlign.End
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                )
            }

            // Expanded Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant) // Theme-aware

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Impact Overview
                        ScenarioImpactOverview(impact = scenario.impact)

                        // Top Changes
                        if (biggestWinner != null || biggestLoser != null) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Biggest Changes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                                )

                                biggestWinner?.let { winner ->
                                    if (winner.rankChange != 0) {
                                        WhatIfResultRow(result = winner, isPositive = winner.improvement)
                                    }
                                }

                                biggestLoser?.let { loser ->
                                    if (loser.rankChange != 0 && loser.managerId != biggestWinner?.managerId) {
                                        WhatIfResultRow(result = loser, isPositive = loser.improvement)
                                    }
                                }
                            }
                        }

                        // All Results (top 10)
                        if (scenario.results.size > 2) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "All Changes",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface // Theme-aware
                                    )
                                    Text(
                                        text = "Showing top 10",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                                    )
                                }

                                scenario.results
                                    .sortedByDescending { kotlin.math.abs(it.rankChange) }
                                    .take(10)
                                    .forEach { result ->
                                        CompactResultRow(result = result)
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScenarioImpactOverview(impact: ScenarioImpact) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Impact Analysis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface // Theme-aware
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImpactStatBox(
                modifier = Modifier.weight(1f),
                title = "Avg Rank Change",
                value = String.format("%.1f", kotlin.math.abs(impact.averageRankChange)),
                isPositive = impact.averageRankChange < 0,
                icon = if (impact.averageRankChange < 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
            )

            ImpactStatBox(
                modifier = Modifier.weight(1f),
                title = "Avg Points Change",
                value = String.format("%.0f", kotlin.math.abs(impact.averagePointsChange)),
                isPositive = impact.averagePointsChange > 0,
                icon = if (impact.averagePointsChange > 0) Icons.Default.Add else Icons.Default.Remove
            )

            ImpactStatBox(
                modifier = Modifier.weight(1f),
                title = "Managers Affected",
                value = "${impact.managersAffected}",
                isPositive = true,
                icon = Icons.Default.Group
            )
        }
    }
}

@Composable
private fun ImpactStatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    isPositive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val color = if (isPositive) FplGreen else FplRed

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WhatIfResultRow(result: WhatIfResult, isPositive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(if (isPositive) FplGreen.copy(alpha = 0.1f) else FplRed.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.managerName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )
            Text(
                text = "Rank: ${result.originalRank} → ${result.newRank}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isPositive) FplGreen else FplRed,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${kotlin.math.abs(result.rankChange)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) FplGreen else FplRed
                )
            }

            if (result.pointsChange != 0) {
                Text(
                    text = "${if (result.pointsChange > 0) "+" else ""}${result.pointsChange} pts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                )
            }
        }
    }
}

@Composable
private fun CompactResultRow(result: WhatIfResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant) // Theme-aware
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = result.managerName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface, // Theme-aware
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${result.originalRank} → ${result.newRank}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (result.rankChange != 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = if (result.improvement) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (result.improvement) FplGreen else FplRed,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${kotlin.math.abs(result.rankChange)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (result.improvement) FplGreen else FplRed
                )
            }
        } else {
            Text(
                text = "=",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
            )
        }
    }
}

@Composable
private fun EmptyWhatIfView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.QuestionMark,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = FplGray.copy(alpha = 0.5f)
        )

        Text(
            text = "No What-If Scenarios",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
        )

        Text(
            text = "Alternative timeline analysis will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WhatIfInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Understanding What-If Scenarios",
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "What are What-If Scenarios?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )
                Text(
                    text = "What-if scenarios show how league standings would change if different decisions were made. They help you understand the impact of key choices and identify missed opportunities.",
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )

                Text(
                    text = "Captain Scenarios:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )
                Text(
                    text = "Shows what would happen if managers made different captain choices. Often reveals how much captain selection affects final rankings.",
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )

                Text(
                    text = "Impact Levels:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )
                Text(
                    text = "• 💤 Minimal: Few managers affected\n" +
                            "• 📊 Minor: Small changes to standings\n" +
                            "• 📈 Moderate: Noticeable rank shifts\n" +
                            "• 🌪️ Major: Significant league shake-up",
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

// Helper functions
private fun getScenarioCount(scenarios: List<WhatIfScenario>, filterType: ScenarioFilterType): Int {
    return if (filterType.scenarioType != null) {
        scenarios.count { it.type == filterType.scenarioType }
    } else {
        scenarios.size
    }
}

private fun getScenarioTypeColor(type: ScenarioType): Color {
    return when (type) {
        ScenarioType.CAPTAIN_CHANGE -> FplOrange
        ScenarioType.TRANSFER_CHANGE -> FplBlue
        ScenarioType.CHIP_TIMING -> FplPurple
        ScenarioType.TEAM_SELECTION -> FplGreen
    }
}

private fun getScenarioTypeIcon(type: ScenarioType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        ScenarioType.CAPTAIN_CHANGE -> Icons.Default.Star
        ScenarioType.TRANSFER_CHANGE -> Icons.Default.SwapHoriz
        ScenarioType.CHIP_TIMING -> Icons.Default.AutoAwesome
        ScenarioType.TEAM_SELECTION -> Icons.Default.Group
    }
}

private fun getStabilityColor(rating: Double): Color {
    return when {
        rating >= 80 -> FplGreen
        rating >= 60 -> FplBlue
        rating >= 40 -> FplOrange
        else -> FplRed
    }
}

private fun getImpactSummary(scenario: WhatIfScenario): String {
    val affected = scenario.results.count { it.significantChange }
    val total = scenario.results.size

    return when {
        affected == 0 -> "💤 Minimal impact"
        affected < total / 3 -> "📊 Minor changes"
        affected < total * 2 / 3 -> "📈 Moderate impact"
        else -> "🌪️ Major shake-up"
    }
}

private fun calculateWhatIfSummary(scenarios: List<WhatIfScenario>): WhatIfSummary {
    val allResults = scenarios.flatMap { it.results }

    val biggestGain = allResults.maxOfOrNull { it.pointsChange } ?: 0
    val biggestLoss = allResults.minOfOrNull { it.pointsChange } ?: 0

    val gameweekImpacts = scenarios.groupBy { it.gameweek }
        .mapValues { (_, scenarios) ->
            scenarios.flatMap { it.results }.sumOf { kotlin.math.abs(it.rankChange) }
        }

    val mostVolatileGameweek = gameweekImpacts.maxByOrNull { it.value }?.key

    val totalRankChanges = allResults.sumOf { kotlin.math.abs(it.rankChange) }
    val stabilityRating = if (allResults.isNotEmpty()) {
        val averageChange = totalRankChanges.toDouble() / allResults.size
        kotlin.math.max(0.0, 100 - (averageChange * 10))
    } else {
        100.0
    }

    return WhatIfSummary(
        totalScenariosAnalyzed = scenarios.size,
        biggestPotentialGain = biggestGain,
        biggestMissedOpportunity = kotlin.math.abs(biggestLoss),
        mostVolatileGameweek = mostVolatileGameweek,
        stabilityRating = stabilityRating
    )
}