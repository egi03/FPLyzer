package com.example.fplyzer.data.models
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName

data class ManagerHistory(
    val current: List<GameweekHistory>,
    val past: List<PastSeason>,
    val chips: List<ChipPlay>
)

data class GameweekHistory(
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

data class PastSeason(
    @SerializedName("season_name") val seasonName: String,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("rank") val rank: Int
)

data class ChipPlay(
    @SerializedName("name") val name: String,
    @SerializedName("time") val time: String,
    @SerializedName("event") val event: Int
)