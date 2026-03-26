package kz.hashiroii.domain.repository

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

interface LogoRepository {
    fun getLogoUrl(domain: String): String?
}
