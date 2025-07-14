package com.example.fplyzer.data.mock

import com.example.fplyzer.data.models.*
import com.example.fplyzer.data.models.statistics.*
import com.example.fplyzer.data.models.differentials.*
import com.example.fplyzer.data.models.whatif.*
import kotlin.random.Random

object MockDataFactory {

    fun createMockLeagueStatistics(): LeagueStatistics {
        val league = createMockLeague()
        val standings = createMockStandings()
        val managerStats = createMockManagerStats()
        val leagueAverages = createMockLeagueAverages(managerStats)
        val chipAnalysis = createMockChipAnalysis()
        val weeklyStats = createMockWeeklyStats()

        return LeagueStatistics(
            leagueInfo = league,
            standings = standings,
            managerStats = managerStats,
            leagueAverages = leagueAverages,
            chipAnalysis = chipAnalysis,
            weeklyStats = weeklyStats
        )
    }

    private fun createMockLeague(): League {
        return League(
            id = 999999,
            name = "Demo League",
            created = "2024-07-15T12:00:00Z",
            closed = false,
            maxEntries = null,
            leagueType = "c",
            scoring = "c",
            adminEntry = 123456,
            startEvent = 1,
            codePrivacy = "p",
            hasCup = false,
            cupLeague = null,
            rank = null
        )
    }

    private fun createMockStandings(): List<StandingResult> {
        val managerNames = listOf(
            "Alex Thunder" to "Thunder FC",
            "Sarah Lightning" to "Lightning Bolts",
            "Mike Storm" to "Storm Chasers",
            "Emma Blaze" to "Blazing Squad",
            "Chris Rocket" to "Rocket Rangers",
            "Jessica Titan" to "Titan Force",
            "David Phoenix" to "Phoenix Rising",
            "Lisa Venom" to "Venom Strike",
            "Ryan Fury" to "Fury United",
            "Chloe Nova" to "Nova Stars",
            "Jake Vortex" to "Vortex Warriors",
            "Sophie Apex" to "Apex Legends"
        )

        return managerNames.mapIndexed { index, (playerName, teamName) ->
            val totalPoints = when (index) {
                0 -> 1847
                1 -> 1823
                2 -> 1801
                3 -> 1785
                4 -> 1769
                5 -> 1751
                6 -> 1734
                7 -> 1718
                8 -> 1695
                9 -> 1672
                10 -> 1651
                11 -> 1628
                else -> 1600 - (index * 10)
            }

            StandingResult(
                id = index + 1,
                eventTotal = Random.nextInt(35, 95),
                playerName = playerName,
                rank = index + 1,
                lastRank = if (index == 0) 2 else if (index == 1) 1 else index + Random.nextInt(-2, 3),
                rankSort = index + 1,
                total = totalPoints,
                entry = 100000 + index,
                entryName = teamName
            )
        }
    }

    private fun createMockManagerStats(): Map<Int, ManagerStatistics> {
        val standings = createMockStandings()

        return standings.associate { standing ->
            val pointsHistory = generateRealisticPointsHistory()
            val rankHistory = generateRealisticRankHistory()
            val chips = generateMockChips()

            standing.entry to ManagerStatistics(
                managerId = standing.entry,
                managerName = standing.playerName,
                teamName = standing.entryName,
                totalPoints = standing.total,
                averagePoints = pointsHistory.average(),
                standardDeviation = calculateStandardDeviation(pointsHistory),
                consistency = calculateConsistency(pointsHistory),
                bestWeek = createBestWeek(pointsHistory),
                worstWeek = createWorstWeek(pointsHistory),
                currentStreak = Random.nextInt(-3, 5),
                longestWinStreak = Random.nextInt(2, 8),
                totalTransfers = Random.nextInt(15, 35),
                totalHits = Random.nextInt(0, 12) * 4,
                teamValue = Random.nextInt(1000, 1030),
                benchPoints = Random.nextInt(80, 180),
                captainPoints = Random.nextInt(200, 350),
                chipsUsed = chips,
                rankHistory = rankHistory,
                pointsHistory = pointsHistory,
                monthlyPoints = generateMonthlyPoints(pointsHistory),
                headToHeadRecord = emptyMap()
            )
        }
    }

    private fun generateRealisticPointsHistory(): List<Int> {
        val gameweeks = 15
        val basePoints = 55
        val points = mutableListOf<Int>()

        for (i in 1..gameweeks) {
            val variation = when {
                i <= 3 -> Random.nextInt(-15, 20) // Early season variance
                i in 4..6 -> Random.nextInt(-10, 25) // Settling in
                i in 7..10 -> Random.nextInt(-8, 30) // Mid season form
                else -> Random.nextInt(-12, 28) // Late season
            }

            val weekPoints = (basePoints + variation).coerceIn(25, 120)
            points.add(weekPoints)
        }

        return points
    }

