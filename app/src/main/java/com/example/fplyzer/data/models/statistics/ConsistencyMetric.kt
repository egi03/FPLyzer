package com.example.fplyzer.data.models.statistics

data class ConsistencyMetric(
    val managerId: Int,
    val managerName: String,
    val consistency: Double,
    val averageDeviation: Double
)