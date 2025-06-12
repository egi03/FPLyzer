package com.example.fplyzer.ui.screens.league

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fplyzer.data.models.LeagueStandingsResponse
import com.example.fplyzer.data.repository.FplRepository
import kotlinx.coroutines.launch

data class LeagueUiState(
    val standings: LeagueStandingsResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1
)

class LeagueViewModel : ViewModel(){
    private val repository = FplRepository()

    private val _uiState = mutableStateOf(LeagueUiState())
    val uiState: State<LeagueUiState> = _uiState

    fun loadLeagueStandings(leagueId: Int, page: Int = 1){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getLeagueStandings(leagueId, page).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        standings = it,
                        isLoading = false,
                        currentPage = page
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
}