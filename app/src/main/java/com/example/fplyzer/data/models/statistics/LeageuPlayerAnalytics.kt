package com.example.fplyzer.data.models.statistics

data class LeaguePlayerAnalytics(
    val leagueId: Int,
    val gameweek: Int,
    val playerOwnership: List<PlayerOwnership>,
    val captaincy: List<CaptaincyData>,
    val differentials: List<DifferentialPick>,
    val templateTeam: List<Int>,
    val transferTrends: TransferTrends,
    val playerPerformance: List<PlayerLeaguePerformance>,
    val benchAnalysis: BenchAnalysis
)

data class PlayerOwnership(
    val playerId: Int,
    val playerName: String,
    val teamName: String,
    val position: String,
    val ownershipCount: Int,
    val ownershipPercentage: Double,
    val effectiveOwnership: Double,
    val price: Double,
    val points: Int,
    val isTemplate: Boolean, // Part of template team (>50% ownership)
    val isDifferential: Boolean // Low ownership but high points
)

data class CaptaincyData(
    val playerId: Int,
    val playerName: String,
    val captainCount: Int,
    val captainPercentage: Double,
    val viceCaptainCount: Int,
    val totalPointsEarned: Int,
    val averageReturn: Double,
    val successRate: Double // % of times returned above average
)

data class DifferentialPick(
    val playerId: Int,
    val playerName: String,
    val teamName: String,
    val ownershipPercentage: Double,
    val points: Int,
    val pointsPerMillion: Double,
    val managers: List<String>, // Managers who own this differential
    val differentialScore: Double // Calculated based on low ownership + high returns
)

data class TransferTrends(
    val mostTransferredIn: List<TransferMovement>,
    val mostTransferredOut: List<TransferMovement>,
    val priceRisers: List<PriceChange>,
    val priceFallers: List<PriceChange>,
    val bandwagons: List<Bandwagon>,
    val kneejerk: List<KneejerkTransfer>
)

data class TransferMovement(
    val playerId: Int,
    val playerName: String,
    val transferCount: Int,
    val percentageOfManagers: Double,
    val priceChange: Double,
    val formChange: Double
)

data class PriceChange(
    val playerId: Int,
    val playerName: String,
    val startPrice: Double,
    val currentPrice: Double,
    val totalChange: Double,
    val leagueOwnership: Double
)

data class Bandwagon(
    val playerId: Int,
    val playerName: String,
    val managersJoining: Int,
    val gameweekStarted: Int,
    val momentum: Double
)

data class KneejerkTransfer(
    val playerIn: Int,
    val playerOut: Int,
    val playerInName: String,
    val playerOutName: String,
    val triggerEvent: String,
    val managersCount: Int
)

data class PlayerLeaguePerformance(
    val playerId: Int,
    val playerName: String,
    val totalPoints: Int,
    val averagePoints: Double,
    val consistency: Double,
    val explosiveness: Double,
    val ownershipWeightedPoints: Double,
    val captaincyROI: Double,
    val benchFrequency: Double,
    val performanceRank: Int
)

data class BenchAnalysis(
    val totalBenchPoints: Int,
    val averageBenchPoints: Double,
    val mostBenchedPlayers: List<BenchedPlayer>,
    val costliestBenching: List<CostlyBench>,
    val benchBoostSuccess: List<BenchBoostAnalysis>
)

data class BenchedPlayer(
    val playerId: Int,
    val playerName: String,
    val benchCount: Int,
    val pointsMissed: Int,
    val managers: List<String>
)

data class CostlyBench(
    val managerId: Int,
    val managerName: String,
    val gameweek: Int,
    val playerName: String,
    val pointsBenched: Int
)

data class BenchBoostAnalysis(
    val managerId: Int,
    val managerName: String,
    val gameweek: Int,
    val benchPoints: Int,
    val success: Boolean
)

data class TemplateAnalysis(
    val gameweek: Int,
    val templatePlayers: List<Int>,
    val templateOwnership: Double,
    val templatePoints: Int,
    val nonTemplateTopPerformers: List<PlayerOwnership>,
    val templateEVO: Double
)

data class PlayerComparison(
    val player1Id: Int,
    val player2Id: Int,
    val player1Name: String,
    val player2Name: String,
    val ownership1: Double,
    val ownership2: Double,
    val points1: Int,
    val points2: Int,
    val form1: Double,
    val form2: Double,
    val managersWithBoth: Int,
    val managersWith1Only: Int,
    val managersWith2Only: Int,
    val recommendation: String
)

data class PositionAnalysis(
    val position: String,
    val mostOwned: List<PlayerOwnership>,
    val bestValue: List<PlayerValueMetric>,
    val premiums: List<PremiumAnalysis>,
    val budgets: List<BudgetAnalysis>,
    val positionTrends: PositionTrends
)

data class PlayerValueMetric(
    val playerId: Int,
    val playerName: String,
    val price: Double,
    val points: Int,
    val pointsPerMillion: Double,
    val pointsPerGame: Double,
    val ownership: Double,
    val valueScore: Double
)

data class PremiumAnalysis(
    val playerId: Int,
    val playerName: String,
    val price: Double,
    val ownership: Double,
    val justificationScore: Double,
    val alternativesCount: Int
)

data class BudgetAnalysis(
    val playerId: Int,
    val playerName: String,
    val price: Double,
    val ownership: Double,
    val enablerScore: Double,
    val startingFrequency: Double
)

data class PositionTrends(
    val averageSpend: Double,
    val premiumOwnership: Double,
    val rotationRisk: List<Int>,
    val emergingOptions: List<Int>
)