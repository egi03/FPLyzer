package com.example.fplyzer.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.Event
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    onNavigateToManager: (Int) -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToTeamViewer: (Int) -> Unit,
    onNavigateToLeagueStats: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    Scaffold(
        containerColor = FplBackground,
        topBar = {
            ModernTopBar(
                onNavigateToPlayers = onNavigateToPlayers
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                ModernLoadingScreen(paddingValues)
            }

            uiState.error != null -> {
                ModernErrorScreen(
                    error = uiState.error!!,
                    paddingValues = paddingValues,
                    onRetry = { viewModel.loadDashboardData() }
                )
            }

            uiState.bootstrapData != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Gameweek Info
                    uiState.currentEvent?.let { event ->
                        item {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically()
                            ) {
                                ModernGameweekCard(event)
                            }
                        }
                    }

                    // Manager ID Input
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300, 100)) +
                                    slideInVertically(animationSpec = tween(300, 100))
                        ) {
                            ModernManagerInput(onNavigateToManager = onNavigateToManager)
                        }
                    }

                    // League Analytics Card
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300, 200)) +
                                    slideInVertically(animationSpec = tween(300, 200))
                        ) {
                            LeagueAnalyticsCard(onNavigateToLeagueStats = onNavigateToLeagueStats)
                        }
                    }

                    // Quick Stats
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                AnimatedStatCard(
                                    title = "Top Scorer",
                                    value = "${uiState.topPlayers.firstOrNull()?.totalPoints ?: 0} pts",
                                    icon = Icons.Default.EmojiEvents,
                                    modifier = Modifier.width(160.dp),
                                    containerColor = FplYellow,
                                    delay = 200
                                )
                            }
                            item {
                                AnimatedStatCard(
                                    title = "Most Selected",
                                    value = "${uiState.topPlayers.maxByOrNull { it.selectedByPercent.toFloatOrNull() ?: 0f }?.selectedByPercent ?: "0"}%",
                                    icon = Icons.Default.Groups,
                                    modifier = Modifier.width(160.dp),
                                    containerColor = FplBlue,
                                    delay = 300
                                )
                            }
                            item {
                                AnimatedStatCard(
                                    title = "Top Transfer",
                                    value = "+${uiState.mostTransferredIn.firstOrNull()?.transfersInEvent ?: 0}",
                                    icon = Icons.Default.TrendingUp,
                                    modifier = Modifier.width(160.dp),
                                    containerColor = FplGreen,
                                    delay = 400
                                )
                            }
                        }
                    }

                    // Section Tabs
                    item {
                        var selectedTab by remember { mutableStateOf(0) }
                        val tabs = listOf("Top Players", "Transfers", "Dream Team")

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(tabs) { index, tab ->
                                ModernChip(
                                    text = tab,
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn() + slideInHorizontally() with
                                        fadeOut() + slideOutHorizontally()
                            }
                        ) { tab ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val players = when (tab) {
                                    0 -> uiState.topPlayers.take(5)
                                    1 -> uiState.mostTransferredIn.take(5)
                                    else -> uiState.dreamTeam.take(5)
                                }

                                players.forEachIndexed { index, player ->
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn(
                                            animationSpec = tween(300, index * 50)
                                        ) + slideInHorizontally(
                                            animationSpec = tween(300, index * 50)
                                        )
                                    ) {
                                        ModernPlayerCard(
                                            player = player,
                                            rank = index + 1,
                                            onClick = { onNavigateToPlayer(player.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar(
    onNavigateToPlayers: () -> Unit
) {
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
                                colors = listOf(FplAccent, FplAccentDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FPL",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = FplPrimaryDark
                    )
                }
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(
                onClick = onNavigateToPlayers,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(FplAccent.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.People,
                    contentDescription = "Players",
                    tint = FplAccent
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FplSurface,
            titleContentColor = FplTextPrimary
        )
    )
}

@Composable
fun ModernGameweekCard(event: Event) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        gradientColors = listOf(FplGradientStart, FplGradientMiddle, FplGradientEnd)
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
                        text = "GAMEWEEK",
                        style = MaterialTheme.typography.labelLarge,
                        color = FplAccentLight,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = event.id.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
                if (event.isCurrent) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PulsingDot(color = FplAccent)
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelMedium,
                            color = FplAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Average",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplAccentLight
                    )
                    Text(
                        text = "${event.averageEntryScore} pts",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Highest",
                        style = MaterialTheme.typography.bodySmall,
                        color = FplAccentLight
                    )
                    Text(
                        text = "${event.highestScore ?: "-"} pts",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Deadline: ${formatDeadline(event.deadlineTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = FplAccentLight
            )
        }
    }
}

@Composable
fun ModernManagerInput(onNavigateToManager: (Int) -> Unit) {
    var managerIdInput by remember { mutableStateOf("") }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Enter Manager ID",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = managerIdInput,
                onValueChange = { managerIdInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., 123456") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (managerIdInput.isNotEmpty()) {
                            keyboardController?.hide()
                            onNavigateToManager(managerIdInput.toInt())
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FplAccent,
                    unfocusedBorderColor = FplDivider,
                    focusedLabelColor = FplAccent,
                    cursorColor = FplAccent
                ),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            ModernButton(
                onClick = {
                    if (managerIdInput.isNotEmpty()) {
                        keyboardController?.hide()
                        onNavigateToManager(managerIdInput.toInt())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = managerIdInput.isNotEmpty(),
                text = "View Manager",
                icon = Icons.Default.Search
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPlayerCard(
    player: Element,
    rank: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = FplSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${player.pointsPerGame} pts/game • ${player.displayPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${player.eventPoints}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FplGreen
                )
                Text(
                    text = "pts",
                    style = MaterialTheme.typography.bodySmall,
                    color = FplTextSecondary
                )
            }
        }
    }
}

@Composable
fun ModernLoadingScreen(paddingValues: PaddingValues) {
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
                    .rotate(rotation)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                FplAccent,
                                FplSecondary,
                                FplAccent
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
                text = "Loading FPL Data...",
                style = MaterialTheme.typography.titleMedium,
                color = FplTextSecondary
            )
        }
    }
}

@Composable
fun ModernErrorScreen(
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
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(FplError.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(40.dp),
                        tint = FplError
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Oops! Something went wrong",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FplTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                ModernButton(
                    onClick = onRetry,
                    text = "Try Again",
                    icon = Icons.Default.Refresh,
                    gradient = listOf(FplError, FplPink)
                )
            }
        }
    }
}

private fun formatDeadline(deadline: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        outputFormat.timeZone = TimeZone.getDefault()
        val date = inputFormat.parse(deadline) ?: Date()
        outputFormat.format(date)
    } catch (e: Exception) {
        deadline
    }
}

@Composable
fun LeagueAnalyticsCard(onNavigateToLeagueStats: () -> Unit) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        gradientColors = listOf(FplSecondary, FplSecondaryDark),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FplGlass.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = "League Analytics",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Column {
                    Text(
                        text = "League Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Deep dive into league statistics",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FplAccentLight
                    )
                }
            }
            IconButton(
                onClick = onNavigateToLeagueStats,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(FplGlass.copy(alpha = 0.2f))
            ) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Go to Analytics",
                    tint = FplAccent
                )
            }
        }
    }
}