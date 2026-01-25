package kz.hashiroii.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kz.hashiroii.domain.usecase.auth.ObserveCurrentUserUseCase
import kz.hashiroii.domain.usecase.auth.SignOutUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUserUseCase()
            .onEach { user ->
                _uiState.value = ProfileUiState.Success(user = user)
            }
            .catch { e ->
                _uiState.value = ProfileUiState.Error(
                    message = e.message ?: "Unknown error"
                )
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> {
            }
            is ProfileIntent.SignIn -> {
            }
            is ProfileIntent.SignOut -> {
                signOut()
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            try {
                signOutUseCase()
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    message = e.message ?: "Failed to sign out"
                )
            }
        }
    }
}
