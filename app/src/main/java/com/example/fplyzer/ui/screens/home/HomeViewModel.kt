package com.example.fplyzer.ui.screens.home

import android.view.View
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.fplyzer.data.models.BootstrapStatic


data class HomeUiState(
    val managerIdInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel: ViewModel() {
    private val _uiState = mutableStateOf(HomeUiState())
    val uiState: State<HomeUiState> = _uiState

    fun updateManagerId(id: String){
        if (id.all { it.isDigit() }){
            _uiState.value = _uiState.value.copy(managerIdInput = id)
        }
    }

    fun clearError(){
        _uiState.value = _uiState.value.copy(error = null)
    }
}