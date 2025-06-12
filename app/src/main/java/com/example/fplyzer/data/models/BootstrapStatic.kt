package com.example.fplyzer.data.models

import com.google.gson.annotations.SerializedName

data class BootstrapStatic(
    @SerializedName("events") val events: List<Event>,
    @SerializedName("game_settings") val gameSettings: GameSettings,
    @SerializedName("phases") val phases: List<Phase>,
    @SerializedName("teams") val teams: List<Team>,
    @SerializedName("total_players") val totalPlayers: Int,
    @SerializedName("elements") val elements: List<Element>,
    @SerializedName("element_stats") val elementStats: List<ElementStat>,
    @SerializedName("element_types") val elementTypes: List<ElementType>
)

data class Event(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("deadline_time") val deadlineTime: String,
    @SerializedName("average_entry_score") val averageEntryScore: Int,
    @SerializedName("finished") val finished: Boolean,
    @SerializedName("data_checked") val dataChecked: Boolean,
    @SerializedName("highest_scoring_entry") val highestScoringEntry: Int?,
    @SerializedName("deadline_time_epoch") val deadlineTimeEpoch: Long,
    @SerializedName("deadline_time_game_offset") val deadlineTimeGameOffset: Int,
    @SerializedName("highest_score") val highestScore: Int?,
    @SerializedName("is_previous") val isPrevious: Boolean,
    @SerializedName("is_current") val isCurrent: Boolean,
    @SerializedName("is_next") val isNext: Boolean,
    @SerializedName("cup_leagues_created") val cupLeaguesCreated: Boolean,
    @SerializedName("h2h_ko_matches_created") val h2hKoMatchesCreated: Boolean,
    @SerializedName("chip_plays") val chipPlays: List<ChipPlay>,
    @SerializedName("most_selected") val mostSelected: Int?,
    @SerializedName("most_transferred_in") val mostTransferredIn: Int?,
    @SerializedName("top_element") val topElement: Int?,
    @SerializedName("top_element_info") val topElementInfo: TopElementInfo?,
    @SerializedName("transfers_made") val transfersMade: Int,
    @SerializedName("most_captained") val mostCaptained: Int?,
    @SerializedName("most_vice_captained") val mostViceCaptained: Int?
)

data class GameSettings(
    @SerializedName("league_join_private_max") val leagueJoinPrivateMax: Int,
    @SerializedName("league_join_public_max") val leagueJoinPublicMax: Int,
    @SerializedName("league_max_size_public_classic") val leagueMaxSizePublicClassic: Int,
    @SerializedName("league_max_size_public_h2h") val leagueMaxSizePublicH2h: Int,
    @SerializedName("league_max_size_private_h2h") val leagueMaxSizePrivateH2h: Int,
    @SerializedName("league_max_ko_rounds_private_h2h") val leagueMaxKoRoundsPrivateH2h: Int,
    @SerializedName("league_prefix_public") val leaguePrefixPublic: String,
    @SerializedName("league_points_h2h_win") val leaguePointsH2hWin: Int,
    @SerializedName("league_points_h2h_lose") val leaguePointsH2hLose: Int,
    @SerializedName("league_points_h2h_draw") val leaguePointsH2hDraw: Int,
    @SerializedName("league_ko_first_instead_of_random") val leagueKoFirstInsteadOfRandom: Boolean,
    @SerializedName("cup_start_event_id") val cupStartEventId: Int?,
    @SerializedName("cup_stop_event_id") val cupStopEventId: Int?,
    @SerializedName("cup_qualifying_method") val cupQualifyingMethod: String?,
    @SerializedName("cup_type") val cupType: String?,
    @SerializedName("squad_squadplay") val squadSquadplay: Int,
    @SerializedName("squad_squadsize") val squadSquadsize: Int,
    @SerializedName("squad_team_limit") val squadTeamLimit: Int,
    @SerializedName("squad_total_spend") val squadTotalSpend: Int,
    @SerializedName("ui_currency_multiplier") val uiCurrencyMultiplier: Int,
    @SerializedName("ui_use_special_shirts") val uiUseSpecialShirts: Boolean,
    @SerializedName("ui_special_shirt_exclusions") val uiSpecialShirtExclusions: List<Any>,
    @SerializedName("stats_form_days") val statsFormDays: Int,
    @SerializedName("sys_vice_captain_enabled") val sysViceCaptainEnabled: Boolean,
    @SerializedName("transfers_cap") val transfersCap: Int,
    @SerializedName("transfers_sell_on_fee") val transfersSellOnFee: Double,
    @SerializedName("league_h2h_tiebreak_stats") val leagueH2hTiebreakStats: List<String>,
    @SerializedName("timezone") val timezone: String
)