    private fun generateRealisticRankHistory(): List<Int> {
        val gameweeks = 15
        val ranks = mutableListOf<Int>()
        var currentRank = Random.nextInt(100000, 2000000)

        for (i in 1..gameweeks) {
            val change = Random.nextInt(-50000, 30000)
            currentRank = (currentRank + change).coerceIn(10000, 3000000)
            ranks.add(currentRank)
        }

        return ranks
    }

    private fun generateMockChips(): List<ChipPlay> {
        val possibleChips = listOf("wildcard", "bboost", "3xc", "freehit")
        val usedChips = possibleChips.shuffled().take(Random.nextInt(1, 4))

        return usedChips.mapIndexed { index, chip ->
            ChipPlay(
                name = chip,
                time = "2024-${Random.nextInt(8, 12)}-${Random.nextInt(10, 28)}T15:30:00Z",
                event = Random.nextInt(3, 15)
            )
        }
    }

    private fun calculateStandardDeviation(points: List<Int>): Double {
        val mean = points.average()
        val variance = points.map { (it - mean) * (it - mean) }.average()
        return kotlin.math.sqrt(variance)
    }

    private fun calculateConsistency(points: List<Int>): Double {
        val mean = points.average()
        val std = calculateStandardDeviation(points)
        return if (mean > 0) std / mean else 0.0
    }

    private fun createBestWeek(points: List<Int>): GameweekHistory {
        val bestPoints = points.maxOrNull() ?: 70
        val bestWeek = points.indexOf(bestPoints) + 1

        return GameweekHistory(
            event = bestWeek,
            points = bestPoints,
            totalPoints = points.take(bestWeek).sum(),
            rank = Random.nextInt(1, 100000),
            rankSort = Random.nextInt(1, 100000),
            overallRank = Random.nextInt(50000, 500000),
            bank = Random.nextInt(0, 50),
            value = Random.nextInt(1000, 1030),
            eventTransfers = Random.nextInt(0, 3),
            eventTransfersCost = Random.nextInt(0, 8),
            pointsOnBench = Random.nextInt(0, 25)
        )
    }

    private fun createWorstWeek(points: List<Int>): GameweekHistory {
        val worstPoints = points.minOrNull() ?: 30
        val worstWeek = points.indexOf(worstPoints) + 1

        return GameweekHistory(
            event = worstWeek,
            points = worstPoints,
            totalPoints = points.take(worstWeek).sum(),
            rank = Random.nextInt(1000000, 3000000),
            rankSort = Random.nextInt(1000000, 3000000),
            overallRank = Random.nextInt(2000000, 4000000),
            bank = Random.nextInt(0, 50),
            value = Random.nextInt(1000, 1030),
            eventTransfers = Random.nextInt(0, 3),
            eventTransfersCost = Random.nextInt(0, 8),
            pointsOnBench = Random.nextInt(0, 25)
        )
    }

    private fun generateMonthlyPoints(pointsHistory: List<Int>): Map<String, Int> {
        return mapOf(
            "August" to pointsHistory.take(4).sum(),
            "September" to pointsHistory.drop(4).take(4).sum(),
            "October" to pointsHistory.drop(8).take(4).sum(),
            "November" to pointsHistory.drop(12).sum()
        )
    }

    private fun createMockLeagueAverages(managerStats: Map<Int, ManagerStatistics>): LeagueAverages {
        val managers = managerStats.values.toList()

        return LeagueAverages(
            averagePoints = managers.map { it.averagePoints }.average(),
            averageTransfers = managers.map { it.totalTransfers.toDouble() }.average(),
            averageHits = managers.map { it.totalHits.toDouble() }.average(),
            averageTeamValue = managers.map { it.teamValue.toDouble() }.average(),
            averageBenchPoints = managers.map { it.benchPoints.toDouble() }.average(),
            topPerformers = createMockTopPerformers(managers),
            consistency = createMockConsistencyMetrics(managers)
        )
    }

