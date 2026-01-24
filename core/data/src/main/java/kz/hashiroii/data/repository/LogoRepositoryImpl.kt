package kz.hashiroii.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kz.hashiroii.domain.repository.LogoRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LogoRepositoryImpl @Inject constructor(
    @Named("logo_dev_api_key") private val apiKey: String
) : LogoRepository {
    
    private val logoCache = mutableMapOf<String, String>()
    
    override fun getLogoUrl(domain: String): Flow<String?> = flow {
        logoCache[domain]?.let { cachedUrl ->
            emit(cachedUrl)
            return@flow
        }
        
        val logoUrl = if (apiKey.isNotEmpty()) {
            "https://img.logo.dev/$domain?token=$apiKey&size=256&format=png"
        } else {
            null
        }
        
        logoUrl?.let { logoCache[domain] = it }
        emit(logoUrl)
    }
    
    override suspend fun prefetchLogos(domains: List<String>) {
        domains.forEach { domain ->
            if (!logoCache.containsKey(domain) && apiKey.isNotEmpty()) {
                val logoUrl = "https://img.logo.dev/$domain?token=$apiKey&size=256&format=png"
                logoCache[domain] = logoUrl
            }
        }
    }
}
