package com.detour.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.detour.app.data.model.Detour
import com.detour.app.data.model.DetourResponse
import com.detour.app.repository.DetourRepository
import com.detour.app.repository.Result
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: DetourResponse) : UiState()
    data class Error(val message: String) : UiState()
}

class DetourViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DetourRepository()

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _selectedDetour = MutableLiveData<Detour>()
    val selectedDetour: LiveData<Detour> = _selectedDetour

    fun planDetour(
        origin: String,
        destination: String,
        categories: List<String>,
        maxDetourMinutes: Int,
        userPreferences: String = ""
    ) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.planDetour(
                origin = origin,
                destination = destination,
                categories = categories,
                maxDetourMinutes = maxDetourMinutes,
                userPreferences = userPreferences
            )) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun selectDetour(detour: Detour) {
        _selectedDetour.value = detour
    }
}
