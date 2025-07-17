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
import com.example.fplyzer.data.models.differentials.*
import com.example.fplyzer.ui.components.GlassmorphicCard
import com.example.fplyzer.ui.components.ModernChip
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsUiState
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel
import com.example.fplyzer.ui.theme.*

@Composable
fun DifferentialsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    var selectedManager by remember { mutableStateOf<Int?>(null) }
    var sortBy by remember { mutableStateOf(DifferentialSortType.SUCCESS_RATE) }
    var showingInfoSheet by remember { mutableStateOf(false) }

    val sortedAnalyses = remember(sortBy, uiState.differentialAnalyses) {
        when (sortBy) {
            DifferentialSortType.SUCCESS_RATE -> uiState.differentialAnalyses.sortedByDescending { it.differentialSuccessRate }
            DifferentialSortType.TOTAL_POINTS -> uiState.differentialAnalyses.sortedByDescending { it.totalDifferentialPoints }
            DifferentialSortType.RISK_LEVEL -> uiState.differentialAnalyses.sortedByDescending { getRiskValue(it.riskRating) }
            DifferentialSortType.DIFFERENTIAL_COUNT -> uiState.differentialAnalyses.sortedByDescending { it.differentialModelPicks.size }
        }
    }

    val leagueSummary = remember(uiState.differentialAnalyses) {
        if (uiState.differentialAnalyses.isNotEmpty()) {
            calculateLeagueSummary(uiState.differentialAnalyses)
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
            if (leagueSummary != null) {
                DifferentialHeaderCard(
                    summary = leagueSummary,
                    onInfoClick = { showingInfoSheet = true }
                )
            }
        }

        // Loading State
        if (uiState.isLoadingDifferentials) {
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
                            text = "Analyzing differentials...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant 
                        )
                    }
                }
            }
        } else {
            // Sort Options
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DifferentialSortType.values()) { type ->
                        DifferentialSortChip(
                            type = type,
                            isSelected = sortBy == type,
                            onClick = { sortBy = type }
                        )
                    }
                }
            }

            // Manager Analysis Cards
            if (sortedAnalyses.isEmpty()) {
                item {
                    EmptyDifferentialView()
                }
            } else {
                itemsIndexed(sortedAnalyses) { index, analysis ->
                    DifferentialAnalysisCard(
                        analysis = analysis,
                        rank = index + 1,
                        isExpanded = selectedManager == analysis.managerId,
                        onTap = {
                            selectedManager = if (selectedManager == analysis.managerId) null else analysis.managerId
                        }
                    )
                }
            }
        }
    }

    if (showingInfoSheet) {
        DifferentialInfoDialog(
            onDismiss = { showingInfoSheet = false }
        )
    }
}

enum class DifferentialSortType(val label: String, val icon: String) {
    SUCCESS_RATE("Success Rate", "percent"),
    TOTAL_POINTS("Total Points", "sum"),
    RISK_LEVEL("Risk Level", "exclamationmark.triangle"),
    DIFFERENTIAL_COUNT("Differential Count", "number")
}

