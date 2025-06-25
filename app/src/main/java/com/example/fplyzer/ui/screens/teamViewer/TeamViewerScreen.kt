package com.example.fplyzer.ui.screens.teamViewer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.ui.components.*
import com.example.fplyzer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamViewerScreen(
    managerId: Int,
    onNavigateBack: () -> Unit,
    viewModel: TeamViewerViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(managerId) {
        viewModel.loadTeamData(managerId)
    }

    Scaffold(
        containerColor = FplBackground,
        topBar = {
            ModernTeamViewerTopBar(
                teamName = uiState.managerInfo?.name ?: "Team",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                ModernLoadingState(paddingValues)
            }

            uiState.error != null -> {
                ModernErrorState(
                    error = uiState.error!!,
                    paddingValues = paddingValues,
                    onRetry = { viewModel.loadTeamData(managerId) }
                )
            }

            uiState.managerTeam != null && uiState.managerInfo != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn()
                        ) {
                            ModernTeamInfoCard(
                                managerName = uiState.managerInfo!!.name,
                                totalPoints = uiState.totalLivePoints,
                                captainPoints = uiState.captainPoints,
                                benchPoints = uiState.benchPoints,
                                gameweek = uiState.currentEvent?.id ?: 0,
                                isLiveDataAvailable = uiState.isLiveDataAvailable
                            )
                        }
                    }

                    if (!uiState.isLiveDataAvailable) {
                        item {
                            InfoCard(
                                text = "Live data is not available as the season has ended. Displaying team without live points.",
                                icon = Icons.Default.Info,
                                color = FplWarning
                            )
                        }
                    }

                    // Active chip
                    uiState.managerTeam!!.activeChip?.let { chip ->
                        item {
                            ModernChipCard(chip = chip)
                        }
                    }

                    // Team formation visualization
                    item {
                        TeamFormationCard(
                            picks = uiState.managerTeam!!.picks,
                            players = uiState.players
                        )
                    }

                    item {
                        SectionDivider(title = "Starting XI")
                    }

                    val startingPicks = uiState.managerTeam!!.picks.filter { it.position <= 11 }
                    items(
                        items = startingPicks,
                        key = { it.element }
                    ) { pick ->
                        val player = uiState.players[pick.element]
                        player?.let {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInHorizontally()
                            ) {
                                ModernPlayerPickCard(
                                    player = it,
                                    points = if (uiState.isLiveDataAvailable)
                                        (uiState.liveData[pick.element]?.totalPoints ?: 0) * pick.multiplier
                                    else 0,
                                    isCaptain = pick.isCaptain,
                                    isViceCaptain = pick.isViceCaptain,
                                    position = pick.position
                                )
                            }
                        }
                    }

                    item {
                        SectionDivider(title = "Bench")
                    }

                    val benchPicks = uiState.managerTeam!!.picks.filter { it.position > 11 }
                    items(
                        items = benchPicks,
                        key = { it.element }
                    ) { pick ->
                        val player = uiState.players[pick.element]
                        player?.let {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInHorizontally()
                            ) {
                                ModernPlayerPickCard(
                                    player = it,
                                    points = if (uiState.isLiveDataAvailable)
                                        uiState.liveData[pick.element]?.totalPoints ?: 0
                                    else 0,
                                    isCaptain = false,
                                    isViceCaptain = false,
                                    position = pick.position,
                                    isBenched = true
                                )
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
fun ModernTeamViewerTopBar(
    teamName: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "TEAM VIEWER",
                    style = MaterialTheme.typography.labelMedium,
                    color = FplTextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = teamName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .background(FplPrimary.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = FplPrimary
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
fun ModernTeamInfoCard(
    managerName: String,
    totalPoints: Int,
    captainPoints: Int,
    benchPoints: Int,
    gameweek: Int,
    isLiveDataAvailable: Boolean
) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        gradientColors = listOf(FplGradientStart, FplGradientMiddle, FplGradientEnd),
        shape = MaterialTheme.shapes.extraLarge
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
                        text = "GAMEWEEK $gameweek",
                        style = MaterialTheme.typography.labelLarge,
                        color = FplAccentLight,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = managerName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (isLiveDataAvailable) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(FplGlass.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = totalPoints.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = FplAccent,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "POINTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = FplAccentLight
                            )
                        }
                    }
                }
            }

            if (isLiveDataAvailable) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    PointsStat(
                        label = "Captain",
                        value = captainPoints,
                        icon = Icons.Default.Star,
                        color = FplYellow
                    )
                    PointsStat(
                        label = "Bench",
                        value = benchPoints,
                        icon = Icons.Default.EventSeat,
                        color = FplOrange
                    )
                    PointsStat(
                        label = "Starting XI",
                        value = totalPoints - captainPoints,
                        icon = Icons.Default.Groups,
                        color = FplAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun PointsStat(
    label: String,
    value: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
fun TeamFormationCard(
    picks: List<com.example.fplyzer.data.models.Pick>,
    players: Map<Int, Element>
) {
    val formation = calculateFormation(picks, players)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = FplGreen.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Formation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FplTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formation,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = FplGreen
            )
        }
    }
}

