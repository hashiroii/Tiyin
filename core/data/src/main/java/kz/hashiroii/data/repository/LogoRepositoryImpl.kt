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

    override fun getLogoUrl(domain: String): String? {
        val url = if (apiKey.isNotEmpty() && domain.isNotEmpty()) {
            "https://img.logo.dev/$domain?token=$apiKey&size=256&format=png"
        } else null
        android.util.Log.d("LogoRepo", "domain=$domain apiKey=${apiKey.take(6)}... url=$url")
        return url
    }
}
