package com.example.fplyzer.data.models

import com.google.gson.annotations.SerializedName

data class LiveGameweek(
    @SerializedName("elements") val elements: List<LiveElement>
)

data class LiveElement(
    @SerializedName("id") val id: Int,
    @SerializedName("stats") val stats: LiveStats,
    @SerializedName("explain") val explain: List<Explain>
)

data class LiveStats(
    @SerializedName("minutes") val minutes: Int,
    @SerializedName("goals_scored") val goalsScored: Int,
    @SerializedName("assists") val assists: Int,
    @SerializedName("clean_sheets") val cleanSheets: Int,
    @SerializedName("goals_conceded") val goalsConceded: Int,
    @SerializedName("own_goals") val ownGoals: Int,
    @SerializedName("penalties_saved") val penaltiesSaved: Int,
    @SerializedName("penalties_missed") val penaltiesMissed: Int,
    @SerializedName("yellow_cards") val yellowCards: Int,
    @SerializedName("red_cards") val redCards: Int,
    @SerializedName("saves") val saves: Int,
    @SerializedName("bonus") val bonus: Int,
    @SerializedName("bps") val bps: Int,
    @SerializedName("influence") val influence: String,
    @SerializedName("creativity") val creativity: String,
    @SerializedName("threat") val threat: String,
    @SerializedName("ict_index") val ictIndex: String,
    @SerializedName("starts") val starts: Int,
    @SerializedName("expected_goals") val expectedGoals: String,
    @SerializedName("expected_assists") val expectedAssists: String,
    @SerializedName("expected_goal_involvements") val expectedGoalInvolvements: String,
    @SerializedName("expected_goals_conceded") val expectedGoalsConceded: String,
    @SerializedName("total_points") val totalPoints: Int
)

data class Explain(
    @SerializedName("fixture") val fixture: Int,
    @SerializedName("stats") val stats: List<ExplainStat>
)

data class ExplainStat(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("points") val points: Int,
    @SerializedName("value") val value: Int
)