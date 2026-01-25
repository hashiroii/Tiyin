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
    
    private fun cleanDomain(domain: String): String {
        return domain.trim()
            .removePrefix("http://")
            .removePrefix("https://")
            .removePrefix("www.")
            .lowercase()
    }
    
    override fun getLogoUrl(domain: String): Flow<String?> = flow {
        val cleanDomain = cleanDomain(domain)
        
        logoCache[cleanDomain]?.let { cachedUrl ->
            emit(cachedUrl)
            return@flow
        }
        
        val logoUrl = if (apiKey.isNotEmpty() && cleanDomain.isNotEmpty()) {
            "https://img.logo.dev/$cleanDomain?token=$apiKey&size=256&format=png"
        } else {
            null
        }
        
        logoUrl?.let { logoCache[cleanDomain] = it }
        emit(logoUrl)
    }
    
    override suspend fun prefetchLogos(domains: List<String>) {
        domains.forEach { domain ->
            val cleanDomain = cleanDomain(domain)
            if (!logoCache.containsKey(cleanDomain) && apiKey.isNotEmpty() && cleanDomain.isNotEmpty()) {
                val logoUrl = "https://img.logo.dev/$cleanDomain?token=$apiKey&size=256&format=png"
                logoCache[cleanDomain] = logoUrl
            }
        }
    }
}
