package com.example.fplyzer.ui.screens.leagueStats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.statistics.*
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*
import com.example.fplyzer.ui.components.playerAnalytics.*
import com.example.fplyzer.ui.screens.leagueStats.cards.ChipSectionCard
import com.example.fplyzer.ui.screens.leagueStats.cards.ComparisonStatsCard
import com.example.fplyzer.ui.screens.leagueStats.cards.LeagueAveragesCard
import com.example.fplyzer.ui.screens.leagueStats.cards.LeagueHeaderCard
import com.example.fplyzer.ui.screens.leagueStats.cards.ManagerRankingCard
import com.example.fplyzer.ui.screens.leagueStats.cards.MonthlyPerformanceCard
import com.example.fplyzer.ui.screens.leagueStats.cards.PerformanceCard
import com.example.fplyzer.ui.screens.leagueStats.cards.SelectableManagerChip
import com.example.fplyzer.ui.screens.leagueStats.cards.TransferEfficiencyCard
import com.example.fplyzer.ui.screens.leagueStats.screens.ErrorScreen
import com.example.fplyzer.ui.screens.leagueStats.screens.LoadingScreen
import com.example.fplyzer.ui.screens.leagueStats.tabs.AnalyticsTab
import com.example.fplyzer.ui.screens.leagueStats.tabs.ChipsTab
import com.example.fplyzer.ui.screens.leagueStats.tabs.HeadToHeadTab
import com.example.fplyzer.ui.screens.leagueStats.tabs.OverviewTab
import com.example.fplyzer.ui.screens.leagueStats.tabs.PlayersTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueStatsScreen(
    leagueId: Int,
    onNavigateBack: () -> Unit,
    viewModel: LeagueStatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    // Load league data when screen opens
    LaunchedEffect(leagueId) {
        viewModel.loadLeagueStatistics(leagueId)
    }

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
                        Column {
                            Text(
                                text = "League Analytics",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.leagueStatistics != null) {
                                Text(
                                    text = "ID: $leagueId",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FplTextSecondary
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(FplSecondary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = FplSecondary
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
            uiState.isLoading -> {
                LoadingScreen(paddingValues)
            }

            uiState.error != null -> {
                ErrorScreen(
                    error = uiState.error!!,
                    paddingValues = paddingValues,
                    onRetry = {
                        viewModel.loadLeagueStatistics(leagueId)
                    },
                    onNavigateBack = onNavigateBack
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