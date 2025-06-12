package com.example.fplyzer.data.models

import com.google.gson.annotations.SerializedName

data class Fixture(
    @SerializedName("code") val code: Int,
    @SerializedName("event") val event: Int?,
    @SerializedName("finished") val finished: Boolean,
    @SerializedName("finished_provisional") val finishedProvisional: Boolean,
    @SerializedName("id") val id: Int,
    @SerializedName("kickoff_time") val kickoffTime: String?,
    @SerializedName("minutes") val minutes: Int,
    @SerializedName("provisional_start_time") val provisionalStartTime: Boolean,
    @SerializedName("started") val started: Boolean?,
    @SerializedName("team_a") val teamA: Int,
    @SerializedName("team_a_score") val teamAScore: Int?,
    @SerializedName("team_h") val teamH: Int,
    @SerializedName("team_h_score") val teamHScore: Int?,
    @SerializedName("stats") val stats: List<FixtureStat>,
    @SerializedName("team_h_difficulty") val teamHDifficulty: Int,
    @SerializedName("team_a_difficulty") val teamADifficulty: Int,
    @SerializedName("pulse_id") val pulseId: Int
)

data class FixtureStat(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("a") val awayStats: List<StatDetail>,
    @SerializedName("h") val homeStats: List<StatDetail>
)

data class StatDetail(
    @SerializedName("value") val value: Int,
    @SerializedName("element") val element: Int
)