package com.example.fplyzer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fplyzer.ui.screens.home.HomeViewModel
import com.example.fplyzer.ui.screens.leagueStats.LeagueStatsViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(application) as T
            }
            modelClass.isAssignableFrom(LeagueStatsViewModel::class.java) -> {
                LeagueStatsViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}