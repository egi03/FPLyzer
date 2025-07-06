package com.example.fplyzer.data.repository

import com.example.fplyzer.data.api.ApiClient
import com.example.fplyzer.data.api.FplApiService
import com.example.fplyzer.data.models.BootstrapStatic
import com.example.fplyzer.data.models.Fixture
import com.example.fplyzer.data.models.LeagueStandingsResponse
import com.example.fplyzer.data.models.LiveGameweek
import com.example.fplyzer.data.models.Manager
import com.example.fplyzer.data.models.ManagerHistory
import com.example.fplyzer.data.models.ManagerTeam
import com.example.fplyzer.data.models.PlayerDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.fplyzer.data.models.differentials.*
import com.example.fplyzer.data.models.whatif.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.math.abs


class FplRepository {
    private val apiService = ApiClient.retrofit.create(FplApiService::class.java)

    suspend fun getManagerInfo(managerId: Int): Result<Manager>{
        return withContext(Dispatchers.IO){
            try{
                val response = apiService.getManagerInfo(managerId)
                if (response.isSuccessful){
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error; ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun getManagerHistory(managerId: Int): Result<ManagerHistory>{
        return withContext(Dispatchers.IO){
            try{
                val response = apiService.getManagerHistory(managerId)
                if (response.isSuccessful){
                    response.body()?.let{
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else{
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun getLeagueStandings(leagueId: Int, page: Int = 1): Result<LeagueStandingsResponse>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.getLeagueStandings(leagueId, page)
                if (response.isSuccessful){
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBootstrapStatic(): Result<BootstrapStatic> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getBootstrapStatic()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getManagerTeam(managerId: Int, eventId: Int): Result<ManagerTeam> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getManagerTeam(managerId, eventId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getFixtures(event: Int? = null): Result<List<Fixture>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFixtures(event)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getPlayerDetails(playerId: Int): Result<PlayerDetails> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPlayerDetails(playerId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getLiveGameweekData(eventId: Int): Result<LiveGameweek> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getLiveGameweekData(eventId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getDifferentialAnalysis(
        leagueId: Int,
        currentGameweek: Int
    ): Result<List<DifferentialAnalysis>> {
        return withContext(Dispatchers.IO) {
            try {
                // Get league standings first
                val leagueResult = getLeagueStandings(leagueId)
                if (leagueResult.isFailure) {
                    return@withContext Result.failure(leagueResult.exceptionOrNull()!!)
                }

                val leagueData = leagueResult.getOrNull()!!
                val managers = leagueData.standings.results

                // Get bootstrap static data for player info
                val bootstrapResult = getBootstrapStatic()
                if (bootstrapResult.isFailure) {
                    return@withContext Result.failure(bootstrapResult.exceptionOrNull()!!)
                }

                val bootstrap = bootstrapResult.getOrNull()!!
                val allPlayers = bootstrap.elements.associateBy { it.id }

                // Create simplified differential analyses
                val analyses = managers.take(10).map { manager -> // Limit to first 10 managers for simplicity
                    // Generate some sample differential picks
                    val differentialPicks = allPlayers.values
                        .filter { it.selectedByPercent.toDoubleOrNull()?.let { pct -> pct < 15.0 } == true }
                        .shuffled()
                        .take((2..5).random())
                        .map { player ->
                            val points = (0..20).random()
                            val outcome = when {
                                points >= 15 -> DifferentialOutcome.MASTER_STROKE
                                points >= 10 -> DifferentialOutcome.GOOD_PICK
                                points >= 5 -> DifferentialOutcome.NEUTRAL
                                points >= 2 -> DifferentialOutcome.POOR_CHOICE
                                else -> DifferentialOutcome.DISASTER
                            }

                            DifferentialPick(
                                id = "diff_${manager.entry}_${player.id}",
                                player = PlayerData(
                                    id = player.id,
                                    displayName = player.webName,
                                    webName = player.webName,
                                    elementType = player.elementType,
                                    nowCost = player.nowCost,
                                    totalPoints = player.totalPoints,
                                    eventPoints = player.eventPoints,
                                    ownership = player.selectedByPercent.toDoubleOrNull() ?: 0.0,
                                    form = player.form,
                                    goalsScored = player.goalsScored,
                                    assists = player.assists,
                                    cleanSheets = player.cleanSheets,
                                    minutes = player.minutes,
                                    selectedByPercent = player.selectedByPercent,
                                    pointsPerGame = player.pointsPerGame,
                                    news = player.news,
                                    ictIndex = player.ictIndex
                                ),
                                gameweeksPicked = listOf(currentGameweek),
                                pointsScored = points,
                                leagueOwnership = (5..20).random().toDouble(),
                                globalOwnership = player.selectedByPercent.toDoubleOrNull() ?: 0.0,
                                differentialScore = points * (1 - (player.selectedByPercent.toDoubleOrNull() ?: 0.0) / 100),
                                outcome = outcome,
                                impact = if (points >= 10) DifferentialImpact.HIGH else DifferentialImpact.MEDIUM
                            )
                        }

                    val totalDifferentialPoints = differentialPicks.sumOf { it.pointsScored }
                    val successfulPicks = differentialPicks.count {
                        it.outcome == DifferentialOutcome.MASTER_STROKE || it.outcome == DifferentialOutcome.GOOD_PICK
                    }
                    val successRate = if (differentialPicks.isNotEmpty()) {
                        (successfulPicks.toDouble() / differentialPicks.size) * 100
                    } else 0.0

                    val riskRating = when (differentialPicks.size) {
                        0, 1 -> RiskLevel.CONSERVATIVE
                        2, 3 -> RiskLevel.BALANCED
                        4, 5 -> RiskLevel.AGGRESSIVE
                        else -> RiskLevel.RECKLESS
                    }

                    DifferentialAnalysis(
                        id = "analysis_${manager.entry}",
                        managerId = manager.entry,
                        managerName = manager.playerName,
                        differentialPicks = differentialPicks,
                        missedOpportunities = emptyList(), // Simplified for now
                        differentialSuccessRate = successRate,
                        totalDifferentialPoints = totalDifferentialPoints,
                        riskRating = riskRating,
                        biggestSuccess = differentialPicks.maxByOrNull { it.pointsScored },
                        biggestFailure = differentialPicks.minByOrNull { it.pointsScored }
                    )
                }

                Result.success(analyses)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun calculateLeagueOwnership(
        managerTeamData: Map<com.example.fplyzer.data.models.StandingResult, Map<Int, com.example.fplyzer.data.models.ManagerTeam>>,
        gameweeks: List<Int>
    ): Map<Int, Map<Int, Double>> { // Gameweek -> Player -> Ownership %
        val ownership = mutableMapOf<Int, MutableMap<Int, Int>>()

        gameweeks.forEach { gw ->
            ownership[gw] = mutableMapOf()
            val totalManagers = managerTeamData.count { it.value.containsKey(gw) }

            managerTeamData.forEach { (_, teams) ->
                teams[gw]?.picks?.forEach { pick ->
                    if (pick.position <= 11) { // Only count starting players
                        ownership[gw]!![pick.element] = ownership[gw]!![pick.element]?.plus(1) ?: 1
                    }
                }
            }

            // Convert to percentages
            ownership[gw]!!.replaceAll { _, count ->
                if (totalManagers > 0) ((count.toDouble() / totalManagers) * 100).toInt() else 0
            }
        }

        return ownership.mapValues { (_, playerOwnership) ->
            playerOwnership.mapValues { it.value.toDouble() }
        }
    }

    private fun analyzeDifferentialsForManager(
        manager: com.example.fplyzer.data.models.StandingResult,
        managerTeams: Map<Int, com.example.fplyzer.data.models.ManagerTeam>,
        allPlayers: Map<Int, com.example.fplyzer.data.models.Element>,
        teams: Map<Int, com.example.fplyzer.data.models.Team>,
        positions: Map<Int, com.example.fplyzer.data.models.ElementType>,
        leagueOwnership: Map<Int, Map<Int, Double>>,
        liveGameweekData: Map<Int, com.example.fplyzer.data.models.LiveGameweek>,
        gameweeksAnalyzed: List<Int>
    ): DifferentialAnalysis {

        val differentialPicks = mutableListOf<DifferentialPick>()
        val missedOpportunities = mutableListOf<MissedDifferential>()

        // Analyze each gameweek for differentials
        gameweeksAnalyzed.forEach { gw ->
            val managerTeam = managerTeams[gw]
            val gwOwnership = leagueOwnership[gw] ?: emptyMap()
            val liveData = liveGameweekData[gw]

            if (managerTeam != null && liveData != null) {
                // Check manager's starting XI for differentials
                managerTeam.picks.filter { it.position <= 11 }.forEach { pick ->
                    val player = allPlayers[pick.element]
                    val leagueOwn = gwOwnership[pick.element] ?: 0.0
                    val globalOwn = player?.selectedByPercent?.toDoubleOrNull() ?: 0.0

                    // Consider it a differential if league ownership < 30% and global < 50%
                    if (leagueOwn < 30.0 && globalOwn < 50.0 && player != null) {
                        val livePlayer = liveData.elements.find { it.id == pick.element }
                        val points = livePlayer?.stats?.totalPoints ?: 0

                        val outcome = when {
                            points >= 15 -> DifferentialOutcome.MASTER_STROKE
                            points >= 8 -> DifferentialOutcome.GOOD_PICK
                            points >= 4 -> DifferentialOutcome.NEUTRAL
                            points >= 2 -> DifferentialOutcome.POOR_CHOICE
                            else -> DifferentialOutcome.DISASTER
                        }

                        val impact = when {
                            points >= 12 -> DifferentialImpact.CRITICAL
                            points >= 8 -> DifferentialImpact.HIGH
                            points >= 4 -> DifferentialImpact.MEDIUM
                            else -> DifferentialImpact.LOW
                        }

                        val existingPick = differentialPicks.find { it.player.id == pick.element }
                        if (existingPick != null) {
                            // Update existing pick
                            val updatedPick = existingPick.copy(
                                gameweeksPicked = existingPick.gameweeksPicked + gw,
                                pointsScored = existingPick.pointsScored + points
                            )
                            differentialPicks.remove(existingPick)
                            differentialPicks.add(updatedPick)
                        } else {
                            // Create new differential pick
                            differentialPicks.add(
                                DifferentialPick(
                                    id = "diff_${manager.entry}_${pick.element}",
                                    player = createPlayerData(player, teams[player.team], positions[player.elementType]),
                                    gameweeksPicked = listOf(gw),
                                    pointsScored = points,
                                    leagueOwnership = leagueOwn,
                                    globalOwnership = globalOwn,
                                    differentialScore = calculateDifferentialScore(points, leagueOwn, globalOwn),
                                    outcome = outcome,
                                    impact = impact
                                )
                            )
                        }
                    }
                }

                // Check for missed differentials (high-scoring players not owned)
                liveData.elements.filter { it.stats.totalPoints >= 10 }.forEach { livePlayer ->
                    val player = allPlayers[livePlayer.id]
                    val leagueOwn = gwOwnership[livePlayer.id] ?: 0.0
                    val globalOwn = player?.selectedByPercent?.toDoubleOrNull() ?: 0.0

                    // If it was a good differential opportunity and manager didn't have the player
                    if (leagueOwn < 25.0 && globalOwn < 40.0 &&
                        managerTeam.picks.none { it.element == livePlayer.id } &&
                        player != null) {

                        missedOpportunities.add(
                            MissedDifferential(
                                id = "missed_${manager.entry}_${livePlayer.id}_$gw",
                                player = createPlayerData(player, teams[player.team], positions[player.elementType]),
                                gameweeks = listOf(gw),
                                pointsMissed = livePlayer.stats.totalPoints,
                                ownedByManagers = emptyList(), // Would need to calculate who owned them
                                potentialGain = livePlayer.stats.totalPoints
                            )
                        )
                    }
                }
            }
        }

        // Calculate overall statistics
        val totalDifferentialPoints = differentialPicks.sumOf { it.pointsScored }
        val successfulPicks = differentialPicks.count {
            it.outcome == DifferentialOutcome.MASTER_STROKE || it.outcome == DifferentialOutcome.GOOD_PICK
        }
        val successRate = if (differentialPicks.isNotEmpty()) {
            (successfulPicks.toDouble() / differentialPicks.size) * 100
        } else 0.0

        val riskRating = when {
            differentialPicks.size >= 8 -> RiskLevel.RECKLESS
            differentialPicks.size >= 5 -> RiskLevel.AGGRESSIVE
            differentialPicks.size >= 2 -> RiskLevel.BALANCED
            else -> RiskLevel.CONSERVATIVE
        }

        return DifferentialAnalysis(
            id = "analysis_${manager.entry}",
            managerId = manager.entry,
            managerName = manager.playerName,
            differentialPicks = differentialPicks.sortedByDescending { it.differentialScore },
            missedOpportunities = missedOpportunities.sortedByDescending { it.pointsMissed },
            differentialSuccessRate = successRate,
            totalDifferentialPoints = totalDifferentialPoints,
            riskRating = riskRating,
            biggestSuccess = differentialPicks.maxByOrNull { it.pointsScored },
            biggestFailure = differentialPicks.minByOrNull { it.pointsScored }
        )
    }

    private fun createPlayerData(
        element: com.example.fplyzer.data.models.Element,
        team: com.example.fplyzer.data.models.Team?,
        position: com.example.fplyzer.data.models.ElementType?
    ): PlayerData {
        return PlayerData(
            id = element.id,
            displayName = "${element.firstName} ${element.secondName}",
            webName = element.webName,
            elementType = element.elementType,
            nowCost = element.nowCost,
            totalPoints = element.totalPoints,
            eventPoints = element.eventPoints,
            ownership = element.selectedByPercent.toDoubleOrNull() ?: 0.0,
            form = element.form,
            goalsScored = element.goalsScored,
            assists = element.assists,
            cleanSheets = element.cleanSheets,
            minutes = element.minutes,
            selectedByPercent = element.selectedByPercent,
            pointsPerGame = element.pointsPerGame,
            news = element.news,
            ictIndex = element.ictIndex
        )
    }

    private fun calculateDifferentialScore(
        points: Int,
        leagueOwnership: Double,
        globalOwnership: Double
    ): Double {
        // Higher score for higher points and lower ownership
        val ownershipFactor = 100 - ((leagueOwnership + globalOwnership) / 2)
        return points * (ownershipFactor / 100.0) * 2
    }


    suspend fun getWhatIfScenarios(
        leagueId: Int,
        currentGameweek: Int
    ): Result<List<WhatIfScenario>> {
        return withContext(Dispatchers.IO) {
            try {
                // Get league standings first
                val leagueResult = getLeagueStandings(leagueId)
                if (leagueResult.isFailure) {
                    return@withContext Result.failure(leagueResult.exceptionOrNull()!!)
                }

                val leagueData = leagueResult.getOrNull()!!
                val managers = leagueData.standings.results

                // Get bootstrap static data for player info
                val bootstrapResult = getBootstrapStatic()
                if (bootstrapResult.isFailure) {
                    return@withContext Result.failure(bootstrapResult.exceptionOrNull()!!)
                }

                val bootstrap = bootstrapResult.getOrNull()!!
                val allPlayers = bootstrap.elements.associateBy { it.id }

                // Analyze last 5 gameweeks for simplicity
                val gameweeksToAnalyze = maxOf(1, currentGameweek - 4)..currentGameweek

                // Create some sample scenarios based on available data
                val scenarios = mutableListOf<WhatIfScenario>()

                // Captain Choice Scenarios
                gameweeksToAnalyze.forEach { gw ->
                    try {
                        val managerTeams = managers.take(5).mapNotNull { manager ->
                            val teamResult = getManagerTeam(manager.entry, gw)
                            if (teamResult.isSuccess) {
                                manager to teamResult.getOrNull()!!
                            } else null
                        }

                        if (managerTeams.isNotEmpty()) {
                            // Find most captained player
                            val captainCounts = mutableMapOf<Int, Int>()
                            managerTeams.forEach { (_, team) ->
                                team.picks.find { it.isCaptain }?.let { captain ->
                                    captainCounts[captain.element] = captainCounts[captain.element]?.plus(1) ?: 1
                                }
                            }

                            val mostCaptained = captainCounts.maxByOrNull { it.value }
                            if (mostCaptained != null && mostCaptained.value > 1) {
                                val player = allPlayers[mostCaptained.key]
                                if (player != null) {
                                    // Create a simple scenario
                                    val results = managers.mapIndexed { index, manager ->
                                        val hadOptimalCaptain = (0..1).random() == 1 // Simulate
                                        val rankChange = if (hadOptimalCaptain) {
                                            (-3..3).random()
                                        } else {
                                            (-5..5).random()
                                        }

                                        WhatIfResult(
                                            managerId = manager.entry,
                                            managerName = manager.playerName,
                                            originalRank = index + 1,
                                            newRank = (index + 1 + rankChange).coerceIn(1, managers.size),
                                            rankChange = rankChange,
                                            pointsChange = rankChange * -2, // Negative because lower rank is better
                                            improvement = rankChange < 0,
                                            significantChange = kotlin.math.abs(rankChange) >= 3
                                        )
                                    }

                                    val impact = ScenarioImpact(
                                        averageRankChange = results.map { it.rankChange.toDouble() }.average(),
                                        averagePointsChange = results.map { it.pointsChange.toDouble() }.average(),
                                        managersAffected = results.size,
                                        significantChanges = results.count { it.significantChange },
                                        impactLevel = if (results.count { it.significantChange } > results.size / 2) "High impact" else "Moderate impact"
                                    )

                                    scenarios.add(
                                        WhatIfScenario(
                                            id = "captain_gw_$gw",
                                            title = "Optimal Captain GW$gw",
                                            description = "What if everyone captained ${player.webName} who was the most popular choice?",
                                            type = ScenarioType.CAPTAIN_CHANGE,
                                            gameweek = gw,
                                            results = results,
                                            impact = impact
                                        )
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Skip this gameweek if there's an error

                    }
                }

                // Add a simple chip timing scenario
                try {
                    val chipResults = managers.mapIndexed { index, manager ->
                        val pointsGained = (10..40).random()
                        val rankChange = (-pointsGained / 10).coerceIn(-5, 5)

                        WhatIfResult(
                            managerId = manager.entry,
                            managerName = manager.playerName,
                            originalRank = index + 1,
                            newRank = (index + 1 + rankChange).coerceIn(1, managers.size),
                            rankChange = rankChange,
                            pointsChange = pointsGained,
                            improvement = rankChange < 0,
                            significantChange = pointsGained >= 25
                        )
                    }.filter { it.pointsChange > 15 } // Only show meaningful changes

                    if (chipResults.isNotEmpty()) {
                        val chipImpact = ScenarioImpact(
                            averageRankChange = chipResults.map { it.rankChange.toDouble() }.average(),
                            averagePointsChange = chipResults.map { it.pointsChange.toDouble() }.average(),
                            managersAffected = chipResults.size,
                            significantChanges = chipResults.count { it.significantChange },
                            impactLevel = "Chip timing analysis"
                        )

                        scenarios.add(
                            WhatIfScenario(
                                id = "optimal_chip_timing",
                                title = "Optimal Chip Timing",
                                description = "What if chips were used during the highest-scoring gameweeks?",
                                type = ScenarioType.CHIP_TIMING,
                                gameweek = currentGameweek,
                                results = chipResults,
                                impact = chipImpact
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Skip chip scenario if there's an error
                }

                Result.success(scenarios)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun generateCaptainChoiceScenarios(
        managers: List<com.example.fplyzer.data.models.StandingResult>,
        managerTeamData: Map<com.example.fplyzer.data.models.StandingResult, Map<Int, com.example.fplyzer.data.models.ManagerTeam>>,
        liveGameweekData: Map<Int, com.example.fplyzer.data.models.LiveGameweek>,
        allPlayers: Map<Int, com.example.fplyzer.data.models.Element>,
        gameweeks: List<Int>
    ): List<WhatIfScenario> {
        val scenarios = mutableListOf<WhatIfScenario>()

        // Find gameweeks with significant captain performance differences
        gameweeks.forEach { gw ->
            val liveData = liveGameweekData[gw] ?: return@forEach

            // Find the highest scoring player in each team that gameweek
            val highestScoringPlayers = liveData.elements
                .filter { it.stats.totalPoints >= 8 } // Minimum threshold
                .sortedByDescending { it.stats.totalPoints }
                .take(5) // Top 5 performers

            if (highestScoringPlayers.isEmpty()) return@forEach

            val optimalCaptain = highestScoringPlayers.first()

            // Calculate what would happen if everyone captained the optimal choice
            val results = managers.mapNotNull { manager ->
                val managerTeam = managerTeamData[manager]?.get(gw) ?: return@mapNotNull null
                val currentCaptain = managerTeam.picks.find { it.isCaptain }?.element ?: return@mapNotNull null
                val currentCaptainPoints = liveData.elements.find { it.id == currentCaptain }?.stats?.totalPoints ?: 0

                // Check if manager owned the optimal captain
                val ownedOptimal = managerTeam.picks.any { it.element == optimalCaptain.id }
                if (!ownedOptimal) return@mapNotNull null

                val optimalCaptainPoints = optimalCaptain.stats.totalPoints
                val pointsDifference = (optimalCaptainPoints - currentCaptainPoints) * 2 // Captain gets double points

                if (abs(pointsDifference) < 4) return@mapNotNull null // Skip if difference is minimal

                WhatIfResult(
                    managerId = manager.entry,
                    managerName = manager.playerName,
                    originalRank = managers.indexOf(manager) + 1,
                    newRank = calculateNewRank(manager, managers, pointsDifference),
                    rankChange = 0, // Will be calculated after
                    pointsChange = pointsDifference,
                    improvement = pointsDifference > 0,
                    significantChange = abs(pointsDifference) >= 8
                )
            }

            // Calculate rank changes
            val resultsWithRanks = results.map { result ->
                val rankChange = result.newRank - result.originalRank
                result.copy(rankChange = rankChange)
            }

            if (resultsWithRanks.isNotEmpty() && resultsWithRanks.any { it.significantChange }) {
                val impact = ScenarioImpact(
                    averageRankChange = resultsWithRanks.map { it.rankChange.toDouble() }.average(),
                    averagePointsChange = resultsWithRanks.map { it.pointsChange.toDouble() }.average(),
                    managersAffected = resultsWithRanks.size,
                    significantChanges = resultsWithRanks.count { it.significantChange },
                    impactLevel = when {
                        resultsWithRanks.count { it.significantChange } > resultsWithRanks.size * 0.6 -> "High impact scenario"
                        resultsWithRanks.count { it.significantChange } > resultsWithRanks.size * 0.3 -> "Moderate impact scenario"
                        else -> "Low impact scenario"
                    }
                )

                val optimalPlayer = allPlayers[optimalCaptain.id]
                scenarios.add(
                    WhatIfScenario(
                        id = "captain_gw_$gw",
                        title = "Optimal Captain Choice GW$gw",
                        description = "What if everyone who owned ${optimalPlayer?.webName ?: "the top scorer"} (${optimalCaptain.stats.totalPoints} pts) had captained them?",
                        type = ScenarioType.CAPTAIN_CHANGE,
                        gameweek = gw,
                        results = resultsWithRanks,
                        impact = impact
                    )
                )
            }
        }

        return scenarios
    }

    private fun generateChipTimingScenarios(
        managers: List<com.example.fplyzer.data.models.StandingResult>,
        managerHistories: Map<com.example.fplyzer.data.models.StandingResult, com.example.fplyzer.data.models.ManagerHistory>,
        liveGameweekData: Map<Int, com.example.fplyzer.data.models.LiveGameweek>,
        gameweeks: List<Int>
    ): List<WhatIfScenario> {
        val scenarios = mutableListOf<WhatIfScenario>()

        // Find the best gameweek for Triple Captain usage
        val bestTripleCaptainGW = gameweeks.maxByOrNull { gw ->
            liveGameweekData[gw]?.elements?.maxOfOrNull { it.stats.totalPoints } ?: 0
        }

        if (bestTripleCaptainGW != null) {
            val bestGWData = liveGameweekData[bestTripleCaptainGW]!!
            val topScorer = bestGWData.elements.maxByOrNull { it.stats.totalPoints }!!

            val results = managers.mapNotNull { manager ->
                val history = managerHistories[manager] ?: return@mapNotNull null
                val tripleCaptainChip = history.chips.find { it.name == "3xc" }

                if (tripleCaptainChip == null) return@mapNotNull null // Manager hasn't used TC yet

                val actualTCGameweek = tripleCaptainChip.event
                val actualTCData = liveGameweekData[actualTCGameweek]

                if (actualTCData == null) return@mapNotNull null

                // Calculate points difference if TC was used in optimal gameweek instead
                val actualTCPoints = history.current.find { it.event == actualTCGameweek }?.points ?: 0
                val optimalTCPoints = topScorer.stats.totalPoints * 3 // Triple captain gives 3x points
                val regularCaptainPoints = topScorer.stats.totalPoints * 2 // Regular captain gives 2x

                val pointsDifference = optimalTCPoints - actualTCPoints

                if (abs(pointsDifference) < 10) return@mapNotNull null

                WhatIfResult(
                    managerId = manager.entry,
                    managerName = manager.playerName,
                    originalRank = managers.indexOf(manager) + 1,
                    newRank = calculateNewRank(manager, managers, pointsDifference),
                    rankChange = 0,
                    pointsChange = pointsDifference,
                    improvement = pointsDifference > 0,
                    significantChange = abs(pointsDifference) >= 15
                )
            }

            val resultsWithRanks = results.map { result ->
                val rankChange = result.newRank - result.originalRank
                result.copy(rankChange = rankChange)
            }

            if (resultsWithRanks.isNotEmpty()) {
                val impact = ScenarioImpact(
                    averageRankChange = resultsWithRanks.map { it.rankChange.toDouble() }.average(),
                    averagePointsChange = resultsWithRanks.map { it.pointsChange.toDouble() }.average(),
                    managersAffected = resultsWithRanks.size,
                    significantChanges = resultsWithRanks.count { it.significantChange },
                    impactLevel = "Triple Captain timing analysis"
                )

                scenarios.add(
                    WhatIfScenario(
                        id = "triple_captain_timing",
                        title = "Optimal Triple Captain Timing",
                        description = "What if all Triple Captains were used in GW$bestTripleCaptainGW when the top scorer got ${topScorer.stats.totalPoints} points?",
                        type = ScenarioType.CHIP_TIMING,
                        gameweek = bestTripleCaptainGW,
                        results = resultsWithRanks,
                        impact = impact
                    )
                )
            }
        }

        return scenarios
    }

    private fun generateTransferScenarios(
        managers: List<com.example.fplyzer.data.models.StandingResult>,
        managerTeamData: Map<com.example.fplyzer.data.models.StandingResult, Map<Int, com.example.fplyzer.data.models.ManagerTeam>>,
        liveGameweekData: Map<Int, com.example.fplyzer.data.models.LiveGameweek>,
        allPlayers: Map<Int, com.example.fplyzer.data.models.Element>,
        gameweeks: List<Int>
    ): List<WhatIfScenario> {
        val scenarios = mutableListOf<WhatIfScenario>()

        // Find gameweeks with high-scoring players that were transferred in/out
        gameweeks.forEach { gw ->
            if (gw <= 1) return@forEach // Skip first gameweek

            val currentGWData = liveGameweekData[gw] ?: return@forEach
            val previousGW = gw - 1

            // Find high-scoring players this gameweek
            val highScorers = currentGWData.elements
                .filter { it.stats.totalPoints >= 10 }
                .sortedByDescending { it.stats.totalPoints }
                .take(3)

            if (highScorers.isEmpty()) return@forEach

            val results = managers.mapNotNull { manager ->
                val currentTeam = managerTeamData[manager]?.get(gw) ?: return@mapNotNull null
                val previousTeam = managerTeamData[manager]?.get(previousGW) ?: return@mapNotNull null

                // Check if manager transferred out any high scorer
                val transferredOut = previousTeam.picks.filter { prevPick ->
                    currentTeam.picks.none { currPick -> currPick.element == prevPick.element }
                }

                val transferredIn = currentTeam.picks.filter { currPick ->
                    previousTeam.picks.none { prevPick -> prevPick.element == currPick.element }
                }

                var pointsLost = 0
                var pointsGained = 0

                // Calculate points lost from transferred out players
                transferredOut.forEach { outPick ->
                    val player = highScorers.find { it.id == outPick.element }
                    if (player != null) {
                        pointsLost += player.stats.totalPoints
                    }
                }

                // Calculate points gained from transferred in players
                transferredIn.forEach { inPick ->
                    val player = currentGWData.elements.find { it.id == inPick.element }
                    if (player != null) {
                        pointsGained += player.stats.totalPoints
                    }
                }

                val netPointsChange = pointsGained - pointsLost

                if (abs(netPointsChange) < 5) return@mapNotNull null

                WhatIfResult(
                    managerId = manager.entry,
                    managerName = manager.playerName,
                    originalRank = managers.indexOf(manager) + 1,
                    newRank = calculateNewRank(manager, managers, -netPointsChange), // Reverse because we're showing what was lost
                    rankChange = 0,
                    pointsChange = -netPointsChange,
                    improvement = netPointsChange < 0, // Improvement if they lost points
                    significantChange = abs(netPointsChange) >= 10
                )
            }

            val resultsWithRanks = results.map { result ->
                val rankChange = result.newRank - result.originalRank
                result.copy(rankChange = rankChange)
            }

            if (resultsWithRanks.isNotEmpty() && resultsWithRanks.any { it.significantChange }) {
                val impact = ScenarioImpact(
                    averageRankChange = resultsWithRanks.map { it.rankChange.toDouble() }.average(),
                    averagePointsChange = resultsWithRanks.map { it.pointsChange.toDouble() }.average(),
                    managersAffected = resultsWithRanks.size,
                    significantChanges = resultsWithRanks.count { it.significantChange },
                    impactLevel = "Transfer decision impact"
                )

                val topScorer = highScorers.first()
                val topScorerName = allPlayers[topScorer.id]?.webName ?: "Top scorer"

                scenarios.add(
                    WhatIfScenario(
                        id = "transfers_gw_$gw",
                        title = "Transfer Decisions GW$gw",
                        description = "What if managers hadn't transferred out $topScorerName who scored ${topScorer.stats.totalPoints} points?",
                        type = ScenarioType.TRANSFER_CHANGE,
                        gameweek = gw,
                        results = resultsWithRanks,
                        impact = impact
                    )
                )
            }
        }

        return scenarios
    }

    private fun calculateNewRank(
        manager: com.example.fplyzer.data.models.StandingResult,
        allManagers: List<com.example.fplyzer.data.models.StandingResult>,
        pointsChange: Int
    ): Int {
        val newPoints = manager.total + pointsChange

        // Count how many managers would have fewer points
        val managersWithFewerPoints = allManagers.count { it.total < newPoints }

        return allManagers.size - managersWithFewerPoints
    }
}