@Composable
private fun DifferentialHeaderCard(
    summary: LeagueDifferentialSummary?,
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
                        text = "DIFFERENTIAL ANALYSIS",
                        style = MaterialTheme.typography.labelLarge,
                        color = FplAccentLight,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Unique picks that made the difference",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface, 
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) 
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Total",
                        value = "${summary.totalDifferentials}",
                        subtitle = "differentials",
                        color = FplBlue
                    )
                    SummaryStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Success Rate",
                        value = "${summary.successRate.toInt()}%",
                        subtitle = "paid off",
                        color = if (summary.successRate > 50) FplGreen else FplOrange
                    )
                    SummaryStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Best Pick",
                        value = summary.topDifferential?.player?.webName ?: "-",
                        subtitle = "${summary.topDifferential?.pointsScored ?: 0} pts",
                        color = FplPurple
                    )
                }
            } else {
                // Placeholder when no data
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Total",
                        value = "-",
                        subtitle = "differentials",
                        color = FplBlue
                    )
                    SummaryStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Success Rate",
                        value = "-",
                        subtitle = "calculating",
                        color = FplGreen
                    )
                    SummaryStatBox(
                        modifier = Modifier.weight(1f),
                        title = "Best Pick",
                        value = "-",
                        subtitle = "analyzing",
                        color = FplPurple
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryStatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
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
            Text(
                text = title.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface, 
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DifferentialSortChip(
    type: DifferentialSortType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ModernChip(
        text = type.label,
        selected = isSelected,
        onClick = onClick
    )
}

@Composable
private fun DifferentialAnalysisCard(
    analysis: DifferentialAnalysis,
    rank: Int,
    isExpanded: Boolean,
    onTap: () -> Unit
) {
    val rankColor = when (rank) {
        1 -> FplPurple
        2 -> FplBlue
        3 -> FplGreen
        else -> FplGray
    }

    val successRateColor = when {
        analysis.differentialSuccessRate > 70 -> FplGreen
        analysis.differentialSuccessRate > 50 -> FplBlue
        analysis.differentialSuccessRate > 30 -> FplOrange
        else -> FplRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), 
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(rankColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = analysis.managerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface 
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RiskLevelBadge(level = analysis.riskRating)

                        Text(
                            text = "${analysis.differentialModelPicks.size} differentials",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant 
                        )
                    }

                    analysis.biggestSuccess?.let { pick ->
                        Text(
                            text = "Best: ${pick.player.webName} (${pick.pointsScored} pts)",
                            style = MaterialTheme.typography.bodySmall,
                            color = FplPurple
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${analysis.differentialSuccessRate.toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = successRateColor
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, 
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "success rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant 
                    )
                    Text(
                        text = "${analysis.totalDifferentialPoints} pts",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = FplBlue
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant 
                )
            }

            // Expanded Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant) 

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Differential Picks
                        if (analysis.differentialModelPicks.isNotEmpty()) {
                            DifferentialPicksSection(
                                picks = analysis.differentialModelPicks
                                    .sortedByDescending { it.differentialScore }
                                    .take(5)
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun RiskLevelBadge(level: RiskLevel) {
    val color = when (level) {
        RiskLevel.CONSERVATIVE -> FplBlue
        RiskLevel.BALANCED -> FplGreen
        RiskLevel.AGGRESSIVE -> FplOrange
        RiskLevel.RECKLESS -> FplRed
    }

    Text(
        text = level.label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
private fun DifferentialPicksSection(picks: List<DifferentialModelPick>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Top Differential Picks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface 
            )
            Text(
                text = "Best ${picks.size} picks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant 
            )
        }

        picks.forEachIndexed { index, pick ->
            DifferentialPickRow(
                pick = pick,
                rank = index + 1
            )
        }
    }
}


@Composable
private fun DifferentialPickRow(
    pick: DifferentialModelPick,
    rank: Int? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant) 
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        rank?.let {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> FplGreen
                            2 -> FplBlue
                            3 -> FplOrange
                            else -> FplGray
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Position Badge
        PositionBadge(position = pick.player.elementType)

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pick.player.webName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface 
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${pick.gameweeksPicked.size} GWs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant 
                )
                Text(
                    text = "${pick.leagueOwnership.toInt()}% owned",
                    style = MaterialTheme.typography.bodySmall,
                    color = FplBlue
                )
                // Show differential score
                Text(
                    text = "Score: ${String.format("%.1f", pick.differentialScore)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FplPurple
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = pick.outcome.emoji,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${pick.pointsScored}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = getOutcomeColor(pick.outcome)
                )
            }
            OutcomeBadge(outcome = pick.outcome)
        }
    }
}


@Composable
private fun MissedOpportunitiesSection(missed: List<MissedDifferential>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Missed Opportunities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface 
            )
            Text(
                text = "${missed.size} missed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant 
            )
        }

        missed.forEach { miss ->
            MissedOpportunityRow(missed = miss)
        }
    }
}

@Composable
private fun MissedOpportunityRow(missed: MissedDifferential) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(FplOrange.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = FplOrange,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = missed.player.webName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface 
            )
            Text(
                text = "Owned by: ${missed.ownedByManagers.take(3).joinToString(", ")}",
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
                text = "${missed.pointsMissed}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = FplRed
            )
            Text(
                text = "pts missed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant 
            )
        }
    }
}

