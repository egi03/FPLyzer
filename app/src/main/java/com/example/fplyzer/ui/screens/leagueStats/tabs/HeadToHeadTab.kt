package com.example.fplyzer.ui.screens.leagueStats.tabs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fplyzer.data.models.statistics.*
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*
import com.example.fplyzer.ui.screens.leagueStats.*

@Composable
fun HeadToHeadTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    val stats = uiState.leagueStatistics ?: return
    var selectedManagerId by remember { mutableStateOf<Int?>(null) }
    var expandedRecordId by remember { mutableStateOf<String?>(null) }
    var showingDetailSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with manager selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Theme-aware
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f) // Theme-aware
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
                        text = "Head-to-Head Analysis",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface // Theme-aware
                    )
                    Text(
                        text = "Compare manager performance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                    )
                }

                selectedManagerId?.let {
                    IconButton(
                        onClick = { showingDetailSheet = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(FplSecondary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "View Stats",
                            tint = FplSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Manager Selector
            ManagerSelector(
                managers = stats.managerStats.values.toList(),
                selectedManagerId = selectedManagerId,
                onManagerSelected = { selectedManagerId = it }
            )
        }

        // Main content
        if (selectedManagerId == null) {
            EmptyH2HState()
        } else {
            val selectedManager = stats.managerStats[selectedManagerId]
            if (selectedManager != null) {
                val h2hRecords = calculateH2HRecords(selectedManager, stats)

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Manager overview
                    item {
                        ManagerH2HOverview(
                            manager = selectedManager,
                            records = h2hRecords
                        )
                    }

                    // Individual matchups
                    items(h2hRecords.sortedByDescending { it.winPercentage }) { record ->
                        H2HRecordCard(
                            record = record,
                            selectedManagerName = selectedManager.managerName,
                            isExpanded = expandedRecordId == record.opponentId,
                            onToggleExpand = {
                                expandedRecordId = if (expandedRecordId == record.opponentId) {
                                    null
                                } else {
                                    record.opponentId
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showingDetailSheet && selectedManagerId != null) {
        val manager = stats.managerStats[selectedManagerId]
        if (manager != null) {
            H2HDetailSheet(
                manager = manager,
                records = calculateH2HRecords(manager, stats),
                onDismiss = { showingDetailSheet = false }
            )
        }
    }
}

@Composable
private fun ManagerSelector(
    managers: List<ManagerStatistics>,
    selectedManagerId: Int?,
    onManagerSelected: (Int) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.PersonPin,
                contentDescription = null,
                tint = FplPrimary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Select Manager",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )

            Spacer(modifier = Modifier.weight(1f))

            if (selectedManagerId != null) {
                TextButton(
                    onClick = { onManagerSelected(-1) } // Clear selection
                ) {
                    Text("Clear")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(managers) { manager ->
                ManagerChip(
                    manager = manager,
                    isSelected = selectedManagerId == manager.managerId,
                    onClick = { onManagerSelected(manager.managerId) }
                )
            }
        }
    }
}

@Composable
private fun ManagerChip(
    manager: ManagerStatistics,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .scale(scale)
            .width(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                FplPrimary
            } else {
                MaterialTheme.colorScheme.surfaceVariant // Theme-aware
            }
        ),
        border = if (!isSelected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Theme-aware
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            Brush.radialGradient(
                                colors = listOf(FplAccent, FplAccentDark)
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = manager.managerName.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) FplPrimaryDark else MaterialTheme.colorScheme.onSurface // Theme-aware
                )
            }

            Text(
                text = manager.managerName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface, // Theme-aware
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ManagerH2HOverview(
    manager: ManagerStatistics,
    records: List<H2HRecord>
) {
    val totalGames = records.sumOf { it.wins + it.draws + it.losses }
    val totalWins = records.sumOf { it.wins }
    val totalDraws = records.sumOf { it.draws }
    val totalLosses = records.sumOf { it.losses }
    val winPercentage = if (totalGames > 0) totalWins.toDouble() / totalGames * 100 else 0.0

    val bestMatchup = records.maxByOrNull { it.winPercentage }
    val worstMatchup = records.minByOrNull { it.winPercentage }

    GradientCard(
        modifier = Modifier.fillMaxWidth(),
        gradientColors = listOf(FplPrimary, FplPrimaryDark)
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
                        text = "${manager.managerName}'s Record",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = getDominanceEmoji(winPercentage),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = getDominanceLabel(winPercentage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplAccentLight
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format("%.0f", winPercentage),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = FplAccent
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FplAccentLight,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Text(
                        text = "win rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplAccentLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Win/Draw/Loss breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                H2HStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Wins",
                    value = totalWins.toString(),
                    subtitle = "victories",
                    color = FplGreen,
                    icon = Icons.Default.CheckCircle
                )

                H2HStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Draws",
                    value = totalDraws.toString(),
                    subtitle = "ties",
                    color = FplOrange,
                    icon = Icons.Default.PauseCircle
                )

                H2HStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Losses",
                    value = totalLosses.toString(),
                    subtitle = "defeats",
                    color = FplRed,
                    icon = Icons.Default.Cancel
                )
            }

            // Best/Worst matchups
            if (bestMatchup != null || worstMatchup != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    bestMatchup?.let {
                        MatchupHighlight(
                            modifier = Modifier.weight(1f),
                            title = "Best vs",
                            opponent = it.opponentName,
                            winRate = it.winPercentage,
                            icon = Icons.Default.Star,
                            color = FplYellow
                        )
                    }

                    worstMatchup?.let {
                        MatchupHighlight(
                            modifier = Modifier.weight(1f),
                            title = "Struggles vs",
                            opponent = it.opponentName,
                            winRate = it.winPercentage,
                            icon = Icons.Default.Warning,
                            color = FplOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun H2HStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
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
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.White // Keep white for contrast on colored backgrounds
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f) // Keep white for contrast
                )
            }
        }
    }
}

@Composable
private fun MatchupHighlight(
    modifier: Modifier = Modifier,
    title: String,
    opponent: String,
    winRate: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FplAccent.copy(alpha = 0.1f))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = FplAccentLight
                )
                Text(
                    text = opponent,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${winRate.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FplAccent
            )
        }
    }
}

@Composable
private fun H2HRecordCard(
    record: H2HRecord,
    selectedManagerName: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        onClick = onToggleExpand,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme-aware
        elevation = CardDefaults.cardElevation(2.dp) // Added slight elevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Opponent info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "vs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                        )
                        Text(
                            text = record.opponentName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface // Theme-aware
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = getDominanceEmoji(record.winPercentage),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(getDominanceColor(record.winPercentage).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = getDominanceLabel(record.winPercentage),
                                style = MaterialTheme.typography.labelSmall,
                                color = getDominanceColor(record.winPercentage),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // W-D-L Record
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RecordBadge(value = record.wins, label = "W", color = FplGreen)
                        RecordBadge(value = record.draws, label = "D", color = FplOrange)
                        RecordBadge(value = record.losses, label = "L", color = FplRed)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${record.winPercentage.toInt()}% Win Rate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                    )
                }

                // Expand arrow
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expanded content
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant) // Theme-aware

                    Spacer(modifier = Modifier.height(16.dp))

                    // Points comparison
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PointsComparison(
                            title = "Points For",
                            value = record.totalPointsFor,
                            color = FplGreen
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Difference",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (record.pointsDifference >= 0) {
                                        Icons.Default.ArrowUpward
                                    } else {
                                        Icons.Default.ArrowDownward
                                    },
                                    contentDescription = null,
                                    tint = if (record.pointsDifference >= 0) FplGreen else FplRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = kotlin.math.abs(record.pointsDifference).toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (record.pointsDifference >= 0) FplGreen else FplRed
                                )
                            }
                        }

                        PointsComparison(
                            title = "Points Against",
                            value = record.totalPointsAgainst,
                            color = FplRed
                        )
                    }

                    // Recent form or additional stats could go here
                    record.recentResults?.let { results ->
                        Spacer(modifier = Modifier.height(16.dp))
                        RecentFormSection(results = results)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordBadge(
    value: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
        )
    }
}

