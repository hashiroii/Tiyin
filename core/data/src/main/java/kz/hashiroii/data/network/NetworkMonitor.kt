package kz.hashiroii.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor() {
    private val _isOnline = MutableStateFlow(true)
    val isOnline: Flow<Boolean> = _isOnline.asStateFlow()

    fun setOnlineStatus(isOnline: Boolean) {
        _isOnline.value = isOnline
    }
}
