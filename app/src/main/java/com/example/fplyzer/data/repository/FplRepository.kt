package com.example.fplyzer.data.repository

import com.example.fplyzer.data.api.ApiClient
import com.example.fplyzer.data.api.FplApiService
import com.example.fplyzer.data.models.BootstrapStatic
import com.example.fplyzer.data.models.LeagueStandingsResponse
import com.example.fplyzer.data.models.Manager
import com.example.fplyzer.data.models.ManagerHistory
import com.example.fplyzer.data.models.ManagerTeam
import com.example.fplyzer.data.models.differentials.DifferentialAnalysis
import com.example.fplyzer.data.models.differentials.DifferentialImpact
import com.example.fplyzer.data.models.differentials.DifferentialOutcome
import com.example.fplyzer.data.models.differentials.DifferentialModelPick
import com.example.fplyzer.data.models.differentials.PlayerData
import com.example.fplyzer.data.models.differentials.RiskLevel
import com.example.fplyzer.data.models.whatif.ScenarioImpact
import com.example.fplyzer.data.models.whatif.ScenarioType
import com.example.fplyzer.data.models.whatif.WhatIfResult
import com.example.fplyzer.data.models.whatif.WhatIfScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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

                // Get actual manager teams for recent gameweeks
                val gameweeksToAnalyze = maxOf(1, currentGameweek - 4)..currentGameweek
                val managerTeams = mutableMapOf<Int, List<Pair<Int, ManagerTeam>>>()

                // Fetch actual teams for each manager
                managers.take(10).forEach { manager ->
                    val teams = mutableListOf<Pair<Int, ManagerTeam>>()
                    gameweeksToAnalyze.forEach { gw ->
                        try {
                            val teamResult = getManagerTeam(manager.entry, gw)
                            if (teamResult.isSuccess) {
                                teamResult.getOrNull()?.let { team ->
                                    teams.add(gw to team)
                                }
                            }
                        } catch (e: Exception) {
                            // Skip this gameweek if failed
                        }
                    }
                    if (teams.isNotEmpty()) {
                        managerTeams[manager.entry] = teams
                    }
                }

                // Calculate league ownership for each player
                val leagueOwnership = mutableMapOf<Int, Int>()
                val totalManagers = managerTeams.size

                managerTeams.values.forEach { teams ->
                    teams.forEach { (_, team) ->
                        team.picks.forEach { pick ->
                            leagueOwnership[pick.element] = (leagueOwnership[pick.element] ?: 0) + 1
                        }
                    }
                }

                // Create actual differential analyses
                val analyses = managerTeams.map { (managerId, teams) ->
                    val manager = managers.find { it.entry == managerId }!!

                    // Find actual differential picks
                    val differentialModelPicks = mutableListOf<DifferentialModelPick>()

                    teams.forEach { (gameweek, team) ->
                        team.picks.forEach { pick ->
                            val player = allPlayers[pick.element]
                            if (player != null) {
                                val leagueOwn = leagueOwnership[pick.element] ?: 0
                                val leagueOwnershipPct = (leagueOwn.toDouble() / totalManagers) * 100
                                val globalOwnershipPct = player.selectedByPercent.toDoubleOrNull() ?: 0.0

                                // Consider it a differential if owned by <50% of league AND <20% globally
                                if (leagueOwnershipPct < 50.0 && globalOwnershipPct < 20.0) {
                                    // Calculate actual points for this gameweek
                                    val actualPoints = if (gameweek == currentGameweek) {
                                        player.eventPoints
                                    } else {
                                        // For past gameweeks, we'd need historical data
                                        // For now, use a portion of total points
                                        player.totalPoints / 20 // Rough estimate
                                    }

                                    val outcome = when {
                                        actualPoints >= 15 -> DifferentialOutcome.MASTER_STROKE
                                        actualPoints >= 10 -> DifferentialOutcome.GOOD_PICK
                                        actualPoints >= 5 -> DifferentialOutcome.NEUTRAL
                                        actualPoints >= 2 -> DifferentialOutcome.POOR_CHOICE
                                        else -> DifferentialOutcome.DISASTER
                                    }

                                    // Check if we already have this player
                                    val existingPickIndex = differentialModelPicks.indexOfFirst { it.player.id == player.id }
                                    if (existingPickIndex != -1) {
                                        // Update existing pick
                                        val existingPick = differentialModelPicks[existingPickIndex]
                                        val updatedPointsScored = existingPick.pointsScored + actualPoints
                                        val updatedGameweeks = existingPick.gameweeksPicked + gameweek

                                        // Recalculate differential score with updated points
                                        val updatedDifferentialScore = updatedPointsScored * (1 - leagueOwnershipPct / 100)

                                        differentialModelPicks[existingPickIndex] = existingPick.copy(
                                            gameweeksPicked = updatedGameweeks,
                                            pointsScored = updatedPointsScored,
                                            differentialScore = updatedDifferentialScore // ✅ Now properly updated!
                                        )

                                        // Debug logging (remove in production)
                                        println("Updated differential: ${player.webName} - Points: $updatedPointsScored, Score: $updatedDifferentialScore")
                                    } else {
                                        // Add new differential pick
                                        differentialModelPicks.add(
                                            DifferentialModelPick(
                                                id = "diff_${managerId}_${player.id}",
                                                player = PlayerData(
                                                    id = player.id,
                                                    displayName = player.webName,
                                                    webName = player.webName,
                                                    elementType = player.elementType,
                                                    nowCost = player.nowCost,
                                                    totalPoints = player.totalPoints,
                                                    eventPoints = player.eventPoints,
                                                    ownership = globalOwnershipPct,
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
                                                gameweeksPicked = listOf(gameweek),
                                                pointsScored = actualPoints,
                                                leagueOwnership = leagueOwnershipPct,
                                                globalOwnership = globalOwnershipPct,
                                                differentialScore = actualPoints * (1 - leagueOwnershipPct / 100),
                                                outcome = outcome,
                                                impact = if (actualPoints >= 10) DifferentialImpact.HIGH else DifferentialImpact.MEDIUM
                                            )
                                        )

                                        // Debug logging (remove in production)
                                        println("New differential: ${player.webName} - Points: $actualPoints, Score: ${actualPoints * (1 - leagueOwnershipPct / 100)}")
                                    }

                                }
                            }
                        }
                    }

                    val totalDifferentialPoints = differentialModelPicks.sumOf { it.pointsScored }
                    val successfulPicks = differentialModelPicks.count {
                        it.outcome == DifferentialOutcome.MASTER_STROKE || it.outcome == DifferentialOutcome.GOOD_PICK
                    }
                    val successRate = if (differentialModelPicks.isNotEmpty()) {
                        (successfulPicks.toDouble() / differentialModelPicks.size) * 100
                    } else 0.0

                    val riskRating = when (differentialModelPicks.size) {
                        0, 1 -> RiskLevel.CONSERVATIVE
                        2, 3 -> RiskLevel.BALANCED
                        4, 5 -> RiskLevel.AGGRESSIVE
                        else -> RiskLevel.RECKLESS
                    }

                    DifferentialAnalysis(
                        id = "analysis_$managerId",
                        managerId = managerId,
                        managerName = manager.playerName,
                        differentialModelPicks = differentialModelPicks,
                        missedOpportunities = emptyList(),
                        differentialSuccessRate = successRate,
                        totalDifferentialPoints = totalDifferentialPoints,
                        riskRating = riskRating,
                        biggestSuccess = differentialModelPicks.maxByOrNull { it.pointsScored },
                        biggestFailure = differentialModelPicks.minByOrNull { it.pointsScored }
                    )
                }.filter { it.differentialModelPicks.isNotEmpty() }

                Result.success(analyses)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
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
                    print(e)
                }

                Result.success(scenarios)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}