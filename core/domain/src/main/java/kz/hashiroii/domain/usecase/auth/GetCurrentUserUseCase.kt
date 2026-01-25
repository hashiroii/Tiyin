package kz.hashiroii.domain.usecase.auth

import kz.hashiroii.domain.model.auth.User
import kz.hashiroii.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}
