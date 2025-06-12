package com.example.fplyzer.data.models

import com.google.gson.annotations.SerializedName

data class ManagerTeam(
    @SerializedName("active_chip") val activeChip: String?,
    @SerializedName("automatic_subs") val automaticSubs: List<AutomaticSub>,
    @SerializedName("entry_history") val entryHistory: EntryHistory,
    @SerializedName("picks") val picks: List<Pick>
)

data class AutomaticSub(
    @SerializedName("entry") val entry: Int,
    @SerializedName("element_in") val elementIn: Int,
    @SerializedName("element_out") val elementOut: Int,
    @SerializedName("event") val event: Int
)

data class EntryHistory(
    @SerializedName("event") val event: Int,
    @SerializedName("points") val points: Int,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("rank") val rank: Int,
    @SerializedName("rank_sort") val rankSort: Int,
    @SerializedName("overall_rank") val overallRank: Int,
    @SerializedName("bank") val bank: Int,
    @SerializedName("value") val value: Int,
    @SerializedName("event_transfers") val eventTransfers: Int,
    @SerializedName("event_transfers_cost") val eventTransfersCost: Int,
    @SerializedName("points_on_bench") val pointsOnBench: Int
)

data class Pick(
    @SerializedName("element") val element: Int,
    @SerializedName("position") val position: Int,
    @SerializedName("multiplier") val multiplier: Int,
    @SerializedName("is_captain") val isCaptain: Boolean,
    @SerializedName("is_vice_captain") val isViceCaptain: Boolean
)