package com.example.fplyzer.data.models.statistics

import com.example.fplyzer.data.models.ChipPlay
import com.example.fplyzer.data.models.GameweekHistory

data class ManagerStatistics(
    val managerId: Int,
    val managerName: String,
    val teamName: String,
    val totalPoints: Int,
    val averagePoints: Double,
    val standardDeviation: Double,
    val consistency: Double,
    val bestWeek: GameweekHistory?,
    val worstWeek: GameweekHistory?,
    val currentStreak: Int,
    val longestWinStreak: Int,
    val totalTransfers: Int,
    val totalHits: Int,
    val teamValue: Int,
    val benchPoints: Int,
    val captainPoints: Int,
    val chipsUsed: List<ChipPlay>,
    val rankHistory: List<Int>,
    val pointsHistory: List<Int>,
    val monthlyPoints: Map<String, Int>,
    val headToHeadRecord: Map<Int, HeadToHeadRecord>
)