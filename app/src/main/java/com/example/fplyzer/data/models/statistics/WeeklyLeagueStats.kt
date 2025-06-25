package com.example.fplyzer.data.models.statistics

data class WeeklyLeagueStats(
    val gameweek: Int,
    val averagePoints: Double,
    val highestScore: Int,
    val highestScorer: String,
    val lowestScore: Int,
    val lowestScorer: String,
    val mostCaptained: String,
    val transfersMade: Int
)