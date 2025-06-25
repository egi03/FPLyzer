package com.example.fplyzer.data.models.statistics

data class HeadToHeadRecord(
    val opponentId: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val totalPointsFor: Int,
    val totalPointsAgainst: Int
)