data class Team(
    @SerializedName("code") val code: Int,
    @SerializedName("draw") val draw: Int,
    @SerializedName("form") val form: String?,
    @SerializedName("id") val id: Int,
    @SerializedName("loss") val loss: Int,
    @SerializedName("name") val name: String,
    @SerializedName("played") val played: Int,
    @SerializedName("points") val points: Int,
    @SerializedName("position") val position: Int,
    @SerializedName("short_name") val shortName: String,
    @SerializedName("strength") val strength: Int,
    @SerializedName("team_division") val teamDivision: Any?,
    @SerializedName("unavailable") val unavailable: Boolean,
    @SerializedName("win") val win: Int,
    @SerializedName("strength_overall_home") val strengthOverallHome: Int,
    @SerializedName("strength_overall_away") val strengthOverallAway: Int,
    @SerializedName("strength_attack_home") val strengthAttackHome: Int,
    @SerializedName("strength_attack_away") val strengthAttackAway: Int,
    @SerializedName("strength_defence_home") val strengthDefenceHome: Int,
    @SerializedName("strength_defence_away") val strengthDefenceAway: Int,
    @SerializedName("pulse_id") val pulseId: Int
)

data class Element(
    @SerializedName("chance_of_playing_next_round") val chanceOfPlayingNextRound: Int?,
    @SerializedName("chance_of_playing_this_round") val chanceOfPlayingThisRound: Int?,
    @SerializedName("code") val code: Int,
    @SerializedName("cost_change_event") val costChangeEvent: Int,
    @SerializedName("cost_change_event_fall") val costChangeEventFall: Int,
    @SerializedName("cost_change_start") val costChangeStart: Int,
    @SerializedName("cost_change_start_fall") val costChangeStartFall: Int,
    @SerializedName("dreamteam_count") val dreamteamCount: Int,
    @SerializedName("element_type") val elementType: Int,
    @SerializedName("ep_next") val epNext: String?,
    @SerializedName("ep_this") val epThis: String?,
    @SerializedName("event_points") val eventPoints: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("form") val form: String,
    @SerializedName("id") val id: Int,
    @SerializedName("in_dreamteam") val inDreamteam: Boolean,
    @SerializedName("news") val news: String,
    @SerializedName("news_added") val newsAdded: String?,
    @SerializedName("now_cost") val nowCost: Int,
    @SerializedName("photo") val photo: String,
    @SerializedName("points_per_game") val pointsPerGame: String,
    @SerializedName("second_name") val secondName: String,
    @SerializedName("selected_by_percent") val selectedByPercent: String,
    @SerializedName("special") val special: Boolean,
    @SerializedName("squad_number") val squadNumber: Int?,
    @SerializedName("status") val status: String,
    @SerializedName("team") val team: Int,
    @SerializedName("team_code") val teamCode: Int,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("transfers_in") val transfersIn: Int,
    @SerializedName("transfers_in_event") val transfersInEvent: Int,
    @SerializedName("transfers_out") val transfersOut: Int,
    @SerializedName("transfers_out_event") val transfersOutEvent: Int,
    @SerializedName("value_form") val valueForm: String,
    @SerializedName("value_season") val valueSeason: String,
    @SerializedName("web_name") val webName: String,
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
    @SerializedName("influence_rank") val influenceRank: Int,
    @SerializedName("influence_rank_type") val influenceRankType: Int,
    @SerializedName("creativity_rank") val creativityRank: Int,
    @SerializedName("creativity_rank_type") val creativityRankType: Int,
    @SerializedName("threat_rank") val threatRank: Int,
    @SerializedName("threat_rank_type") val threatRankType: Int,
    @SerializedName("ict_index_rank") val ictIndexRank: Int,
    @SerializedName("ict_index_rank_type") val ictIndexRankType: Int,
    @SerializedName("corners_and_indirect_freekicks_order") val cornersAndIndirectFreekicksOrder: Int?,
    @SerializedName("corners_and_indirect_freekicks_text") val cornersAndIndirectFreekicksText: String,
    @SerializedName("direct_freekicks_order") val directFreekicksOrder: Int?,
    @SerializedName("direct_freekicks_text") val directFreekicksText: String,
    @SerializedName("penalties_order") val penaltiesOrder: Int?,
    @SerializedName("penalties_text") val penaltiesText: String,
    @SerializedName("expected_goals_per_90") val expectedGoalsPer90: Double,
    @SerializedName("saves_per_90") val savesPer90: Double,
    @SerializedName("expected_assists_per_90") val expectedAssistsPer90: Double,
    @SerializedName("expected_goal_involvements_per_90") val expectedGoalInvolvementsPer90: Double,
    @SerializedName("expected_goals_conceded_per_90") val expectedGoalsConcededPer90: Double,
    @SerializedName("goals_conceded_per_90") val goalsConcededPer90: Double,
    @SerializedName("now_cost_rank") val nowCostRank: Int,
    @SerializedName("now_cost_rank_type") val nowCostRankType: Int,
    @SerializedName("form_rank") val formRank: Int,
    @SerializedName("form_rank_type") val formRankType: Int,
    @SerializedName("points_per_game_rank") val pointsPerGameRank: Int,
    @SerializedName("points_per_game_rank_type") val pointsPerGameRankType: Int,
    @SerializedName("selected_rank") val selectedRank: Int,
    @SerializedName("selected_rank_type") val selectedRankType: Int,
    @SerializedName("starts_per_90") val startsPer90: Double,
    @SerializedName("clean_sheets_per_90") val cleanSheetsPer90: Double
) {
    val displayName: String
        get() = webName

    val displayPrice: String
        get() = "£${nowCost / 10.0}m"

    val priceChange: String
        get() = when {
            costChangeEvent > 0 -> "+${costChangeEvent / 10.0}"
            costChangeEvent < 0 -> "${costChangeEvent / 10.0}"
            else -> "0.0"
        }
}

data class ElementType(
    @SerializedName("id") val id: Int,
    @SerializedName("plural_name") val pluralName: String,
    @SerializedName("plural_name_short") val pluralNameShort: String,
    @SerializedName("singular_name") val singularName: String,
    @SerializedName("singular_name_short") val singularNameShort: String,
    @SerializedName("squad_select") val squadSelect: Int,
    @SerializedName("squad_min_play") val squadMinPlay: Int,
    @SerializedName("squad_max_play") val squadMaxPlay: Int,
    @SerializedName("ui_shirt_specific") val uiShirtSpecific: Boolean,
    @SerializedName("sub_positions_locked") val subPositionsLocked: List<Int>,
    @SerializedName("element_count") val elementCount: Int
)

data class Phase(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("start_event") val startEvent: Int,
    @SerializedName("stop_event") val stopEvent: Int
)

data class ElementStat(
    @SerializedName("label") val label: String,
    @SerializedName("name") val name: String
)

data class TopElementInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("points") val points: Int
)