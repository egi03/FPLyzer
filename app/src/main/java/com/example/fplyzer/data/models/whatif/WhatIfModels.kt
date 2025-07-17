package com.example.fplyzer.data.models.whatif

data class WhatIfScenario(
    val id: String,
    val title: String,
    val description: String,
    val type: ScenarioType,
    val gameweek: Int?,
    val results: List<WhatIfResult>,
    val impact: ScenarioImpact
)

data class WhatIfResult(
    val managerId: Int,
    val managerName: String,
    val originalRank: Int,
    val newRank: Int,
    val rankChange: Int,
    val pointsChange: Int,
    val improvement: Boolean,
    val significantChange: Boolean
) {
    val isPositive: Boolean
        get() = rankChange < 0
}

enum class ScenarioType(val label: String, val icon: String) {
    CAPTAIN_CHANGE("Captain Choice", "star.circle"),
    TRANSFER_CHANGE("Transfer Decision", "arrow.left.arrow.right"),
    CHIP_TIMING("Chip Timing", "wand.and.stars"),
    TEAM_SELECTION("Team Selection", "person.3");

    val description: String
        get() = when (this) {
            CAPTAIN_CHANGE -> "What if different captains were chosen?"
            TRANSFER_CHANGE -> "What if different transfers were made?"
            CHIP_TIMING -> "What if chips were used at different times?"
            TEAM_SELECTION -> "What if different players were selected?"
        }
}

data class ScenarioImpact(
    val averageRankChange: Double,
    val averagePointsChange: Double,
    val managersAffected: Int,
    val significantChanges: Int,
    val impactLevel: String
) {
    val impactDescription: String
        get() = when {
            significantChanges == 0 -> "💤 Minimal impact"
            significantChanges < managersAffected / 3 -> "📊 Minor changes"
            significantChanges < managersAffected * 2 / 3 -> "📈 Moderate impact"
            else -> "🌪️ Major shake-up"
        }
}

data class WhatIfSummary(
    val totalScenariosAnalyzed: Int,
    val biggestPotentialGain: Int,
    val biggestMissedOpportunity: Int,
    val mostVolatileGameweek: Int?,
    val stabilityRating: Double
) {
    val stabilityDescription: String
        get() = when {
            stabilityRating >= 80 -> "🗿 Rock Solid"
            stabilityRating >= 60 -> "⚖️ Stable"
            stabilityRating >= 40 -> "🌊 Moderate"
            stabilityRating >= 20 -> "🌪️ Volatile"
            else -> "💥 Chaotic"
        }
}