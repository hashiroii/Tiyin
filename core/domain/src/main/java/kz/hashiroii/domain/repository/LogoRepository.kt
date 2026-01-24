package kz.hashiroii.domain.repository

import kotlinx.coroutines.flow.Flow

interface LogoRepository {
    fun getLogoUrl(domain: String): Flow<String?>
    suspend fun prefetchLogos(domains: List<String>)
}
