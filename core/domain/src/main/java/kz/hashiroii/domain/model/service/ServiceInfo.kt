package kz.hashiroii.domain.model.service

data class ServiceInfo(
    val name: String,
    val logoUrls: List<String> = emptyList(),
    val logoUrl: String? = null,
    val logoResId: Int = 0,
    val packageName: String? = null,
    val primaryColor: Long,
    val secondaryColor: Long,
    val serviceType: ServiceType
) {
    val effectiveLogoUrl: String?
        get() = logoUrls.firstOrNull() ?: logoUrl
}
