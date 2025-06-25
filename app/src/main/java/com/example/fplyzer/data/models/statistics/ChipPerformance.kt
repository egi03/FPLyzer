package com.example.fplyzer.data.models.statistics

data class ChipPerformance(
    val managerId: Int,
    val managerName: String,
    val chipName: String,
    val gameweek: Int,
    val points: Int,
    val pointsGained: Int, // Compared to average
    val rank: Int
)