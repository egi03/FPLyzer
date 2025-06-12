package com.example.fplyzer.data.models

import com.google.gson.annotations.SerializedName

data class BootstrapStatic(
    val events: List<Event>,
    @SerializedName("game_settings") val gameSettings: GameSettings,
    val phases: List<Phase>,
    val teams: List<Team>,
    @SerializedName("total_players") val totalPlayers: Int,
    val elements: List<Player>,
    @SerializedName("element_stats") val elementStats: List<ElementStat>,
    @SerializedName("element_types") val elementTypes: List<ElementType>
)

data class Event(
    val id: Int,
    val name: String,
    @SerializedName("deadline_time") val deadlineTime: String,
    @SerializedName("average_entry_score") val averageEntryScore: Int,
    @SerializedName("finished") val isFinished: Boolean,
    @SerializedName("is_current") val isCurrent: Boolean,
    @SerializedName("is_next") val isNext: Boolean
)

data class GameSettings(
    @SerializedName("squad_squadplay") val squadPlay: Int,
    @SerializedName("squad_squadsize") val squadSize: Int,
    @SerializedName("squad_team_limit") val teamLimit: Int
)

data class Phase(
    val id: Int,
    val name: String,
    @SerializedName("start_event") val startEvent: Int,
    @SerializedName("stop_event") val stopEvent: Int
)

data class Team(
    val id: Int,
    val name: String,
    @SerializedName("short_name") val shortName: String,
    val strength: Int,
    @SerializedName("strength_overall_home") val strengthOverallHome: Int,
    @SerializedName("strength_overall_away") val strengthOverallAway: Int
)

data class Player(
    val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("second_name") val secondName: String,
    @SerializedName("web_name") val webName: String,
    @SerializedName("team") val teamId: Int,
    @SerializedName("element_type") val positionId: Int,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("now_cost") val cost: Int,
    @SerializedName("selected_by_percent") val selectedByPercent: String,
    @SerializedName("goals_scored") val goalsScored: Int,
    @SerializedName("assists") val assists: Int
) {
    val fullName: String
        get() = "$firstName $secondName"
}

data class ElementStat(
    val name: String,
    val label: String
)

data class ElementType(
    val id: Int,
    @SerializedName("plural_name") val pluralName: String,
    @SerializedName("singular_name") val singularName: String
)