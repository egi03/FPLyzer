package com.example.fplyzer.data.models.statistics

data class ChipAnalysis(
    val bestWildcards: List<ChipPerformance>,
    val bestBenchBoosts: List<ChipPerformance>,
    val bestTripleCaptains: List<ChipPerformance>,
    val bestFreeHits: List<ChipPerformance>,
    val worstChips: List<ChipPerformance>,
    val unusedChips: Map<String, List<Int>>
)
