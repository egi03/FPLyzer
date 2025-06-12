package com.example.fplyzer.ui.screens.playerDetails

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.ElementType
import com.example.fplyzer.data.models.PlayerDetails
import com.example.fplyzer.data.models.Team
import com.example.fplyzer.data.repository.FplRepository
import kotlinx.coroutines.launch


data class PlayerDetailsUiState(
    val player: Element? = null,
    val team: Team? = null,
    val position: ElementType? = null,
    val teams: List<Team> = emptyList(),
    val playerDetails: PlayerDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0
)

class PlayerDetailsViewModel : ViewModel() {
    private val repository = FplRepository()

    private val _uiState = mutableStateOf(PlayerDetailsUiState())
    val uiState: State<PlayerDetailsUiState> = _uiState

    fun loadPlayerDetails(playerId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getBootstrapStatic().fold(
                onSuccess = { bootstrap ->
                    val player = bootstrap.elements.find { it.id == playerId }
                    val team = bootstrap.teams.find { it.id == player?.team }
                    val position = bootstrap.elementTypes.find { it.id == player?.elementType }

                    _uiState.value = _uiState.value.copy(
                        player = player,
                        team = team,
                        position = position,
                        teams = bootstrap.teams
                    )

                    repository.getPlayerDetails(playerId).fold(
                        onSuccess = { details ->
                            _uiState.value = _uiState.value.copy(
                                playerDetails = details,
                                isLoading = false
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                error = exception.message,
                                isLoading = false
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun setSelectedTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }
}