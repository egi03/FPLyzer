package com.example.fplyzer.data.models.statistics

data class TransferRecord(
    val playerIn: String,
    val playerOut: String,
    val gameweek: Int,
    val pointsGained: Int
)