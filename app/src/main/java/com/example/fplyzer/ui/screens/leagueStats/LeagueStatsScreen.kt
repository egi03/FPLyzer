package com.example.fplyzer.ui.screens.leagueStats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.*
import com.example.fplyzer.data.models.statistics.*
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.components.charts.*
import com.example.fplyzer.ui.theme.*
import com.example.fplyzer.ui.components.playerAnalytics.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueStatsScreen(
    viewModel: LeagueStatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    var leagueIdInput by remember { mutableStateOf("") }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = FplBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(FplSecondary, FplSecondaryDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Analytics,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "League Analytics",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FplSurface,
                    titleContentColor = FplTextPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.leagueStatistics == null && !uiState.isLoading -> {
                LeagueInputScreen(
                    paddingValues = paddingValues,
                    leagueIdInput = leagueIdInput,
                    onLeagueIdChange = { leagueIdInput = it },
                    onAnalyze = {
                        if (leagueIdInput.isNotEmpty()) {
                            keyboardController?.hide()
                            viewModel.loadLeagueStatistics(leagueIdInput.toInt())
                        }
                    }
                )
            }

            uiState.isLoading -> {
                LoadingScreen(paddingValues)
            }

            uiState.error != null -> {
                ErrorScreen(
                    error = uiState.error!!,
                    paddingValues = paddingValues,
                    onRetry = {
                        if (leagueIdInput.isNotEmpty()) {
                            viewModel.loadLeagueStatistics(leagueIdInput.toInt())
                        }
                    }
                )
            }

            uiState.leagueStatistics != null -> {
                LeagueStatsContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Composable
fun LeagueInputScreen(
    paddingValues: PaddingValues,
    leagueIdInput: String,
    onLeagueIdChange: (String) -> Unit,
    onAnalyze: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = FplSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Enter League ID",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get comprehensive analytics for any FPL league",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FplTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = leagueIdInput,
                    onValueChange = onLeagueIdChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., 314159") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onAnalyze() }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FplSecondary,
                        unfocusedBorderColor = FplDivider,
                        cursorColor = FplSecondary
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(24.dp))
                ModernButton(
                    onClick = onAnalyze,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = leagueIdInput.isNotEmpty(),
                    text = "Analyze League",
                    icon = Icons.Default.TrendingUp,
                    gradient = listOf(FplSecondary, FplSecondaryDark)
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LeagueStatsContent(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // League header
        LeagueHeaderCard(uiState.leagueStatistics!!)

        // Tabs
        val tabs = listOf("Overview", "Head to Head", "Chips", "Analytics", "Players")
        ScrollableTabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = FplSurface,
            contentColor = FplTextPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = FplSecondary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.setSelectedTab(index) },
                    text = {
                        Text(
                            title,
                            fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        // Tab content
        AnimatedContent(
            targetState = uiState.selectedTab,
            transitionSpec = {
                fadeIn() + slideInHorizontally() with fadeOut() + slideOutHorizontally()
            }
        ) { tab ->
            when (tab) {
                0 -> OverviewTab(uiState, viewModel)
                1 -> HeadToHeadTab(uiState, viewModel)
                2 -> ChipsTab(uiState, viewModel)
                3 -> AnalyticsTab(uiState, viewModel)
                4 -> PlayersTab(uiState, viewModel)
            }
        }
    }
}

@Composable
fun LeagueHeaderCard(stats: LeagueStatistics) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        gradientColors = listOf(FplSecondary, FplSecondaryDark)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stats.leagueInfo.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stats.standings.size} Managers",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FplAccentLight
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = String.format("%.1f", stats.leagueAverages.averagePoints),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Avg Points/GW",
                    style = MaterialTheme.typography.bodySmall,
                    color = FplAccentLight
                )
            }
        }
    }
}

@Composable
fun OverviewTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sorting options
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SortingOption.values()) { option ->
                    ModernChip(
                        text = option.name.replace("_", " ").lowercase().capitalize(),
                        selected = uiState.currentSortOption == option,
                        onClick = { viewModel.setSortOption(option) }
                    )
                }
            }
        }

        // Top performers cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.leagueStatistics?.leagueAverages?.topPerformers?.forEach { metric ->
                    PerformanceCard(
                        modifier = Modifier.weight(1f),
                        metric = metric
                    )
                }
            }
        }

        // Manager rankings
        item {
            Text(
                text = "Manager Rankings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
        }

        itemsIndexed(uiState.sortedManagers) { index, manager ->
            ManagerRankingCard(
                manager = manager,
                rank = index + 1,
                sortOption = uiState.currentSortOption
            )
        }
    }
}

