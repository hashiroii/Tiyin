package kz.hashiroii.data.repository

import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kz.hashiroii.data.service.NotificationListener
import kz.hashiroii.data.service.ServiceRecognizer
import kz.hashiroii.domain.model.service.ServiceInfo
import kz.hashiroii.domain.model.service.ServiceType
import kz.hashiroii.domain.model.service.Subscription
import kz.hashiroii.domain.model.service.SubscriptionPeriod
import kz.hashiroii.domain.repository.NotificationRepository
import java.time.LocalDate
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val serviceRecognizer: ServiceRecognizer
) : NotificationRepository {

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    private val subscriptionsStateFlow: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    private val costPattern = Pattern.compile("(\\d+[.,]?\\d*)\\s*([₸₽\\$€£]|тенге|рубл|dollar|euro)", Pattern.CASE_INSENSITIVE)
    private val datePattern = Pattern.compile("(\\d{1,2})[./-](\\d{1,2})[./-](\\d{2,4})", Pattern.CASE_INSENSITIVE)

    init {
        setupNotificationListener()
        loadMockData()
    }

    private fun loadMockData() {
        val mockSubscriptions = listOf(
            Subscription(
                serviceInfo = ServiceInfo(
                    name = "Spotify",
                    logoResId = 0,
                    primaryColor = 0xFF1DB954,
                    secondaryColor = 0xFF191414,
                    serviceType = ServiceType.STREAMING
                ),
                cost = "$9.99",
                period = SubscriptionPeriod.MONTHLY,
                nextPaymentDate = LocalDate.now().plusDays(5),
                currentPaymentDate = LocalDate.now().minusDays(25)
            ),
            Subscription(
                serviceInfo = ServiceInfo(
                    name = "Netflix",
                    logoResId = 0,
                    primaryColor = 0xFFE50914,
                    secondaryColor = 0xFF000000,
                    serviceType = ServiceType.STREAMING
                ),
                cost = "$15.99",
                period = SubscriptionPeriod.MONTHLY,
                nextPaymentDate = LocalDate.now().plusDays(12),
                currentPaymentDate = LocalDate.now().minusDays(18)
            ),
            Subscription(
                serviceInfo = ServiceInfo(
                    name = "Apple Music",
                    logoResId = 0,
                    primaryColor = 0xFFFA243C,
                    secondaryColor = 0xFF000000,
                    serviceType = ServiceType.STREAMING
                ),
                cost = "₸2990",
                period = SubscriptionPeriod.MONTHLY,
                nextPaymentDate = LocalDate.now().plusDays(8),
                currentPaymentDate = LocalDate.now().minusDays(22)
            ),
            Subscription(
                serviceInfo = ServiceInfo(
                    name = "YouTube Premium",
                    logoResId = 0,
                    primaryColor = 0xFFFF0000,
                    secondaryColor = 0xFF000000,
                    serviceType = ServiceType.STREAMING
                ),
                cost = "₸1990",
                period = SubscriptionPeriod.MONTHLY,
                nextPaymentDate = LocalDate.now().plusDays(15),
                currentPaymentDate = LocalDate.now().minusDays(15)
            ),
            Subscription(
                serviceInfo = ServiceInfo(
                    name = "Adobe Creative Cloud",
                    logoResId = 0,
                    primaryColor = 0xFFFF0000,
                    secondaryColor = 0xFF000000,
                    serviceType = ServiceType.SOFTWARE
                ),
                cost = "$52.99",
                period = SubscriptionPeriod.MONTHLY,
                nextPaymentDate = LocalDate.now().plusDays(20),
                currentPaymentDate = LocalDate.now().minusDays(10)
            )
        )
        _subscriptions.value = mockSubscriptions
    }

    private fun setupNotificationListener() {
        NotificationListener.notificationCallback = { notification ->
            processNotification(notification)
        }
    }

    private fun processNotification(notification: StatusBarNotification) {
        val title = notification.notification.extras.getCharSequence("android.title")?.toString()
        val text = notification.notification.extras.getCharSequence("android.text")?.toString()
        val packageName = notification.packageName

        val isBankPackage = serviceRecognizer.isBankPackage(packageName)
        
        if (isBankPackage) {
            val paymentData = serviceRecognizer.extractPaymentData(title, text)
            if (paymentData == null || !paymentData.isRecurring) {
                return
            }
        }

        val serviceInfo = serviceRecognizer.recognizeService(packageName, title, text)
            ?: return

        val paymentData = serviceRecognizer.extractPaymentData(title, text)
        val cost = if (paymentData != null) {
            val currencySymbol = when (paymentData.currency) {
                "KZT" -> "₸"
                "RUB" -> "₽"
                "USD" -> "$"
                "EUR" -> "€"
                "GBP" -> "£"
                else -> paymentData.currency
            }
            "$currencySymbol${paymentData.amount}"
        } else {
            extractCost(text ?: title ?: "")
        }
        
        val period = extractPeriod(title, text)
        val nextPaymentDate = extractDate(text ?: title ?: "") ?: LocalDate.now().plusDays(30)
        val currentPaymentDate = LocalDate.now().minusDays(15)

        val subscription = Subscription(
            serviceInfo = serviceInfo,
            cost = cost,
            period = period,
            nextPaymentDate = nextPaymentDate,
            currentPaymentDate = currentPaymentDate
        )

        val currentList = _subscriptions.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.serviceInfo.name == serviceInfo.name }
        
        if (existingIndex >= 0) {
            currentList[existingIndex] = subscription
        } else {
            currentList.add(subscription)
        }
        
        _subscriptions.value = currentList
    }

    private fun extractCost(text: String): String {
        val matcher = costPattern.matcher(text)
        return if (matcher.find()) {
            "${matcher.group(2) ?: "$"}${matcher.group(1)}"
        } else {
            "$0.00"
        }
    }

    private fun extractPeriod(title: String?, text: String?): SubscriptionPeriod {
        val combined = "${title ?: ""} ${text ?: ""}"
        val detectedPeriod = serviceRecognizer.detectPeriod(title, text)
        return when (detectedPeriod.uppercase()) {
            "MONTHLY" -> SubscriptionPeriod.MONTHLY
            "YEARLY" -> SubscriptionPeriod.YEARLY
            "WEEKLY" -> SubscriptionPeriod.WEEKLY
            "DAILY" -> SubscriptionPeriod.DAILY
            "QUARTERLY" -> SubscriptionPeriod.QUARTERLY
            else -> SubscriptionPeriod.MONTHLY
        }
    }

    private fun extractDate(text: String): LocalDate? {
        val matcher = datePattern.matcher(text)
        return if (matcher.find()) {
            try {
                val day = matcher.group(1)?.toInt() ?: return null
                val month = matcher.group(2)?.toInt() ?: return null
                val year = matcher.group(3)?.toInt() ?: return null
                val fullYear = if (year < 100) 2000 + year else year
                LocalDate.of(fullYear, month, day)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    override fun getSubscriptions(): kotlinx.coroutines.flow.Flow<List<Subscription>> {
        return subscriptionsStateFlow
    }

    override suspend fun refreshSubscriptions() {
    }
}
