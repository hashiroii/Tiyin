package kz.hashiroii.profile

sealed interface ProfileIntent {
    data object LoadProfile : ProfileIntent
    data object SignIn : ProfileIntent
    data object SignOut : ProfileIntent
}
