package kz.hashiroii.domain.repository

import kz.hashiroii.domain.model.auth.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    val isAuthenticated: Flow<Boolean>
    
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut()
    suspend fun getCurrentUser(): User?
}
