package com.example.fplyzer.data.models.statistics

data class PerformanceMetric(
    val managerId: Int,
    val managerName: String,
    val metric: String,
    val value: Double
)