@Composable
fun HeadToHeadTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Select managers to compare (max 5)",
                style = MaterialTheme.typography.titleMedium,
                color = FplTextSecondary
            )
        }

        // Manager selection
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.leagueStatistics?.managerStats?.values?.toList() ?: emptyList()) { manager ->
                    SelectableManagerChip(
                        manager = manager,
                        isSelected = uiState.selectedManagerIds.contains(manager.managerId),
                        onClick = { viewModel.toggleManagerSelection(manager.managerId) }
                    )
                }
            }
        }

        if (uiState.selectedManagerIds.size >= 2) {
            val selectedManagers = uiState.selectedManagerIds.mapNotNull { id ->
                uiState.leagueStatistics?.managerStats?.get(id)
            }

            // Points progression chart
            item {
                val data = selectedManagers.associate { manager ->
                    manager.managerId to manager.pointsHistory
                }
                val labels = selectedManagers.associate { manager ->
                    manager.managerId to manager.managerName
                }

                LineChart(
                    data = data,
                    labels = labels,
                    title = "Points Progression",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Rank progression chart
            item {
                val data = selectedManagers.associate { manager ->
                    manager.managerId to manager.rankHistory
                }
                val labels = selectedManagers.associate { manager ->
                    manager.managerId to manager.managerName
                }

                LineChart(
                    data = data,
                    labels = labels,
                    title = "Rank Progression",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Comparison stats
            item {
                ComparisonStatsCard(selectedManagers)
            }
        }
    }
}

@Composable
fun ChipsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val chipAnalysis = uiState.leagueStatistics?.chipAnalysis

        // Best chips section
        item {
            ChipSectionCard(
                title = "Best Wildcard Usage",
                chips = chipAnalysis?.bestWildcards ?: emptyList(),
                icon = Icons.Default.SwapHoriz,
                color = FplGreen
            )
        }

        item {
            ChipSectionCard(
                title = "Best Bench Boost",
                chips = chipAnalysis?.bestBenchBoosts ?: emptyList(),
                icon = Icons.Default.Groups,
                color = FplBlue
            )
        }

        item {
            ChipSectionCard(
                title = "Best Triple Captain",
                chips = chipAnalysis?.bestTripleCaptains ?: emptyList(),
                icon = Icons.Default.Stars,
                color = FplYellow
            )
        }

        item {
            ChipSectionCard(
                title = "Worst Chip Usage",
                chips = chipAnalysis?.worstChips ?: emptyList(),
                icon = Icons.Default.Error,
                color = FplRed
            )
        }
    }
}

@Composable
fun AnalyticsTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // League averages
        item {
            LeagueAveragesCard(uiState.leagueStatistics?.leagueAverages)
        }

//        // Consistency chart
//        item {
//            val data = uiState.leagueStatistics?.consistency?.take(10)?.map {
//                it.managerName to it.consistency.toFloat()
//            } ?: emptyList()
//
//            BarChart(
//                data = data,
//                title = "Most Consistent Managers",
//                barColor = FplAccent,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }

        // Monthly performance
        item {
            MonthlyPerformanceCard(uiState.leagueStatistics)
        }

        // Transfer efficiency
        item {
            TransferEfficiencyCard(uiState.leagueStatistics)
        }
    }
}

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
        elevation = CardDefaults.cardElevation(2.dp)
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

