package com.mashu.mesunset.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mashu.mesunset.data.models.*
import com.mashu.mesunset.data.repository.MESunsetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(private val repository: MESunsetRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()
    
    private val _balance = MutableStateFlow<Balance?>(null)
    val balance: StateFlow<Balance?> = _balance.asStateFlow()
    
    private val _tieringInfo = MutableStateFlow<TieringInfo?>(null)
    val tieringInfo: StateFlow<TieringInfo?> = _tieringInfo.asStateFlow()
    
    init {
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        _currentUser.value = repository.getActiveUser()
        _currentUser.value?.let { user ->
            loadProfile(user)
        }
    }
    
    fun requestOtp(msisdn: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.requestOtp(msisdn)
            result.onSuccess { message ->
                _uiState.value = UiState.Success(message)
            }.onFailure { error ->
                _uiState.value = UiState.Error("Request OTP gagal: ${error.message}")
            }
        }
    }
    
    fun login(msisdn: String, otp: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.login(msisdn, otp)
            result.onSuccess { user ->
                _currentUser.value = user
                _uiState.value = UiState.Success("Login berhasil!")
                loadProfile(user)
            }.onFailure { error ->
                _uiState.value = UiState.Error("Login gagal: ${error.message}")
            }
        }
    }
    
    private fun loadProfile(user: User) {
        viewModelScope.launch {
            // Load balance
            val balanceResult = repository.getBalance(user)
            balanceResult.onSuccess { balance ->
                _balance.value = balance
                
                // Load tiering info if PREPAID
                if (user.subscriptionType == "PREPAID") {
                    val tieringResult = repository.getTieringInfo(user)
                    tieringResult.onSuccess { tiering ->
                        _tieringInfo.value = tiering
                        
                        _profile.value = Profile(
                            number = user.number,
                            subscriberId = user.subscriberId,
                            subscriptionType = user.subscriptionType,
                            balance = balance.remaining,
                            balanceExpiredAt = balance.expiredAt,
                            pointInfo = "Points: ${tiering.currentPoint} | Tier: ${tiering.tier}"
                        )
                    }.onFailure {
                        _profile.value = Profile(
                            number = user.number,
                            subscriberId = user.subscriberId,
                            subscriptionType = user.subscriptionType,
                            balance = balance.remaining,
                            balanceExpiredAt = balance.expiredAt,
                            pointInfo = "Points: N/A | Tier: N/A"
                        )
                    }
                } else {
                    _profile.value = Profile(
                        number = user.number,
                        subscriberId = user.subscriberId,
                        subscriptionType = user.subscriptionType,
                        balance = balance.remaining,
                        balanceExpiredAt = balance.expiredAt,
                        pointInfo = "Points: N/A | Tier: N/A"
                    )
                }
            }.onFailure { error ->
                _uiState.value = UiState.Error("Gagal memuat profil: ${error.message}")
            }
        }
    }
    
    fun refreshProfile() {
        _currentUser.value?.let { user ->
            loadProfile(user)
        }
    }
    
    fun logout() {
        _currentUser.value?.let { user ->
            repository.removeUser(user.number)
        }
        _currentUser.value = null
        _profile.value = null
        _balance.value = null
        _tieringInfo.value = null
        _uiState.value = UiState.Idle
    }
    
    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
}
