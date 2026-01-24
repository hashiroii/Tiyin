package kz.hashiroii.domain.usecase.logo

import kz.hashiroii.domain.repository.LogoRepository
import javax.inject.Inject

class PrefetchLogosUseCase @Inject constructor(
    private val logoRepository: LogoRepository
) {
    suspend operator fun invoke(domains: List<String>) {
        logoRepository.prefetchLogos(domains)
    }
}