    private fun createMockTopPerformers(managers: List<ManagerStatistics>): List<PerformanceMetric> {
        return listOf(
            PerformanceMetric(
                managerId = managers.maxByOrNull { it.averagePoints }?.managerId ?: 0,
                managerName = managers.maxByOrNull { it.averagePoints }?.managerName ?: "",
                metric = "Highest Average",
                value = managers.maxOfOrNull { it.averagePoints } ?: 0.0
            ),
            PerformanceMetric(
                managerId = managers.minByOrNull { it.consistency }?.managerId ?: 0,
                managerName = managers.minByOrNull { it.consistency }?.managerName ?: "",
                metric = "Most Consistent",
                value = managers.minOfOrNull { it.consistency } ?: 0.0
            ),
            PerformanceMetric(
                managerId = managers.maxByOrNull { it.bestWeek?.points ?: 0 }?.managerId ?: 0,
                managerName = managers.maxByOrNull { it.bestWeek?.points ?: 0 }?.managerName ?: "",
                metric = "Best Week",
                value = managers.maxOfOrNull { it.bestWeek?.points?.toDouble() ?: 0.0 } ?: 0.0
            )
        )
    }

    private fun createMockConsistencyMetrics(managers: List<ManagerStatistics>): List<ConsistencyMetric> {
        return managers.map { manager ->
            ConsistencyMetric(
                managerId = manager.managerId,
                managerName = manager.managerName,
                consistency = manager.consistency,
                averageDeviation = manager.standardDeviation
            )
        }.sortedBy { it.consistency }
    }

    private fun createMockChipAnalysis(): ChipAnalysis {
        return ChipAnalysis(
            bestWildcards = createMockChipPerformances("wildcard"),
            bestBenchBoosts = createMockChipPerformances("bboost"),
            bestTripleCaptains = createMockChipPerformances("3xc"),
            bestFreeHits = createMockChipPerformances("freehit"),
            worstChips = emptyList(),
            unusedChips = emptyMap()
        )
    }

    private fun createMockChipPerformances(chipName: String): List<ChipPerformance> {
        val standings = createMockStandings().take(5)

        return standings.map { standing ->
            val points = when (chipName) {
                "wildcard" -> Random.nextInt(55, 85)
                "bboost" -> Random.nextInt(65, 95)
                "3xc" -> Random.nextInt(45, 120)
                "freehit" -> Random.nextInt(60, 90)
                else -> Random.nextInt(50, 80)
            }

            ChipPerformance(
                managerId = standing.entry,
                managerName = standing.playerName,
                chipName = chipName,
                gameweek = Random.nextInt(5, 15),
                points = points,
                pointsGained = points - 55,
                rank = Random.nextInt(1, 1000000)
            )
        }
    }

    private fun createMockWeeklyStats(): List<WeeklyLeagueStats> {
        return (1..15).map { gw ->
            WeeklyLeagueStats(
                gameweek = gw,
                averagePoints = Random.nextInt(45, 75).toDouble(),
                highestScore = Random.nextInt(80, 120),
                highestScorer = createMockStandings().random().playerName,
                lowestScore = Random.nextInt(25, 45),
                lowestScorer = createMockStandings().random().playerName,
                mostCaptained = getMockPlayerName(),
                transfersMade = Random.nextInt(15, 45)
            )
        }
    }

    fun createMockPlayerAnalytics(): LeaguePlayerAnalytics {
        return LeaguePlayerAnalytics(
            leagueId = 999999,
            gameweek = 15,
            playerOwnership = createMockPlayerOwnership(),
            captaincy = createMockCaptaincyData(),
            differentials = createMockStatisticsDifferentials(),
            templateTeam = listOf(1, 2, 15, 25, 35, 45, 55, 65, 75, 85, 95),
            transferTrends = createMockTransferTrends(),
            playerPerformance = emptyList(),
            benchAnalysis = createMockBenchAnalysis()
        )
    }

    private fun createMockPlayerOwnership(): List<PlayerOwnership> {
        val mockPlayers = listOf(
            Triple("Haaland", "MCI", "FWD"),
            Triple("Salah", "LIV", "MID"),
            Triple("Son", "TOT", "MID"),
            Triple("Alexander-Arnold", "LIV", "DEF"),
            Triple("Pickford", "EVE", "GKP"),
            Triple("Watkins", "AVL", "FWD"),
            Triple("Palmer", "CHE", "MID"),
            Triple("Saka", "ARS", "MID"),
            Triple("Cunha", "WOL", "FWD"),
            Triple("Gabriel", "ARS", "DEF"),
            Triple("Mbeumo", "BRE", "FWD"),
            Triple("Rogers", "AVL", "MID")
        )

        return mockPlayers.mapIndexed { index, (name, team, position) ->
            val ownership = when (index) {
                0, 1 -> Random.nextInt(85, 100) // Template players
                2, 3, 4 -> Random.nextInt(60, 85) // Popular picks
                5, 6, 7 -> Random.nextInt(30, 60) // Moderate ownership
                else -> Random.nextInt(5, 30) // Differentials
            }

            PlayerOwnership(
                playerId = index + 1,
                playerName = name,
                teamName = team,
                position = position,
                ownershipCount = (ownership * 12 / 100),
                ownershipPercentage = ownership.toDouble(),
                effectiveOwnership = ownership + Random.nextInt(-5, 10).toDouble(),
                price = Random.nextInt(45, 130) / 10.0,
                points = Random.nextInt(2, 15),
                isTemplate = ownership > 70,
                isDifferential = ownership < 15 && Random.nextInt(0, 10) > 7
            )
        }
    }

