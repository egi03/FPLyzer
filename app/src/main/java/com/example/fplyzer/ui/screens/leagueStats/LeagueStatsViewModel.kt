package com.example.fplyzer.ui.screens.leagueStats

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fplyzer.data.manager.FavouriteLeaguesManager
import com.example.fplyzer.data.mock.MockDataFactory
import com.example.fplyzer.data.models.*
import com.example.fplyzer.data.models.differentials.DifferentialAnalysis
import com.example.fplyzer.data.models.statistics.*
import com.example.fplyzer.data.models.whatif.WhatIfScenario
import com.example.fplyzer.data.repository.FplRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

import kotlinx.coroutines.delay


data class LeagueStatsUiState(
    val leagueId: Int = 0,
    val leagueStatistics: LeagueStatistics? = null,
    val sortedManagers: List<ManagerStatistics> = emptyList(),
    val currentSortOption: SortingOption = SortingOption.TOTAL_POINTS,
    val selectedManagerIds: Set<Int> = emptySet(),
    val playerAnalytics: LeaguePlayerAnalytics? = null,
    val selectedGameweek: Int? = null,
    val selectedPosition: String? = null,
    val isLoading: Boolean = false,
    val isLoadingPlayers: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0,
    val differentialAnalyses: List<DifferentialAnalysis> = emptyList(),
    val whatIfScenarios: List<WhatIfScenario> = emptyList(),
    val isLoadingDifferentials: Boolean = false,
    val isLoadingWhatIf: Boolean = false,
    val isFavourite: Boolean = false,
    val isDemo: Boolean = false
)

class LeagueStatsViewModel(application: Application): AndroidViewModel(application) {
    private val repository = FplRepository()
    private val favouriteLeaguesManager = FavouriteLeaguesManager(application)

    private val _uiState = mutableStateOf(LeagueStatsUiState())
    val uiState: State<LeagueStatsUiState> = _uiState

    fun loadLeagueStatistics(leagueId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                leagueId = leagueId,
                isLoading = true,
                error = null
            )

            try {
                val leagueResult = repository.getLeagueStandings(leagueId)
                if (leagueResult.isFailure) {
                    throw Exception("Failed to load league data. Please check if the league ID is correct.")
                }

                val leagueData = leagueResult.getOrNull()!!
                val allManagers = mutableListOf<StandingResult>()
                allManagers.addAll(leagueData.standings.results)

                var currentPage = 1
                while (leagueData.standings.hasNext && currentPage < 10) {
                    currentPage++
                    val nextPageResult = repository.getLeagueStandings(leagueId, currentPage)
                    if (nextPageResult.isSuccess) {
                        allManagers.addAll(nextPageResult.getOrNull()!!.standings.results)
                    }
                }

                val managerStats = allManagers.map { standing ->
                    async {
                        try {
                            loadManagerStatistics(standing.entry)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }.awaitAll().filterNotNull()

                if (managerStats.isEmpty()) {
                    throw Exception("Unable to load manager data for this league.")
                }

                // Calculate league statistics
                val leagueStats = calculateLeagueStatistics(
                    leagueData.league,
                    allManagers,
                    managerStats
                )

                _uiState.value = _uiState.value.copy(
                    leagueStatistics = leagueStats,
                    sortedManagers = sortManagers(
                        managerStats,
                        _uiState.value.currentSortOption
                    ),
                    isFavourite = favouriteLeaguesManager.isFavourite(leagueId),
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "An unknown error occurred while loading league data.",
                    isLoading = false
                )
            }
        }
    }

    fun toggleFavourite() {
        viewModelScope.launch {
            val leagueStats = _uiState.value.leagueStatistics ?: return@launch
            val leagueId = _uiState.value.leagueId

            if (_uiState.value.isFavourite) {
                // Remove from favourites
                favouriteLeaguesManager.removeFavouriteLeague(leagueId)
                _uiState.value = _uiState.value.copy(isFavourite = false)
            } else {
                // Add to favourites
                val favouriteLeague = FavouriteLeague(
                    id = leagueId,
                    name = leagueStats.leagueInfo.name,
                    totalManagers = leagueStats.standings.size,
                    averagePoints = leagueStats.leagueAverages.averagePoints
                )
                val success = favouriteLeaguesManager.addFavouriteLeague(favouriteLeague)
                if (success) {
                    _uiState.value = _uiState.value.copy(isFavourite = true)
                }
            }
        }
    }

