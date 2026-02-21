package kz.hashiroii.auth

sealed interface AuthIntent {
    data class SignInWithGoogle(val idToken: String) : AuthIntent
}
