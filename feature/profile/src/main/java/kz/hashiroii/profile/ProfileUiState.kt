package kz.hashiroii.profile

import kz.hashiroii.domain.model.auth.User

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    
    data class Success(
        val user: User?
    ) : ProfileUiState
    
    data class Error(
        val message: String
    ) : ProfileUiState
}
