package kz.hashiroii.domain.usecase.logo

import kotlinx.coroutines.flow.Flow
import kz.hashiroii.domain.repository.LogoRepository
import javax.inject.Inject

class GetLogoUrlUseCase @Inject constructor(
    private val logoRepository: LogoRepository
) {
    operator fun invoke(domain: String): Flow<String?> {
        return logoRepository.getLogoUrl(domain)
    }
}