@Composable
private fun PointsComparison(
    title: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun RecentFormSection(results: List<H2HResult>) {
    Column {
        Text(
            text = "Recent Meetings",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface // Theme-aware
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            results.takeLast(5).forEach { result ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when (result.outcome) {
                                H2HOutcome.WIN -> FplGreen
                                H2HOutcome.DRAW -> FplOrange
                                H2HOutcome.LOSS -> FplRed
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.outcome.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyH2HState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(FplPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Groups,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = FplPrimary.copy(alpha = 0.3f)
                )
            }

            Text(
                text = "Select a Manager",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )

            Text(
                text = "Choose a manager from the list above to see their head-to-head record against all other managers in the league",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
                textAlign = TextAlign.Center
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(icon = Icons.Default.EmojiEvents, text = "Win/Loss records")
                InfoRow(icon = Icons.Default.BarChart, text = "Points comparisons")
                InfoRow(icon = Icons.Default.Star, text = "Best and worst performances")
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
        )
    }
}

@Composable
private fun H2HDetailSheet(
    manager: ManagerStatistics,
    records: List<H2HRecord>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.9f),
        title = {
            Column {
                Text(
                    text = "${manager.managerName} Stats",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface // Theme-aware
                )
                Text(
                    text = "Detailed H2H Analysis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary stats
                item {
                    val totalGames = records.sumOf { it.totalGames }
                    val totalWins = records.sumOf { it.wins }
                    val dominantWins = records.count { it.winPercentage > 70 }
                    val strugglingAgainst = records.count { it.winPercentage < 30 }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface // Theme-aware
                        )

                        DetailStatRow(
                            label = "Total Games",
                            value = totalGames.toString(),
                            icon = Icons.Default.SportsScore
                        )

                        DetailStatRow(
                            label = "Overall Win Rate",
                            value = "${(totalWins.toDouble() / totalGames * 100).toInt()}%",
                            icon = Icons.Default.Percent
                        )

                        if (dominantWins > 0) {
                            DetailStatRow(
                                label = "Dominant matchups",
                                value = dominantWins.toString(),
                                icon = Icons.Default.MilitaryTech,
                                color = FplSecondary
                            )
                        }

                        if (strugglingAgainst > 0) {
                            DetailStatRow(
                                label = "Struggling against",
                                value = strugglingAgainst.toString(),
                                icon = Icons.Default.Warning,
                                color = FplRed
                            )
                        }
                    }
                }

                // All matchups
                item {
                    Text(
                        text = "All Matchups",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface // Theme-aware
                    )
                }

                items(records.sortedByDescending { it.winPercentage }) { record ->
                    CompactH2HRow(record = record)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun DetailStatRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = FplPrimary
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
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun CompactH2HRow(record: H2HRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Theme-aware
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = record.opponentName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface, // Theme-aware
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${record.wins}-${record.draws}-${record.losses}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware
            )

            Text(
                text = "${record.winPercentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = getDominanceColor(record.winPercentage),
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

// Data classes and helper functions remain the same...
data class H2HRecord(
    val opponentId: String,
    val opponentName: String,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val totalPointsFor: Int,
    val totalPointsAgainst: Int,
    val recentResults: List<H2HResult>? = null
) {
    val totalGames: Int = wins + draws + losses
    val winPercentage: Double = if (totalGames > 0) wins.toDouble() / totalGames * 100 else 0.0
    val pointsDifference: Int = totalPointsFor - totalPointsAgainst
}

data class H2HResult(
    val gameweek: Int,
    val outcome: H2HOutcome,
    val pointsFor: Int,
    val pointsAgainst: Int
)

enum class H2HOutcome(val label: String) {
    WIN("W"),
    DRAW("D"),
    LOSS("L")
}

private fun calculateH2HRecords(
    manager: ManagerStatistics,
    stats: LeagueStatistics
): List<H2HRecord> {
    return stats.managerStats.values
        .filter { it.managerId != manager.managerId }
        .map { opponent ->
            // Calculate head-to-head record
            val results = manager.pointsHistory.zip(opponent.pointsHistory).mapIndexed { index, (myPoints, theirPoints) ->
                when {
                    myPoints > theirPoints -> H2HResult(index + 1, H2HOutcome.WIN, myPoints, theirPoints)
                    myPoints < theirPoints -> H2HResult(index + 1, H2HOutcome.LOSS, myPoints, theirPoints)
                    else -> H2HResult(index + 1, H2HOutcome.DRAW, myPoints, theirPoints)
                }
            }

            H2HRecord(
                opponentId = opponent.managerId.toString(),
                opponentName = opponent.managerName,
                wins = results.count { it.outcome == H2HOutcome.WIN },
                draws = results.count { it.outcome == H2HOutcome.DRAW },
                losses = results.count { it.outcome == H2HOutcome.LOSS },
                totalPointsFor = results.sumOf { it.pointsFor },
                totalPointsAgainst = results.sumOf { it.pointsAgainst },
                recentResults = results.takeLast(5)
            )
        }
}

private fun getDominanceEmoji(winPercentage: Double): String {
    return when {
        winPercentage >= 80 -> "👑"
        winPercentage >= 60 -> "💪"
        winPercentage >= 40 -> "⚔️"
        else -> "😅"
    }
}

private fun getDominanceLabel(winPercentage: Double): String {
    return when {
        winPercentage >= 80 -> "Dominant"
        winPercentage >= 60 -> "Strong"
        winPercentage >= 40 -> "Competitive"
        else -> "Struggling"
    }
}

private fun getDominanceColor(winPercentage: Double): Color {
    return when {
        winPercentage >= 80 -> FplSecondary
        winPercentage >= 60 -> FplGreen
        winPercentage >= 40 -> FplBlue
        else -> FplRed
    }
}