package com.example.fplyzer.data.models
import com.google.gson.annotations.SerializedName

data class Manager(
    @SerializedName("id") val id: Int,
    @SerializedName("joined_time") val joinedTime: String,
    @SerializedName("started_event") val startedEvent: Int,
    @SerializedName("favourite_team") val favouriteTeam: Int,
    @SerializedName("player_first_name") val playerFirstName: String,
    @SerializedName("player_last_name") val playerLastName: String,
    @SerializedName("player_region_id") val playerRegionId: Int,
    @SerializedName("player_region_name") val playerRegionName: String,
    @SerializedName("player_region_iso_code_short") val playerRegionIsoCodeShort: String,
    @SerializedName("player_region_iso_code_long") val playerRegionIsoCodeLong: String,
    @SerializedName("summary_overall_points") val summaryOverallPoints: Int,
    @SerializedName("summary_overall_rank") val summaryOverallRank: Int,
    @SerializedName("summary_event_points") val summaryEventPoints: Int,
    @SerializedName("summary_event_rank") val summaryEventRank: Int,
    @SerializedName("current_event") val currentEvent: Int,
    @SerializedName("leagues") val leagues: Leagues,
    @SerializedName("name") val name: String,
    @SerializedName("name_change_blocked") val nameChangeBlocked: Boolean,
    @SerializedName("kit") val kit: String?,
    @SerializedName("last_deadline_bank") val lastDeadlineBank: Int,
    @SerializedName("last_deadline_value") val lastDeadlineValue: Int,
    @SerializedName("last_deadline_total_transfers") val lastDeadlineTotalTransfers: Int
) {
    val fullName: String
        get() = "$playerFirstName $playerLastName"
}

data class Leagues(
    @SerializedName("classic") val classic: List<ClassicLeague>,
    @SerializedName("h2h") val h2h: List<Any>,
    @SerializedName("cup") val cup: Cup?
)

data class ClassicLeague(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("short_name") val shortName: String?,
    @SerializedName("created") val created: String,
    @SerializedName("closed") val closed: Boolean,
    @SerializedName("rank") val rank: Int?,
    @SerializedName("max_entries") val maxEntries: Int?,
    @SerializedName("league_type") val leagueType: String,
    @SerializedName("scoring") val scoring: String,
    @SerializedName("admin_entry") val adminEntry: Int?,
    @SerializedName("start_event") val startEvent: Int,
    @SerializedName("entry_can_leave") val entryCanLeave: Boolean,
    @SerializedName("entry_can_admin") val entryCanAdmin: Boolean,
    @SerializedName("entry_can_invite") val entryCanInvite: Boolean,
    @SerializedName("has_cup") val hasCup: Boolean,
    @SerializedName("cup_league") val cupLeague: Int?,
    @SerializedName("cup_qualified") val cupQualified: Boolean?
)

data class Cup(
    @SerializedName("matches_played") val matchesPlayed: Int,
    @SerializedName("matches_won") val matchesWon: Int,
    @SerializedName("matches_drawn") val matchesDrawn: Int,
    @SerializedName("matches_lost") val matchesLost: Int
)