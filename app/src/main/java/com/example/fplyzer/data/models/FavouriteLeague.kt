package com.example.fplyzer.data.models

data class FavouriteLeague(
    val id: Int,
    val name: String,
    val totalManagers: Int,
    val averagePoints: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)