@Composable
fun ModernPlayerPickCard(
    player: Element,
    points: Int,
    isCaptain: Boolean,
    isViceCaptain: Boolean,
    position: Int,
    isBenched: Boolean = false
) {
    val positionColor = when (player.elementType) {
        1 -> Color(0xFFFFD700) // GKP
        2 -> Color(0xFF00D685) // DEF
        3 -> Color(0xFF05F1FF) // MID
        4 -> Color(0xFFE90052) // FWD
        else -> FplPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(
                if (isBenched) Modifier.graphicsLayer { alpha = 0.7f }
                else Modifier
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isBenched) FplSurface.copy(alpha = 0.7f) else FplSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isBenched) 1.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Position number
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(positionColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = position.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = positionColor
                )
            }

            // Player info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = player.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    when {
                        isCaptain -> {
                            CaptainBadge("C", FplYellow)
                        }
                        isViceCaptain -> {
                            CaptainBadge("VC", FplSecondary)
                        }
                    }
                }
                Text(
                    text = "${getPositionAbbreviation(player.elementType)} • ${player.displayPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FplTextSecondary
                )
            }

            // Points
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = points.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        points >= 10 -> FplGreen
                        points > 0 -> FplTextPrimary
                        else -> FplTextSecondary
                    }
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
private fun CaptainBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = FplPrimaryDark
        )
    }
}

@Composable
fun ModernChipCard(chip: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = FplSecondary
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(FplGlass.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (chip.lowercase()) {
                        "wildcard" -> Icons.Default.Refresh
                        "bboost" -> Icons.Default.RocketLaunch
                        "3xc" -> Icons.Default.Stars
                        "freehit" -> Icons.Default.FlashOn
                        else -> Icons.Default.EmojiEvents
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = "Active Chip",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = getChipDisplayName(chip),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SectionDivider(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(FplDivider)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FplPrimary
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(FplDivider)
        )
    }
}

@Composable
fun InfoCard(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ModernLoadingState(paddingValues: PaddingValues) {
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
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    FplPrimary,
                                    FplSecondary,
                                    FplAccent,
                                    FplPrimary
                                )
                            )
                        )
                )
                // Inner circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(FplBackground)
                )
                // Icon
                Icon(
                    Icons.Default.Groups,
                    contentDescription = null,
                    tint = FplPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = "Loading team...",
                style = MaterialTheme.typography.titleMedium,
                color = FplTextSecondary
            )
        }
    }
}

@Composable
fun ModernErrorState(
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

private fun calculateFormation(
    picks: List<com.example.fplyzer.data.models.Pick>,
    players: Map<Int, Element>
): String {
    val startingPicks = picks.filter { it.position <= 11 }
    var gk = 0
    var def = 0
    var mid = 0
    var fwd = 0

    startingPicks.forEach { pick ->
        players[pick.element]?.let { player ->
            when (player.elementType) {
                1 -> gk++
                2 -> def++
                3 -> mid++
                4 -> fwd++
            }
        }
    }

    return "$def-$mid-$fwd"
}

private fun getPositionAbbreviation(elementType: Int): String {
    return when (elementType) {
        1 -> "GKP"
        2 -> "DEF"
        3 -> "MID"
        4 -> "FWD"
        else -> ""
    }
}

private fun getChipDisplayName(chip: String): String {
    return when (chip.lowercase()) {
        "wildcard" -> "Wildcard"
        "bboost" -> "Bench Boost"
        "3xc" -> "Triple Captain"
        "freehit" -> "Free Hit"
        else -> chip
    }
}