@Composable
private fun PositionBadge(position: Int) {
    val (text, color) = when (position) {
        1 -> "GKP" to Color(0xFFFFD700)
        2 -> "DEF" to Color(0xFF00D685)
        3 -> "MID" to Color(0xFF05F1FF)
        4 -> "FWD" to Color(0xFFE90052)
        else -> "???" to FplGray
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun OutcomeBadge(outcome: DifferentialOutcome) {
    val color = getOutcomeColor(outcome)

    Text(
        text = outcome.label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    )
}

@Composable
private fun EmptyDifferentialView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = FplGray.copy(alpha = 0.5f)
        )

        Text(
            text = "No Differential Data",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant 
        )

        Text(
            text = "Differential analysis will appear here once ownership data is available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DifferentialInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Understanding Differentials",
                color = MaterialTheme.colorScheme.onSurface 
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "What are Differentials?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface 
                )
                Text(
                    text = "Differential picks are players owned by few managers in your league compared to the general FPL population. They're risky but can provide huge advantages when they pay off.",
                    color = MaterialTheme.colorScheme.onSurface 
                )

                Text(
                    text = "Risk Levels:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface 
                )
                Text(
                    text = "• Conservative: Sticks to popular template players\n" +
                            "• Balanced: Good mix of template and differentials\n" +
                            "• Aggressive: Bold differential choices\n" +
                            "• Reckless: High-risk, high-reward strategy",
                    color = MaterialTheme.colorScheme.onSurface 
                )

                Text(
                    text = "Strategy Tips:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface 
                )
                Text(
                    text = "• Time differentials around good fixtures\n" +
                            "• Don't go too differential in defense\n" +
                            "• Balance risk across your team\n" +
                            "• Sometimes template is template for a reason!",
                    color = MaterialTheme.colorScheme.onSurface 
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

private fun getRiskValue(risk: RiskLevel): Int {
    return when (risk) {
        RiskLevel.CONSERVATIVE -> 1
        RiskLevel.BALANCED -> 2
        RiskLevel.AGGRESSIVE -> 3
        RiskLevel.RECKLESS -> 4
    }
}

private fun getOutcomeColor(outcome: DifferentialOutcome): Color {
    return when (outcome) {
        DifferentialOutcome.MASTER_STROKE -> FplPurple
        DifferentialOutcome.GOOD_PICK -> FplGreen
        DifferentialOutcome.NEUTRAL -> FplBlue
        DifferentialOutcome.POOR_CHOICE -> FplOrange
        DifferentialOutcome.DISASTER -> FplRed
    }
}

private fun calculateLeagueSummary(analyses: List<DifferentialAnalysis>): LeagueDifferentialSummary {
    val allDifferentials = analyses.flatMap { it.differentialModelPicks }
    val totalDifferentials = allDifferentials.size

    val successfulPicks = allDifferentials.filter { pick ->
        pick.outcome == DifferentialOutcome.MASTER_STROKE || pick.outcome == DifferentialOutcome.GOOD_PICK
    }
    val successfulCount = successfulPicks.size

    val successRate = if (totalDifferentials > 0) (successfulCount.toDouble() / totalDifferentials) * 100 else 0.0

    val topDifferential = allDifferentials.maxByOrNull { it.differentialScore }
    val worstDifferential = allDifferentials.minByOrNull { it.differentialScore }

    val mostConservative = analyses.minByOrNull { it.differentialModelPicks.size }?.managerName
    val mostAggressive = analyses.maxByOrNull { it.differentialModelPicks.size }?.managerName

    return LeagueDifferentialSummary(
        totalDifferentials = totalDifferentials,
        successfulDifferentials = successfulCount,
        failedDifferentials = totalDifferentials - successfulCount,
        successRate = successRate,
        topDifferential = topDifferential,
        worstDifferential = worstDifferential,
        mostConservativeManager = mostConservative,
        mostAggressiveManager = mostAggressive,
        averageRiskLevel = RiskLevel.BALANCED
    )
}