    private fun createMockCaptaincyData(): List<CaptaincyData> {
        val topPlayers = listOf("Haaland", "Salah", "Son", "Palmer", "Watkins")

        return topPlayers.mapIndexed { index, name ->
            val captainCount = when (index) {
                0 -> 8 // Most captained
                1 -> 3
                2 -> 1
                else -> 0
            }

            CaptaincyData(
                playerId = index + 1,
                playerName = name,
                captainCount = captainCount,
                captainPercentage = (captainCount * 100.0 / 12),
                viceCaptainCount = if (captainCount > 0) Random.nextInt(1, 3) else 0,
                totalPointsEarned = captainCount * Random.nextInt(8, 20),
                averageReturn = Random.nextInt(6, 15).toDouble(),
                successRate = if (captainCount > 0) Random.nextInt(60, 90).toDouble() else 0.0
            )
        }.filter { it.captainCount > 0 }
    }

    private fun createMockStatisticsDifferentials(): List<DifferentialPick> {
        val differentialPlayers = listOf("Mbeumo", "Rogers", "Cunha", "Diogo Jota")

        return differentialPlayers.mapIndexed { index, name ->
            DifferentialPick(
                playerId = index + 20,
                playerName = name,
                teamName = listOf("BRE", "AVL", "WOL", "LIV")[index],
                ownershipPercentage = Random.nextInt(5, 20).toDouble(),
                points = Random.nextInt(8, 18),
                pointsPerMillion = Random.nextDouble(1.0, 3.0),
                managers = createMockStandings().shuffled().take(Random.nextInt(1, 3)).map { it.playerName },
                differentialScore = Random.nextDouble(5.0, 25.0)
            )
        }
    }

    private fun createMockTransferTrends(): TransferTrends {
        return TransferTrends(
            mostTransferredIn = emptyList(),
            mostTransferredOut = emptyList(),
            priceRisers = emptyList(),
            priceFallers = emptyList(),
            bandwagons = emptyList(),
            kneejerk = emptyList()
        )
    }

    private fun createMockBenchAnalysis(): BenchAnalysis {
        return BenchAnalysis(
            totalBenchPoints = Random.nextInt(150, 250),
            averageBenchPoints = Random.nextDouble(10.0, 20.0),
            mostBenchedPlayers = emptyList(),
            costliestBenching = emptyList(),
            benchBoostSuccess = emptyList()
        )
    }

    fun createMockDifferentialAnalyses(): List<DifferentialAnalysis> {
        val standings = createMockStandings()

        return standings.take(8).mapIndexed { index, standing ->
            val differentialPicks = createMockDifferentialModelPicks(index)
            val successRate = if (differentialPicks.isNotEmpty()) {
                differentialPicks.count { it.outcome == DifferentialOutcome.MASTER_STROKE || it.outcome == DifferentialOutcome.GOOD_PICK }.toDouble() / differentialPicks.size * 100
            } else 0.0

            DifferentialAnalysis(
                id = "diff_${standing.entry}",
                managerId = standing.entry,
                managerName = standing.playerName,
                differentialModelPicks = differentialPicks,
                missedOpportunities = emptyList(),
                differentialSuccessRate = successRate,
                totalDifferentialPoints = differentialPicks.sumOf { it.pointsScored },
                riskRating = when (differentialPicks.size) {
                    0, 1 -> RiskLevel.CONSERVATIVE
                    2, 3 -> RiskLevel.BALANCED
                    4, 5 -> RiskLevel.AGGRESSIVE
                    else -> RiskLevel.RECKLESS
                },
                biggestSuccess = differentialPicks.maxByOrNull { it.pointsScored },
                biggestFailure = differentialPicks.minByOrNull { it.pointsScored }
            )
        }
    }

