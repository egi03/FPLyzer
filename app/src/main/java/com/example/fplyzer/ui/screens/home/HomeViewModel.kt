package com.example.fplyzer.ui.screens.home

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fplyzer.data.manager.FavouriteLeaguesManager
import com.example.fplyzer.data.models.FavouriteLeague
import kotlinx.coroutines.launch

data class HomeUiState(
    val leagueIdInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val favouriteLeagues: List<FavouriteLeague> = emptyList(),
    val isLoadingFavourites: Boolean = false
)

class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val _uiState = mutableStateOf(HomeUiState())
    val uiState: State<HomeUiState> = _uiState

    private val favouriteLeaguesManager = FavouriteLeaguesManager(application)

    init {
        loadFavouriteLeagues()
    }

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

    fun loadFavouriteLeagues() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingFavourites = true)
            try {
                val favourites = favouriteLeaguesManager.getFavouriteLeagues()
                _uiState.value = _uiState.value.copy(
                    favouriteLeagues = favourites,
                    isLoadingFavourites = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingFavourites = false,
                    error = "Failed to load favourite leagues"
                )
            }
        }
    }

    fun removeFavouriteLeague(leagueId: Int) {
        viewModelScope.launch {
            favouriteLeaguesManager.removeFavouriteLeague(leagueId)
            loadFavouriteLeagues()
        }
    }
}