package kz.hashiroii.domain.usecase.auth

import kz.hashiroii.domain.model.auth.User
import kz.hashiroii.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return authRepository.signInWithGoogle(idToken)
    }
}