    private suspend fun loadManagerStatistics(managerId: Int): ManagerStatistics? {
        return try {
            val managerResult = repository.getManagerInfo(managerId)
            val historyResult = repository.getManagerHistory(managerId)

            if (managerResult.isSuccess && historyResult.isSuccess) {
                val manager = managerResult.getOrNull()!!
                val history = historyResult.getOrNull()!!

                calculateManagerStatistics(manager, history)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateManagerStatistics(
        manager: Manager,
        history: ManagerHistory
    ): ManagerStatistics {
        val gameweeks = history.current
        val points = gameweeks.map { it.points }
        val totalPoints = gameweeks.lastOrNull()?.totalPoints ?: 0
        val averagePoints = if (points.isNotEmpty()) points.average() else 0.0

        val variance = if (points.size > 1) {
            points.map { (it - averagePoints).pow(2) }.average()
        } else 0.0
        val standardDev = sqrt(variance)
        val consistency = if(averagePoints > 0) standardDev / averagePoints else 0.0

        val bestWeek = gameweeks.maxByOrNull { it.points }
        val worstWeek = gameweeks.minByOrNull { it.points }

        val currentStreak = calculateCurrentStreak(gameweeks)
        val longestWinStreak = calculateLongestWinStreak(gameweeks)

        val totalTransfers = gameweeks.sumOf { it.eventTransfers }
        val totalHits = gameweeks.sumOf { it.eventTransfersCost }
        val teamValue = gameweeks.lastOrNull()?.value ?: 0
        val benchPoints = gameweeks.sumOf { it.pointsOnBench }

        val captainPoints = 0

        val rankHistory = gameweeks.map { it.overallRank }
        val pointsHistory = gameweeks.map { it.points }

        val monthlyPoints = gameweeks.chunked(4).mapIndexed { ind, weeks ->
            "Month ${ind + 1}" to weeks.sumOf { it.points }
        }.toMap()

        return ManagerStatistics(
            managerId = manager.id,
            managerName = manager.fullName,
            teamName = manager.name,
            totalPoints = totalPoints,
            averagePoints = averagePoints,
            standardDeviation = standardDev,
            consistency = consistency,
            bestWeek = bestWeek,
            worstWeek = worstWeek,
            currentStreak = currentStreak,
            longestWinStreak = longestWinStreak,
            totalTransfers = totalTransfers,
            totalHits = totalHits,
            teamValue = teamValue,
            benchPoints = benchPoints,
            captainPoints = captainPoints,
            chipsUsed = history.chips,
            rankHistory = rankHistory,
            pointsHistory = pointsHistory,
            monthlyPoints = monthlyPoints,
            headToHeadRecord = emptyMap()
        )
    }

    private fun calculateCurrentStreak(gameweeks: List<GameweekHistory>): Int{
        if (gameweeks.size < 2) return 0

        var streak = 0
        val reversed = gameweeks.reversed()

        for (i in 1 until reversed.size) {
            val current = reversed[i]
            val previous = reversed[i - 1]

            if (current.rank < previous.rank) {
                if (streak <= 0) streak = 1
                else streak++
            } else if (current.rank > previous.rank) {
                if (streak >= 0) streak = -1
                else streak--
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateLongestWinStreak(gameweeks: List<GameweekHistory>): Int {
        if (gameweeks.size < 2) return 0

        var maxStreak = 0
        var streak = 0

        for (i in 1 until gameweeks.size) {
            if (gameweeks[i].rank < gameweeks[i - 1].rank) {
                streak++
                maxStreak = maxOf(maxStreak, streak)
            } else {
                streak = 0
            }
        }

        return maxStreak
    }

    private fun calculateLeagueStatistics(
        league: League,
        standings: List<StandingResult>,
        managerStats: List<ManagerStatistics>
    ): LeagueStatistics {
        val averages = LeagueAverages(
            averagePoints = managerStats.map { it.averagePoints }.average(),
            averageTransfers = managerStats.map { it.totalTransfers.toDouble() }.average(),
            averageHits = managerStats.map { it.totalHits.toDouble() }.average(),
            averageTeamValue = managerStats.map { it.teamValue.toDouble() }.average(),
            averageBenchPoints = managerStats.map { it.benchPoints.toDouble() }.average(),
            topPerformers = calculateTopPerformers(managerStats),
            consistency = calculateConsistencyMetrics(managerStats)
        )

        val chipAnalysis = analyzeChips(managerStats)
        val weeklyStats = calculateWeeklyStats(managerStats)
        val statsMap = managerStats.associateBy { it.managerId }

        return LeagueStatistics(
            leagueInfo = league,
            standings = standings,
            managerStats = statsMap,
            leagueAverages = averages,
            chipAnalysis = chipAnalysis,
            weeklyStats = weeklyStats
        )
    }

    private fun calculateTopPerformers(managers: List<ManagerStatistics>): List<PerformanceMetric> {
        val metrics = mutableListOf<PerformanceMetric>()

        managers.maxByOrNull { it.averagePoints }?.let {
            metrics.add(
                PerformanceMetric(
                    it.managerId,
                    it.managerName,
                    "Highest Average",
                    it.averagePoints
                )
            )
        }

        managers.minByOrNull { it.consistency }?.let {
            metrics.add(
                PerformanceMetric(
                    it.managerId,
                    it.managerName,
                    "Most Consistent",
                    it.consistency
                )
            )
        }

        managers.maxByOrNull { it.bestWeek?.points ?: 0 }?.let {
            metrics.add(
                PerformanceMetric(
                    it.managerId,
                    it.managerName,
                    "Best Week",
                    it.bestWeek?.points?.toDouble() ?: 0.0
                )
            )
        }

        return metrics
    }

    private fun calculateConsistencyMetrics(managers: List<ManagerStatistics>): List<ConsistencyMetric> {
        return managers.map { manager ->
            ConsistencyMetric(
                managerId = manager.managerId,
                managerName = manager.managerName,
                consistency = manager.consistency,
                averageDeviation = manager.standardDeviation
            )
        }.sortedBy { it.consistency }
    }

    private fun analyzeChips(managers: List<ManagerStatistics>): ChipAnalysis {
        val allChips = managers.flatMap { manager ->
            manager.chipsUsed.map { chip ->
                ChipPerformance(
                    managerId = manager.managerId,
                    managerName = manager.managerName,
                    chipName = chip.name,
                    gameweek = chip.event,
                    points = manager.pointsHistory.getOrNull(chip.event - 1) ?: 0,
                    pointsGained = 0,
                    rank = 0
                )
            }
        }

        val wildcards = allChips.filter { it.chipName == "wildcard" }.sortedByDescending { it.points }
        val benchBoosts = allChips.filter { it.chipName == "bboost" }.sortedByDescending { it.points }
        val tripleCaptains = allChips.filter { it.chipName == "3xc" }.sortedByDescending { it.points }
        val freeHits = allChips.filter { it.chipName == "freehit" }.sortedByDescending { it.points }

        return ChipAnalysis(
            bestWildcards = wildcards.take(5),
            bestBenchBoosts = benchBoosts.take(5),
            bestTripleCaptains = tripleCaptains.take(5),
            bestFreeHits = freeHits.take(5),
            worstChips = allChips.sortedBy { it.points }.take(5),
            unusedChips = emptyMap()
        )
    }

    private fun calculateWeeklyStats(managers: List<ManagerStatistics>): List<WeeklyLeagueStats> {
        val maxGameweeks = managers.maxOfOrNull { it.pointsHistory.size } ?: 0

        return (0 until maxGameweeks).map { gwIndex ->
            val weekPoints = managers.mapNotNull { it.pointsHistory.getOrNull(gwIndex) }
            val average = if (weekPoints.isNotEmpty()) weekPoints.average() else 0.0
            val highest = weekPoints.maxOrNull() ?: 0
            val lowest = weekPoints.minOrNull() ?: 0

            WeeklyLeagueStats(
                gameweek = gwIndex + 1,
                averagePoints = average,
                highestScore = highest,
                highestScorer = managers.find { it.pointsHistory.getOrNull(gwIndex) == highest }?.managerName ?: "",
                lowestScore = lowest,
                lowestScorer = managers.find { it.pointsHistory.getOrNull(gwIndex) == lowest }?.managerName ?: "",
                mostCaptained = "",
                transfersMade = 0
            )
        }
    }

    fun setSortOption(sort: SortingOption) {
        _uiState.value =  _uiState.value.copy(
            currentSortOption = sort,
            sortedManagers = sortManagers(
                _uiState.value.leagueStatistics?.managerStats?.values?.toList() ?: emptyList(),
                sort
            )
        )
    }

    private fun sortManagers(managers: List<ManagerStatistics>, option: SortingOption): List<ManagerStatistics> {
        return when (option) {
            SortingOption.TOTAL_POINTS -> managers.sortedByDescending { it.totalPoints }
            SortingOption.AVERAGE_POINTS -> managers.sortedByDescending { it.averagePoints }
            SortingOption.CONSISTENCY -> managers.sortedBy { it.consistency }
            SortingOption.BEST_WEEK -> managers.sortedByDescending { it.bestWeek?.points ?: 0 }
            SortingOption.WORST_WEEK -> managers.sortedByDescending { it.worstWeek?.points ?: 0 }
            SortingOption.FORM -> managers.sortedByDescending { it.currentStreak }
            SortingOption.TRANSFERS -> managers.sortedByDescending { it.totalTransfers }
            SortingOption.TEAM_VALUE -> managers.sortedByDescending { it.teamValue }
            SortingOption.BENCH_POINTS -> managers.sortedByDescending { it.benchPoints }
            SortingOption.CAPTAIN_POINTS -> managers.sortedByDescending { it.captainPoints }
        }
    }

    fun toggleManagerSelection(managerId: Int) {
        val current = _uiState.value.selectedManagerIds
        _uiState.value = _uiState.value.copy(
            selectedManagerIds = if (current.contains(managerId)) {
                current - managerId
            } else {
                if (current.size < 5) current + managerId else current
            }
        )
    }

    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)

        when (tab) {
            4 -> { // Players tab
                if (_uiState.value.playerAnalytics == null && !_uiState.value.isLoadingPlayers) {
                    loadPlayerAnalytics()
                }
            }
            5 -> { // Differentials tab
                if (_uiState.value.differentialAnalyses.isEmpty() && !_uiState.value.isLoadingDifferentials) {
                    loadDifferentialAnalyses()
                }
            }
            6 -> { // What-If tab
                if (_uiState.value.whatIfScenarios.isEmpty() && !_uiState.value.isLoadingWhatIf) {
                    loadWhatIfScenarios()
                }
            }
        }
    }

    fun loadPlayerAnalytics(gameweek: Int? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPlayers = true)

            try{
                val currentGW = gameweek ?: getCurrentGameweek()
                val leagueStats = _uiState.value.leagueStatistics

                if(leagueStats == null){
                    return@launch
                }

                val managerTeams = leagueStats.standings.map { standing ->
                    async {
                        try {
                            val teamResult = repository.getManagerTeam(standing.entry, currentGW)
                            if(teamResult.isSuccess) {
                                standing.entry to teamResult.getOrNull()!!
                            } else null
                        } catch (e: Exception){
                            null
                        }
                    }
                }.awaitAll().filterNotNull().toMap()

                val bootstrapRes = repository.getBootstrapStatic()
                if (bootstrapRes.isFailure) return@launch

                val bootstrap = bootstrapRes.getOrNull()!!
                val playerMap = bootstrap.elements.associateBy { it.id }
                val teamMap = bootstrap.teams.associateBy { it.id }
                val positionMap = bootstrap.elementTypes.associateBy { it.id }

                val playerOwnershipMap = mutableMapOf<Int, MutableList<OwnershipInfo>>()
                val captainMap = mutableMapOf<Int, MutableList<CaptainInfo>>()

                managerTeams.forEach { (managerId, team) ->
                    team.picks.forEach { pick ->
                        playerOwnershipMap.getOrPut(pick.element) { mutableListOf() }
                            .add(OwnershipInfo(managerId, pick.position <= 11))

                        if (pick.isCaptain) {
                            captainMap.getOrPut(pick.element) { mutableListOf() }
                                .add(CaptainInfo(managerId, true))
                        } else if (pick.isViceCaptain){
                            captainMap.getOrPut(pick.element) { mutableListOf() }
                                .add(CaptainInfo(managerId, false))
                        }
                    }
                }

                val totalManagers = managerTeams.size.toDouble()

                val playerOwnership = playerOwnershipMap.map { (playerId, owners) ->
                    val player = playerMap[playerId]!!
                    val team = teamMap[player.team]!!
                    val position = positionMap[player.elementType]!!
                    val ownershipPct = (owners.size / totalManagers) * 100
                    val captainCount = captainMap[playerId]?.count { it.isCaptain } ?: 0
                    val effectiveOwnership = ownershipPct + (captainCount / totalManagers * 100)

                    PlayerOwnership(
                        playerId = playerId,
                        playerName = player.webName,
                        teamName = team.shortName,
                        position = position.singularNameShort,
                        ownershipCount = owners.size,
                        ownershipPercentage = ownershipPct,
                        effectiveOwnership = effectiveOwnership,
                        price = player.nowCost / 10.0,
                        points = player.eventPoints,
                        isTemplate = ownershipPct > 50,
                        isDifferential = ownershipPct < 10 && player.eventPoints > 6
                    )
                }.sortedByDescending { it.ownershipPercentage }

                val captaincyData = captainMap.map { (playerId, captains) ->
                    val player = playerMap[playerId]!!
                    val captainCount = captains.count { it.isCaptain }
                    val viceCaptainCount = captains.count { !it.isCaptain }

                    CaptaincyData(
                        playerId = playerId,
                        playerName = player.webName,
                        captainCount = captainCount,
                        captainPercentage = (captainCount / totalManagers) * 100,
                        viceCaptainCount = viceCaptainCount,
                        totalPointsEarned = player.eventPoints * (captainCount * 2),
                        averageReturn = player.eventPoints.toDouble(),
                        successRate = if (player.eventPoints > 6) 100.0 else 0.0
                    )
                }.filter { it.captainCount > 0 }.sortedByDescending { it.captainCount }

                val differentials = playerOwnership
                    .filter { it.isDifferential }
                    .map { ownership ->
                        DifferentialPick(
                            playerId = ownership.playerId,
                            playerName = ownership.playerName,
                            teamName = ownership.teamName,
                            ownershipPercentage = ownership.ownershipPercentage,
                            points = ownership.points,
                            pointsPerMillion = ownership.points / ownership.price,
                            managers = playerOwnershipMap[ownership.playerId]
                                ?.mapNotNull { ownershipInfo ->
                                    leagueStats.managerStats[ownershipInfo.managerId]?.managerName
                                } ?: emptyList(),
                            differentialScore = ownership.points * (1 - ownership.ownershipPercentage / 100)
                        )
                    }.sortedByDescending { it.differentialScore }

                val templateTeam = playerOwnership
                    .filter { it.isTemplate }
                    .map { it.playerId }

                val playerAnalytics = LeaguePlayerAnalytics(
                    leagueId = _uiState.value.leagueId,
                    gameweek = currentGW,
                    playerOwnership = playerOwnership,
                    captaincy = captaincyData,
                    differentials = differentials,
                    templateTeam = templateTeam,
                    transferTrends = TransferTrends(
                        mostTransferredIn = emptyList(),
                        mostTransferredOut = emptyList(),
                        priceRisers = emptyList(),
                        priceFallers = emptyList(),
                        bandwagons = emptyList(),
                        kneejerk = emptyList()
                    ),
                    playerPerformance = emptyList(),
                    benchAnalysis = BenchAnalysis(
                        totalBenchPoints = 0,
                        averageBenchPoints = 0.0,
                        mostBenchedPlayers = emptyList(),
                        costliestBenching = emptyList(),
                        benchBoostSuccess = emptyList()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    playerAnalytics = playerAnalytics,
                    selectedGameweek = currentGW,
                    isLoadingPlayers = false
                )

            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load player analytics: ${e.message}",
                    isLoadingPlayers = false
                )
            }
        }
    }

    private suspend fun getCurrentGameweek(): Int {
        val bootstrapResult = repository.getBootstrapStatic()
        return if (bootstrapResult.isSuccess) {
            bootstrapResult.getOrNull()?.events?.find { it.isCurrent }?.id ?: 1
        } else {
            1
        }
    }

    fun setSelectedGameweek(gameweek: Int) {
        _uiState.value = _uiState.value.copy(selectedGameweek = gameweek)
        loadPlayerAnalytics(gameweek)
    }

    fun setSelectedPosition(position: String?) {
        _uiState.value = _uiState.value.copy(selectedPosition = position)
    }

    fun loadDifferentialAnalyses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDifferentials = true)

            try {
                val currentGW = getCurrentGameweek()
                val analysesResult = repository.getDifferentialAnalysis(_uiState.value.leagueId, currentGW)

                if (analysesResult.isSuccess) {
                    val analyses = analysesResult.getOrNull() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        differentialAnalyses = analyses,
                        isLoadingDifferentials = false
                    )
                } else {
                    throw analysesResult.exceptionOrNull() ?: Exception("Failed to load differential analysis")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load differential analysis: ${e.message}",
                    isLoadingDifferentials = false
                )
            }
        }
    }

    fun loadWhatIfScenarios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingWhatIf = true)

            try {
                val currentGW = getCurrentGameweek()
                val scenariosResult = repository.getWhatIfScenarios(_uiState.value.leagueId, currentGW)

                if (scenariosResult.isSuccess) {
                    val scenarios = scenariosResult.getOrNull() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        whatIfScenarios = scenarios,
                        isLoadingWhatIf = false
                    )
                } else {
                    throw scenariosResult.exceptionOrNull() ?: Exception("Failed to load what-if scenarios")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load what-if scenarios: ${e.message}",
                    isLoadingWhatIf = false
                )
            }
        }
    }

    fun refreshDifferentialAnalyses() {
        _uiState.value = _uiState.value.copy(differentialAnalyses = emptyList())
        loadDifferentialAnalyses()
    }

    fun refreshWhatIfScenarios() {
        _uiState.value = _uiState.value.copy(whatIfScenarios = emptyList())
        loadWhatIfScenarios()
    }

    fun loadDemoData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                delay(1500)

                val demoStats = MockDataFactory.createMockLeagueStatistics()

                _uiState.value = _uiState.value.copy(
                    leagueStatistics = demoStats,
                    sortedManagers = sortManagers(
                        demoStats.managerStats.values.toList(),
                        _uiState.value.currentSortOption
                    ),
                    isFavourite = false,
                    isLoading = false
                )

                loadDemoPlayerAnalytics()
                loadDemoDifferentialAnalyses()
                loadDemoWhatIfScenarios()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load demo data: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private suspend fun loadDemoPlayerAnalytics() {
        _uiState.value = _uiState.value.copy(isLoadingPlayers = true)

        try {
            delay(500)
            val demoPlayerAnalytics = MockDataFactory.createMockPlayerAnalytics()

            _uiState.value = _uiState.value.copy(
                playerAnalytics = demoPlayerAnalytics,
                selectedGameweek = 15,
                isLoadingPlayers = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoadingPlayers = false
            )
        }
    }

    private suspend fun loadDemoDifferentialAnalyses() {
        _uiState.value = _uiState.value.copy(isLoadingDifferentials = true)

        try {
            delay(800)
            val demoAnalyses = MockDataFactory.createMockDifferentialAnalyses()

            _uiState.value = _uiState.value.copy(
                differentialAnalyses = demoAnalyses,
                isLoadingDifferentials = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoadingDifferentials = false
            )
        }
    }

    private suspend fun loadDemoWhatIfScenarios() {
        _uiState.value = _uiState.value.copy(isLoadingWhatIf = true)

        try {
            delay(600)
            val demoScenarios = MockDataFactory.createMockWhatIfScenarios()

            _uiState.value = _uiState.value.copy(
                whatIfScenarios = demoScenarios,
                isLoadingWhatIf = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoadingWhatIf = false
            )
        }
    }
}

private data class OwnershipInfo(
    val managerId: Int,
    val isStarting: Boolean
)

private data class CaptainInfo(
    val managerId: Int,
    val isCaptain: Boolean
)