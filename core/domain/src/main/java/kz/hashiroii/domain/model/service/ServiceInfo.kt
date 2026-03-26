package kz.hashiroii.domain.model.service

data class ServiceInfo(
    val name: String,
    val domain: String,
    val primaryColor: Long,
    val secondaryColor: Long,
    val serviceType: ServiceType,
)
