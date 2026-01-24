package kz.hashiroii.data.datasource

import kz.hashiroii.data.model.SubscriptionEntity
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockSubscriptionDataSource @Inject constructor() {
    
    fun getMockSubscriptions(): List<SubscriptionEntity> {
        val today = LocalDate.now()
        return listOf(
            SubscriptionEntity(
                serviceName = "Spotify",
                serviceDomain = "spotify.com",
                cost = "$9.99",
                period = "MONTHLY",
                nextPaymentDate = today.plusDays(5),
                currentPaymentDate = today.minusDays(25),
                serviceType = "STREAMING",
                primaryColor = 0xFF1DB954,
                secondaryColor = 0xFF191414
            ),
            SubscriptionEntity(
                serviceName = "Netflix",
                serviceDomain = "netflix.com",
                cost = "$15.99",
                period = "MONTHLY",
                nextPaymentDate = today.plusDays(12),
                currentPaymentDate = today.minusDays(18),
                serviceType = "STREAMING",
                primaryColor = 0xFFE50914,
                secondaryColor = 0xFF000000
            ),
            SubscriptionEntity(
                serviceName = "Apple Music",
                serviceDomain = "apple.com",
                cost = "₸2990",
                period = "MONTHLY",
                nextPaymentDate = today.plusDays(8),
                currentPaymentDate = today.minusDays(22),
                serviceType = "STREAMING",
                primaryColor = 0xFFFA243C,
                secondaryColor = 0xFF000000
            ),
            SubscriptionEntity(
                serviceName = "YouTube Premium",
                serviceDomain = "youtube.com",
                cost = "₸1990",
                period = "MONTHLY",
                nextPaymentDate = today.plusDays(15),
                currentPaymentDate = today.minusDays(15),
                serviceType = "STREAMING",
                primaryColor = 0xFFFF0000,
                secondaryColor = 0xFF000000
            ),
            SubscriptionEntity(
                serviceName = "Adobe Creative Cloud",
                serviceDomain = "adobe.com",
                cost = "$52.99",
                period = "MONTHLY",
                nextPaymentDate = today.plusDays(20),
                currentPaymentDate = today.minusDays(10),
                serviceType = "SOFTWARE",
                primaryColor = 0xFFFF0000,
                secondaryColor = 0xFF000000
            )
        )
    }
}
