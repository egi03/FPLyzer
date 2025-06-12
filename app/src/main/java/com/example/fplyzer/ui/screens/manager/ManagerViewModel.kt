package com.example.fplyzer.ui.screens.manager

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fplyzer.data.models.Manager
import com.example.fplyzer.data.models.ManagerHistory
import com.example.fplyzer.data.repository.FplRepository
import kotlinx.coroutines.launch

data class ManagerUiState(
    val manager: Manager? = null,
    val history: ManagerHistory? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0
)

class ManagerViewModel : ViewModel() {
    private val repository = FplRepository()

    private val _uiState = mutableStateOf(ManagerUiState())
    val uiState: State<ManagerUiState> = _uiState

    fun loadManagerData(managerId: Int){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getManagerInfo(managerId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(manager = it)
                },
                onFailure = {exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message,
                        isLoading = false
                    )
                    return@launch
                }
            )

            repository.getManagerHistory(managerId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(history = it, isLoading = false)
                },
                onFailure = {exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }
}