package com.example.fplyzer.data.models.statistics

data class TransferAnalysis(
    val managerId: Int,
    val bestTransfers: List<TransferRecord>,
    val worstTransfers: List<TransferRecord>,
    val netTransferValue: Int
)