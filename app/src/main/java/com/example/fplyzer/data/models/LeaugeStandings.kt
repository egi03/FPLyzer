package com.example.fplyzer.data.models
import com.google.gson.annotations.SerializedName

data class LeagueStandingsResponse(
    @SerializedName("league") val league: League,
    @SerializedName("standings") val standings: Standings
)

data class League(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("created") val created: String,
    @SerializedName("closed") val closed: Boolean,
    @SerializedName("max_entries") val maxEntries: Int?,
    @SerializedName("league_type") val leagueType: String,
    @SerializedName("scoring") val scoring: String,
    @SerializedName("admin_entry") val adminEntry: Int?,
    @SerializedName("start_event") val startEvent: Int,
    @SerializedName("code_privacy") val codePrivacy: String,
    @SerializedName("has_cup") val hasCup: Boolean,
    @SerializedName("cup_league") val cupLeague: Int?,
    @SerializedName("rank") val rank: Int?
)

data class Standings(
    @SerializedName("has_next") val hasNext: Boolean,
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<StandingResult>
)

data class StandingResult(
    @SerializedName("id") val id: Int,
    @SerializedName("event_total") val eventTotal: Int,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("rank") val rank: Int,
    @SerializedName("last_rank") val lastRank: Int,
    @SerializedName("rank_sort") val rankSort: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("entry") val entry: Int,
    @SerializedName("entry_name") val entryName: String
) {
    val rankChange: Int
        get() = lastRank - rank
}