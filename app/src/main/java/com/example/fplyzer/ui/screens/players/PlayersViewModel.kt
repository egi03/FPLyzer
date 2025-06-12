package com.example.fplyzer.ui.screens.players

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.ElementType
import com.example.fplyzer.data.models.Team
import com.example.fplyzer.data.repository.FplRepository
import kotlinx.coroutines.launch
import java.util.logging.Filter

data class PlayersUiState(
    val allPlayers: List<Element> = emptyList(),
    val filteredPlayers: List<Element> = emptyList(),
    val teams: List<Team> = emptyList(),
    val positions: List<ElementType> = emptyList(),
    val searchQuery: String = "",
    val selectedPosition: Int? = null,
    val selectedTeam: Int? = null,
    val sortBy: SortBy = SortBy.TOTAL_POINTS,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class SortBy{
    TOTAL_POINTS,
    FORM,
    PRICE,
    SELECTED_BY,
    TRANSFERS_IN,
    VALUE
}

class PlayersViewModel : ViewModel() {
    private val repository = FplRepository()

    private val _uiState = mutableStateOf(PlayersUiState())
    val uiState: State<PlayersUiState> = _uiState

    init {
        loadPlayers()
    }

    fun loadPlayers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getBootstrapStatic().fold(
                onSuccess = { data->
                    _uiState.value = _uiState.value.copy(
                        allPlayers = data.elements,
                        filteredPlayers = data.elements,
                        teams = data.teams,
                        positions = data.elementTypes,
                        isLoading = false
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        error = it.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun updateSearchQuery(query: String){
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun selectPosition(positionId: Int?){
        _uiState.value = _uiState.value.copy(selectedPosition = positionId)
        applyFilters()
    }

    fun selectTeam(teamId: Int?){
        _uiState.value = _uiState.value.copy(selectedTeam = teamId)
        applyFilters()
    }

    fun updateSortBy(sortBy: SortBy){
        _uiState.value = _uiState.value.copy(sortBy = sortBy)
        applyFilters()
    }

    private fun applyFilters(){
        val state = _uiState.value
        var filtered = state.allPlayers

        if (state.searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.webName.contains(state.searchQuery, ignoreCase = true) ||
                it.firstName.contains(state.searchQuery, ignoreCase = true) ||
                it.secondName.contains(state.searchQuery, ignoreCase = true)
            }
        }

        state.selectedPosition?.let {position ->
            filtered = filtered.filter { it.elementType == position }
        }

        state.selectedTeam?.let { team ->
            filtered = filtered.filter { it.team == team }
        }

        filtered = when(state.sortBy) {
            SortBy.TOTAL_POINTS -> filtered.sortedByDescending { it.totalPoints }
            SortBy.FORM -> filtered.sortedByDescending { it.form.toFloatOrNull() ?: 0.0f }
            SortBy.PRICE -> filtered.sortedByDescending { it.nowCost }
            SortBy.SELECTED_BY -> filtered.sortedByDescending { it.selectedByPercent.toFloatOrNull() ?: 0.0f }
            SortBy.TRANSFERS_IN -> filtered.sortedByDescending { it.transfersInEvent }
            SortBy.VALUE -> filtered.sortedByDescending { it.valueSeason.toFloatOrNull() ?: 0.0f }
        }

        _uiState.value = _uiState.value.copy(filteredPlayers = filtered)
    }
}