@Composable
fun SelectableManagerChip(
    manager: ManagerStatistics,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) FplSecondary else FplChipBackground,
        modifier = Modifier.animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = manager.managerName,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.White else FplChipText,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun ComparisonStatsCard(managers: List<ManagerStatistics>) {
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
                text = "Head to Head Comparison",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            val metrics = listOf(
                "Total Points" to managers.map { it.totalPoints.toFloat() },
                "Average Points" to managers.map { it.averagePoints.toFloat() },
                "Transfers" to managers.map { it.totalTransfers.toFloat() },
                "Hits Taken" to managers.map { it.totalHits.toFloat() },
                "Bench Points" to managers.map { it.benchPoints.toFloat() }
            )

            metrics.forEach { (metric, values) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = metric,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplTextPrimary,
                        modifier = Modifier.width(100.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        values.forEachIndexed { index, value ->
                            val manager = managers[index]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = String.format("%.1f", value),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FplTextPrimary
                                )
                                Text(
                                    text = manager.managerName.split(" ").first(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FplTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChipSectionCard(
    title: String,
    chips: List<ChipPerformance>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = FplSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (chips.isEmpty()) {
                Text(
                    text = "No data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            } else {
                chips.forEach { chip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chip.managerName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "GW${chip.gameweek}",
                                style = MaterialTheme.typography.bodySmall,
                                color = FplTextSecondary
                            )
                        }
                        Text(
                            text = "${chip.points} pts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeagueAveragesCard(averages: LeagueAverages?) {
    averages?.let {
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
                    text = "League Averages",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                val stats = listOf(
                    "Points per GW" to it.averagePoints,
                    "Transfers per Manager" to it.averageTransfers,
                    "Hits per Manager" to it.averageHits,
                    "Team Value" to it.averageTeamValue / 10.0,
                    "Bench Points" to it.averageBenchPoints
                )

                stats.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FplTextSecondary
                        )
                        Text(
                            text = if (label.contains("Value")) "£${String.format("%.1f", value)}m"
                            else String.format("%.1f", value),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = FplTextPrimary
                        )
                    }
                }
            }
        }
    }
}

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

                // This would need actual monthly data
                Text(
                    text = "Coming soon...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }
        }
    }
}

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

@Composable
fun LoadingScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(FplBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer { rotationZ = rotation }
                    .clip(CircleShape)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                FplSecondary,
                                FplSecondaryLight,
                                FplSecondary
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(FplBackground)
                )
            }
            Text(
                text = "Analyzing League Data...",
                style = MaterialTheme.typography.titleMedium,
                color = FplTextSecondary
            )
        }
    }
}

@Composable
fun ErrorScreen(
    error: String,
    paddingValues: PaddingValues,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(FplBackground),
        contentAlignment = Alignment.Center
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    modifier = Modifier.size(64.dp),
                    tint = FplError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FplError,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                ModernButton(
                    onClick = onRetry,
                    text = "Try Again",
                    icon = Icons.Default.Refresh
                )
            }
        }
    }
}

@Composable
fun PlayersTab(
    uiState: LeagueStatsUiState,
    viewModel: LeagueStatsViewModel
) {
    if (uiState.isLoadingPlayers) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = FplSecondary)
        }
        return
    }

    val playerAnalytics = uiState.playerAnalytics ?: return

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Position filter
        item {
            val positions = listOf("All", "GKP", "DEF", "MID", "FWD")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(positions) { position ->
                    ModernChip(
                        text = position,
                        selected = (position == "All" && uiState.selectedPosition == null) ||
                                position == uiState.selectedPosition,
                        onClick = {
                            viewModel.setSelectedPosition(if (position == "All") null else position)
                        }
                    )
                }
            }
        }

        // Template team
        item {
            val templatePlayers = playerAnalytics.playerOwnership.filter { it.isTemplate }
            val templateOwnership = uiState.leagueStatistics?.managerStats?.values?.count { manager ->
                // Check if manager has all template players
                true // This would need actual calculation
            }?.toDouble()?.div(uiState.leagueStatistics.managerStats.size) ?: 0.0

            TemplateTeamCard(
                templatePlayers = templatePlayers,
                templateOwnership = templateOwnership
            )
        }

        // Ownership distribution
        item {
            OwnershipDistributionChart(
                playerOwnership = playerAnalytics.playerOwnership,
                position = uiState.selectedPosition
            )
        }

        // Most captained
        item {
            Text(
                text = "Most Captained",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
        }
        items(playerAnalytics.captaincy.take(5)) { captaincy ->
            CaptaincyCard(captaincy)
        }

        // Differentials
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Differentials",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Text(
                    text = "< 10% owned",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }
        }
        items(playerAnalytics.differentials.take(10)) { differential ->
            DifferentialCard(differential)
        }

        // All players by ownership
        item {
            Text(
                text = "All Players by Ownership",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
        }

        val filteredPlayers = if (uiState.selectedPosition != null) {
            playerAnalytics.playerOwnership.filter { it.position == uiState.selectedPosition }
        } else {
            playerAnalytics.playerOwnership
        }

        items(filteredPlayers) { player ->
            PlayerOwnershipCard(player)
        }
    }
}