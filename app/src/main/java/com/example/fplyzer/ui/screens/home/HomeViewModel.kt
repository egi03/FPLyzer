package com.example.fplyzer.ui.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class HomeUiState(
    val leagueIdInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel: ViewModel() {
    private val _uiState = mutableStateOf(HomeUiState())
    val uiState: State<HomeUiState> = _uiState

    fun updateLeagueId(id: String){
        if (id.all { it.isDigit() } || id.isEmpty()){
            _uiState.value = _uiState.value.copy(
                leagueIdInput = id,
                error = null
            )
        }
    }

    fun clearError(){
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    fun setError(error: String) {
        _uiState.value = _uiState.value.copy(
            error = error,
            isLoading = false
        )
    }
}