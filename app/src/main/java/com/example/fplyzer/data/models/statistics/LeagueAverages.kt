package com.example.fplyzer.data.models.statistics

data class LeagueAverages(
    val averagePoints: Double,
    val averageTransfers: Double,
    val averageHits: Double,
    val averageTeamValue: Double,
    val averageBenchPoints: Double,
    val topPerformers: List<PerformanceMetric>,
    val consistency: List<ConsistencyMetric>
)