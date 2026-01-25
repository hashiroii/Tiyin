package kz.hashiroii.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kz.hashiroii.domain.model.auth.User
import kz.hashiroii.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    
    private val _currentUser = MutableStateFlow<User?>(firebaseAuth.currentUser?.toDomain())
    
    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser?.toDomain()
        }
    }
    
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()
    
    override val isAuthenticated: Flow<Boolean> = currentUser.map { it != null }
    
    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user?.toDomain()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to get user information"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
    
    override suspend fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toDomain()
    }
    
    private fun FirebaseUser.toDomain(): User {
        return User(
            id = uid,
            email = email ?: "",
            displayName = displayName,
            photoUrl = photoUrl?.toString()
        )
    }
}
