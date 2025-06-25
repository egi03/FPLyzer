package com.example.fplyzer.data.models.statistics

import com.example.fplyzer.data.models.League
import com.example.fplyzer.data.models.StandingResult

data class LeagueStatistics(
    val leagueInfo: League,
    val standings: List<StandingResult>,
    val managerStats: Map<Int, ManagerStatistics>,
    val leagueAverages: LeagueAverages,
    val chipAnalysis: ChipAnalysis,
    val weeklyStats: List<WeeklyLeagueStats>
)
