
sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object SigningIn : AuthUiState
    data object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}