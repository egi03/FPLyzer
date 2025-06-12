package com.example.fplyzer.data.models

import com.google.gson.annotations.SerializedName

data class ManagerTeam(
    val picks: List<Pick>,
    @SerializedName("entry_history") val entryHistory: EntryHistory,
    @SerializedName("automatic_subs") val automaticSubs: List<AutomaticSub>,
    @SerializedName("active_chip") val activeChip: String?
)

data class Pick(
    @SerializedName("element") val playerId: Int,
    val position: Int, // 1-15 (1-11 for starting XI, 12-15 for bench)
    val multiplier: Int, // 1 for regular, 2 for captain, 3 for triple captain
    @SerializedName("is_captain") val isCaptain: Boolean,
    @SerializedName("is_vice_captain") val isViceCaptain: Boolean
)

data class EntryHistory(
    val event: Int,
    val points: Int,
    @SerializedName("total_points") val totalPoints: Int,
    val rank: Int,
    @SerializedName("rank_sort") val rankSort: Int,
    @SerializedName("overall_rank") val overallRank: Int,
    val bank: Int,
    val value: Int,
    @SerializedName("event_transfers") val transfers: Int,
    @SerializedName("event_transfers_cost") val transfersCost: Int,
    @SerializedName("points_on_bench") val benchPoints: Int
)

data class AutomaticSub(
    @SerializedName("entry") val managerId: Int,
    @SerializedName("element_in") val playerInId: Int,
    @SerializedName("element_out") val playerOutId: Int,
    val event: Int
)