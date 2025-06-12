package com.example.fplyzer.data.models

import com.google.gson.annotations.SerializedName

data class PlayerDetails(
    @SerializedName("fixtures") val fixtures: List<PlayerFixture>,
    @SerializedName("history") val history: List<PlayerHistory>,
    @SerializedName("history_past") val historyPast: List<PlayerPastHistory>
)

data class PlayerFixture(
    @SerializedName("code") val code: Int,
    @SerializedName("team_h") val teamH: Int,
    @SerializedName("team_h_score") val teamHScore: Int?,
    @SerializedName("team_a") val teamA: Int,
    @SerializedName("team_a_score") val teamAScore: Int?,
    @SerializedName("event") val event: Int,
    @SerializedName("finished") val finished: Boolean,
    @SerializedName("minutes") val minutes: Int,
    @SerializedName("provisional_start_time") val provisionalStartTime: Boolean,
    @SerializedName("kickoff_time") val kickoffTime: String,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("is_home") val isHome: Boolean,
    @SerializedName("difficulty") val difficulty: Int
)

data class PlayerHistory(
    @SerializedName("element") val element: Int,
    @SerializedName("fixture") val fixture: Int,
    @SerializedName("opponent_team") val opponentTeam: Int,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("was_home") val wasHome: Boolean,
    @SerializedName("kickoff_time") val kickoffTime: String,
    @SerializedName("team_h_score") val teamHScore: Int?,
    @SerializedName("team_a_score") val teamAScore: Int?,
    @SerializedName("round") val round: Int,
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
    @SerializedName("value") val value: Int,
    @SerializedName("transfers_balance") val transfersBalance: Int,
    @SerializedName("selected") val selected: Int,
    @SerializedName("transfers_in") val transfersIn: Int,
    @SerializedName("transfers_out") val transfersOut: Int
)

data class PlayerPastHistory(
    @SerializedName("season_name") val seasonName: String,
    @SerializedName("element_code") val elementCode: Int,
    @SerializedName("start_cost") val startCost: Int,
    @SerializedName("end_cost") val endCost: Int,
    @SerializedName("total_points") val totalPoints: Int,
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
    @SerializedName("ict_index") val ictIndex: String
)