    private fun createMockDifferentialModelPicks(managerIndex: Int): List<DifferentialModelPick> {
        val differentialPlayers = listOf(
            "Mbeumo" to "BRE",
            "Rogers" to "AVL",
            "Cunha" to "WOL",
            "Diogo Jota" to "LIV",
            "Isak" to "NEW",
            "Kudus" to "WHU"
        )

        val numPicks = when (managerIndex) {
            0, 1 -> Random.nextInt(0, 2) // Conservative managers
            2, 3, 4 -> Random.nextInt(1, 4) // Balanced
            else -> Random.nextInt(2, 6) // Aggressive
        }

        return differentialPlayers.shuffled().take(numPicks).mapIndexed { index, (name, team) ->
            val points = Random.nextInt(2, 20)
            val outcome = when {
                points >= 15 -> DifferentialOutcome.MASTER_STROKE
                points >= 10 -> DifferentialOutcome.GOOD_PICK
                points >= 6 -> DifferentialOutcome.NEUTRAL
                points >= 3 -> DifferentialOutcome.POOR_CHOICE
                else -> DifferentialOutcome.DISASTER
            }

            DifferentialModelPick(
                id = "pick_${managerIndex}_$index",
                player = PlayerData(
                    id = index + 50,
                    displayName = name,
                    webName = name,
                    elementType = Random.nextInt(2, 5),
                    nowCost = Random.nextInt(45, 85),
                    totalPoints = Random.nextInt(50, 150),
                    eventPoints = points,
                    ownership = Random.nextDouble(2.0, 15.0),
                    form = Random.nextDouble(3.0, 8.0).toString(),
                    goalsScored = Random.nextInt(0, 8),
                    assists = Random.nextInt(0, 6),
                    cleanSheets = Random.nextInt(0, 8),
                    minutes = Random.nextInt(800, 1350),
                    selectedByPercent = Random.nextDouble(2.0, 15.0).toString(),
                    pointsPerGame = Random.nextDouble(3.0, 8.0).toString(),
                    news = "",
                    ictIndex = Random.nextDouble(5.0, 15.0).toString()
                ),
                gameweeksPicked = listOf(Random.nextInt(10, 15)),
                pointsScored = points,
                leagueOwnership = Random.nextDouble(5.0, 25.0),
                globalOwnership = Random.nextDouble(2.0, 15.0),
                differentialScore = points * (1 - Random.nextDouble(0.05, 0.25)),
                outcome = outcome,
                impact = if (points >= 12) DifferentialImpact.HIGH else DifferentialImpact.MEDIUM
            )
        }
    }

    fun createMockWhatIfScenarios(): List<WhatIfScenario> {
        val standings = createMockStandings()

        return listOf(
            // Captain scenario
            WhatIfScenario(
                id = "captain_gw15",
                title = "Optimal Captain GW15",
                description = "What if everyone captained Haaland who scored 18 points?",
                type = ScenarioType.CAPTAIN_CHANGE,
                gameweek = 15,
                results = standings.map { standing ->
                    val rankChange = Random.nextInt(-3, 4)
                    WhatIfResult(
                        managerId = standing.entry,
                        managerName = standing.playerName,
                        originalRank = standing.rank,
                        newRank = (standing.rank + rankChange).coerceIn(1, standings.size),
                        rankChange = rankChange,
                        pointsChange = rankChange * -3,
                        improvement = rankChange < 0,
                        significantChange = kotlin.math.abs(rankChange) >= 2
                    )
                },
                impact = ScenarioImpact(
                    averageRankChange = -0.5,
                    averagePointsChange = 8.0,
                    managersAffected = standings.size,
                    significantChanges = 6,
                    impactLevel = "Moderate impact"
                )
            ),
            // Chip timing scenario
            WhatIfScenario(
                id = "chip_timing",
                title = "Optimal Bench Boost Timing",
                description = "What if managers used Bench Boost during the highest-scoring gameweek?",
                type = ScenarioType.CHIP_TIMING,
                gameweek = null,
                results = standings.filter { Random.nextBoolean() }.map { standing ->
                    val rankChange = Random.nextInt(-5, 2)
                    WhatIfResult(
                        managerId = standing.entry,
                        managerName = standing.playerName,
                        originalRank = standing.rank,
                        newRank = (standing.rank + rankChange).coerceIn(1, standings.size),
                        rankChange = rankChange,
                        pointsChange = Random.nextInt(15, 35),
                        improvement = rankChange < 0,
                        significantChange = kotlin.math.abs(rankChange) >= 3
                    )
                },
                impact = ScenarioImpact(
                    averageRankChange = -1.8,
                    averagePointsChange = 22.0,
                    managersAffected = 7,
                    significantChanges = 4,
                    impactLevel = "High impact"
                )
            )
        )
    }

    private fun getMockPlayerName(): String {
        val players = listOf("Haaland", "Salah", "Son", "Palmer", "Saka", "Watkins", "Alexander-Arnold")
        return players.random()
    }
}