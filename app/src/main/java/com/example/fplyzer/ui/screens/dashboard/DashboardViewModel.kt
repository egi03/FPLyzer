package com.example.fplyzer.ui.screens.dashboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import com.example.fplyzer.data.models.BootstrapStatic
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.Event
import com.example.fplyzer.data.repository.FplRepository
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


data class DashboardUiState(
    val bootstrapData: BootstrapStatic? = null,
    val currentEvent: Event? = null,
    val topPlayers: List<Element> = emptyList(),
    val mostTransferredIn: List<Element> = emptyList(),
    val mostTransferredOut: List<Element> = emptyList(),
    val dreamTeam: List<Element> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel : ViewModel() {
    private val repository = FplRepository()

    private val _uiState = mutableStateOf(DashboardUiState())
    val uiState: State<DashboardUiState> = _uiState

    init{
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getBootstrapStatic().fold(
                onSuccess = {data ->
                    val currentEvent = data.events.find { it.isCurrent }
                    val topPlayers = data.elements.sortedByDescending { it.eventPoints }.take(10)
                    val mostTransferredIn = data.elements.sortedByDescending { it.transfersInEvent }.take(10)
                    val mostTransferredOut = data.elements.sortedByDescending { it.transfersOutEvent }.take(10)
                    val dreamTeam = data.elements.filter { it.inDreamteam }

                    _uiState.value = _uiState.value.copy(
                        bootstrapData = data,
                        currentEvent = currentEvent,
                        topPlayers = topPlayers,
                        mostTransferredIn = mostTransferredIn,
                        mostTransferredOut = mostTransferredOut,
                        dreamTeam = dreamTeam,
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
}