package kz.hashiroii.domain.model.auth

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?
)
