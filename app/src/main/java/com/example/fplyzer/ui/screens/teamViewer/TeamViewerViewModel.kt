package com.example.fplyzer.ui.screens.teamViewer

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fplyzer.data.models.Element
import com.example.fplyzer.data.models.Event
import com.example.fplyzer.data.models.LiveStats
import com.example.fplyzer.data.models.Manager
import com.example.fplyzer.data.models.ManagerTeam
import com.example.fplyzer.data.repository.FplRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class TeamViewerUiState(
    val managerId: Int = 0,
    val managerInfo: Manager? = null,
    val managerTeam: ManagerTeam? = null,
    val players: Map<Int, Element> = emptyMap(),
    val liveData: Map<Int, LiveStats> = emptyMap(),
    val currentEvent: Event? = null,
    val totalLivePoints: Int = 0,
    val captainPoints: Int = 0,
    val benchPoints: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLiveDataAvailable: Boolean = true
)

class TeamViewerViewModel : ViewModel() {
    private val repository = FplRepository()

    private val _uiState = mutableStateOf(TeamViewerUiState())
    val uiState: State<TeamViewerUiState> = _uiState

    fun loadTeamData(managerId: Int){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                managerId = managerId,
                isLoading = true,
                error = null
            )

            val bootstrapDeferred = async { repository.getBootstrapStatic() }
            val managerDeferred = async { repository.getManagerInfo(managerId) }

            val bootstrapResult = bootstrapDeferred.await()
            val managerResult = managerDeferred.await()

            if (bootstrapResult.isFailure || managerResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = "Data loading failed",
                    isLoading = false
                )
                return@launch
            }

            val bootstrap = bootstrapResult.getOrNull()!!
            val manager = managerResult.getOrNull()!!
            val targetEvent = bootstrap.events.find { it.isCurrent } ?: bootstrap.events.find { it.id == 38 }

            _uiState.value = _uiState.value.copy(
                managerInfo = manager,
                currentEvent = targetEvent,
                players = bootstrap.elements.associateBy { it.id }
            )

            targetEvent?.let { event ->
                loadGameweekTeam(managerId, event.id)
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    error = "No current event available",
                    isLoading = false,
                    isLiveDataAvailable = false
                )
            }
        }
    }

    private suspend fun loadGameweekTeam(managerId: Int, eventId: Int){
        val teamResult = repository.getManagerTeam(managerId, eventId)
        val liveResult = repository.getLiveGameweekData(eventId)

        if (teamResult.isSuccess) {
            val team = teamResult.getOrNull()!!
            _uiState.value = _uiState.value.copy(managerTeam = team)

            if (liveResult.isSuccess) {
                val liveData = liveResult.getOrNull()!!
                val liveStats = liveData.elements.associateBy ({ it.id }, { it.stats })

                var totalPoints = 0
                var captainPoints = 0
                var benchPoints = 0

                team.picks.forEach { pick->
                    val playerLiveStats = liveStats[pick.element]
                    val points = (playerLiveStats?.totalPoints ?: 0) * pick.multiplier

                    if(pick.position <= 11){
                        totalPoints += points
                        if (pick.isCaptain){
                            captainPoints = points
                        }
                    } else {
                        benchPoints += playerLiveStats?.totalPoints ?: 0
                    }
                }

                _uiState.value = _uiState.value.copy(
                    liveData = liveStats,
                    totalLivePoints = totalPoints,
                    captainPoints = captainPoints,
                    benchPoints = benchPoints,
                    isLoading = false,
                    isLiveDataAvailable = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    liveData = emptyMap(),
                    totalLivePoints = 0,
                    captainPoints = 0,
                    benchPoints = 0,
                    isLoading = false,
                    isLiveDataAvailable = false,
                    error = null
                )
            }
        } else {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load team data for gameweek $eventId",
                isLoading = false
            )
        }
    }
}