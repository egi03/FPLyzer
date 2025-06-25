package com.example.fplyzer.data.models.statistics

data class CaptainChoice(
    val managerId: Int,
    val gameweek: Int,
    val captain: String,
    val points: Int,
    val differential: Boolean
)