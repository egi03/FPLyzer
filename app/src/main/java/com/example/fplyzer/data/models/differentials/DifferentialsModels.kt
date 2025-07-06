package com.example.fplyzer.data.models.differentials

import com.google.gson.annotations.SerializedName

data class DifferentialAnalysis(
    val id: String,
    val managerId: Int,
    val managerName: String,
    val differentialPicks: List<DifferentialPick>,
    val missedOpportunities: List<MissedDifferential>,
    val differentialSuccessRate: Double,
    val totalDifferentialPoints: Int,
    val riskRating: RiskLevel,
    val biggestSuccess: DifferentialPick?,
    val biggestFailure: DifferentialPick?
)

data class DifferentialPick(
    val id: String,
    val player: PlayerData,
    val gameweeksPicked: List<Int>,
    val pointsScored: Int,
    val leagueOwnership: Double,
    val globalOwnership: Double,
    val differentialScore: Double,
    val outcome: DifferentialOutcome,
    val impact: DifferentialImpact
)

data class MissedDifferential(
    val id: String,
    val player: PlayerData,
    val gameweeks: List<Int>,
    val pointsMissed: Int,
    val ownedByManagers: List<String>,
    val potentialGain: Int
)

enum class RiskLevel(val label: String, val description: String) {
    CONSERVATIVE("Conservative", "Plays it safe with template picks"),
    BALANCED("Balanced", "Good mix of template and differentials"),
    AGGRESSIVE("Aggressive", "Bold differential choices"),
    RECKLESS("Reckless", "High-risk, high-reward strategy");

    val color: String
        get() = when (this) {
            CONSERVATIVE -> "blue"
            BALANCED -> "green"
            AGGRESSIVE -> "orange"
            RECKLESS -> "red"
        }
}

enum class DifferentialOutcome(val label: String, val emoji: String) {
    MASTER_STROKE("Master Stroke", "🧠"),
    GOOD_PICK("Good Pick", "✅"),
    NEUTRAL("Neutral", "📊"),
    POOR_CHOICE("Poor Choice", "😬"),
    DISASTER("Disaster", "💥");

    val color: String
        get() = when (this) {
            MASTER_STROKE -> "purple"
            GOOD_PICK -> "green"
            NEUTRAL -> "blue"
            POOR_CHOICE -> "orange"
            DISASTER -> "red"
        }
}

enum class DifferentialImpact(val label: String, val color: String) {
    CRITICAL("Critical", "red"),
    HIGH("High", "orange"),
    MEDIUM("Medium", "yellow"),
    LOW("Low", "green")
}

data class LeagueDifferentialSummary(
    val totalDifferentials: Int,
    val successfulDifferentials: Int,
    val failedDifferentials: Int,
    val successRate: Double,
    val topDifferential: DifferentialPick?,
    val worstDifferential: DifferentialPick?,
    val mostConservativeManager: String?,
    val mostAggressiveManager: String?,
    val averageRiskLevel: RiskLevel
)

data class PlayerData(
    val id: Int,
    val displayName: String,
    val webName: String,
    val elementType: Int,
    val nowCost: Int,
    val totalPoints: Int,
    val eventPoints: Int,
    val ownership: Double,
    val form: String?,
    val goalsScored: Int,
    val assists: Int,
    val cleanSheets: Int,
    val minutes: Int,
    val selectedByPercent: String,
    val pointsPerGame: String,
    val news: String?,
    val ictIndex: String?
) {
    val pointsPerMinute: Double
        get() = if (minutes > 0) totalPoints.toDouble() / (minutes / 90.0) else 0.0
}