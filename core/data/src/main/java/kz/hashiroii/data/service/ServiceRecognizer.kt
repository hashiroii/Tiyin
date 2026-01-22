package kz.hashiroii.data.service

import android.content.Context
import kz.hashiroii.domain.model.service.ExtractedPaymentData
import kz.hashiroii.domain.model.service.ServiceInfo
import kz.hashiroii.domain.model.service.ServiceType
import java.util.regex.Pattern

class ServiceRecognizer(private val context: Context) {

    private val BANK_PACKAGES = listOf(
        "kz.kaspi.yield",
        "com.halykbank",
        "kz.kaspi.kaspi",
        "kz.halykbank.mobile",
        "kz.jysanbank.mobile"
    )

    private val serviceMap = mapOf(
        "com.spotify.music" to ServiceMapping("Spotify", 0xFF1DB954, 0xFF191414, ServiceType.STREAMING),
        "com.netflix.mediaclient" to ServiceMapping("Netflix", 0xFFE50914, 0xFF000000, ServiceType.STREAMING),
        "com.amazon.avod.thirdpartyclient" to ServiceMapping("Prime Video", 0xFF00A8E1, 0xFF000000, ServiceType.STREAMING),
        "com.disney.disneyplus" to ServiceMapping("Disney+", 0xFF113CCF, 0xFF000000, ServiceType.STREAMING),
        "com.hbo.hbonow" to ServiceMapping("HBO", 0xFF000000, 0xFF8B0000, ServiceType.STREAMING),
        "com.apple.android.music" to ServiceMapping("Apple Music", 0xFFFA243C, 0xFF000000, ServiceType.STREAMING),
        "com.google.android.apps.youtube.music" to ServiceMapping("YouTube Music", 0xFFFF0000, 0xFF000000, ServiceType.STREAMING),
        "com.audible.application" to ServiceMapping("Audible", 0xFFF8991C, 0xFF000000, ServiceType.AUDIOBOOK),
        "com.microsoft.office.officehub" to ServiceMapping("Microsoft 365", 0xFF0078D4, 0xFF000000, ServiceType.PRODUCTIVITY),
        "com.dropbox.android" to ServiceMapping("Dropbox", 0xFF0061FF, 0xFFFFFFFF, ServiceType.CLOUD_STORAGE),
        "com.google.android.apps.drive" to ServiceMapping("Google Drive", 0xFF4285F4, 0xFFFFFFFF, ServiceType.CLOUD_STORAGE),
        "com.adobe.reader" to ServiceMapping("Adobe", 0xFFFF0000, 0xFF000000, ServiceType.SOFTWARE),
        "com.strava" to ServiceMapping("Strava", 0xFFFC4C02, 0xFF000000, ServiceType.FITNESS),
        "com.nike.plusgps" to ServiceMapping("Nike Run Club", 0xFF000000, 0xFFFFFFFF, ServiceType.FITNESS),
        "com.duolingo" to ServiceMapping("Duolingo", 0xFF58CC02, 0xFFFFFFFF, ServiceType.EDUCATION),
        "com.grammarly.android.keyboard" to ServiceMapping("Grammarly", 0xFF15C39A, 0xFFFFFFFF, ServiceType.PRODUCTIVITY),
        "com.nytimes.android" to ServiceMapping("NYTimes", 0xFF000000, 0xFFFFFFFF, ServiceType.NEWS),
        "com.medium.reader" to ServiceMapping("Medium", 0xFF000000, 0xFFFFFFFF, ServiceType.NEWS),
        "com.epicgames.fortnite" to ServiceMapping("Fortnite", 0xFF000000, 0xFFFFFFFF, ServiceType.GAMING),
        "com.activision.callofduty.shooter" to ServiceMapping("Call of Duty", 0xFFED1C24, 0xFF000000, ServiceType.GAMING)
    )

    private val periodPatterns = mapOf(
        Pattern.compile("(?i)(monthly|ежемесячно|месяц)", Pattern.CASE_INSENSITIVE) to "MONTHLY",
        Pattern.compile("(?i)(yearly|annual|годовой|ежегодно)", Pattern.CASE_INSENSITIVE) to "YEARLY",
        Pattern.compile("(?i)(weekly|еженедельно|неделя)", Pattern.CASE_INSENSITIVE) to "WEEKLY",
        Pattern.compile("(?i)(daily|ежедневно|день)", Pattern.CASE_INSENSITIVE) to "DAILY",
        Pattern.compile("(?i)(quarterly|квартал)", Pattern.CASE_INSENSITIVE) to "QUARTERLY"
    )

    private val recurringPaymentPatterns = listOf(
        Pattern.compile("(?i)(subscription|подписка|абонемент|автопродление|recurring|повторяющийся)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)(monthly|ежемесячно|yearly|ежегодно|weekly|еженедельно)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)(auto.?renew|автоплатеж|автосписание)", Pattern.CASE_INSENSITIVE)
    )

    private val oneTimePaymentPatterns = listOf(
        Pattern.compile("(?i)(one.?time|разов|единоразов|single|один раз)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)(purchase|покупка|оплата|платеж)(?!.*(subscription|подписка|recurring))", Pattern.CASE_INSENSITIVE)
    )

    private val amountPattern = Pattern.compile(
        "(\\d+[.,]?\\d*)\\s*([₸₽\\$€£]|тенге|рубль|dollar|euro|USD|EUR|KZT|RUB)",
        Pattern.CASE_INSENSITIVE
    )

    fun recognizeService(packageName: String, notificationTitle: String?, notificationText: String?): ServiceInfo? {
        val combinedText = "${notificationTitle ?: ""} ${notificationText ?: ""}".lowercase()
        
        for ((_, serviceMapping) in serviceMap) {
            if (combinedText.contains(serviceMapping.name.lowercase())) {
                return ServiceInfo(
                    name = serviceMapping.name,
                    logoResId = 0,
                    primaryColor = serviceMapping.primaryColor,
                    secondaryColor = serviceMapping.secondaryColor,
                    serviceType = serviceMapping.serviceType
                )
            }
        }

        val mapping = serviceMap[packageName]
        if (mapping != null) {
            return ServiceInfo(
                name = mapping.name,
                logoResId = 0,
                primaryColor = mapping.primaryColor,
                secondaryColor = mapping.secondaryColor,
                serviceType = mapping.serviceType
            )
        }

        val appName = getAppName(packageName)
        return ServiceInfo(
            name = appName,
            logoResId = 0,
            primaryColor = 0xFF6200EE,
            secondaryColor = 0xFF000000,
            serviceType = ServiceType.OTHER
        )
    }

    fun detectPeriod(notificationTitle: String?, notificationText: String?): String {
        val combined = "${notificationTitle ?: ""} ${notificationText ?: ""}"
        for ((pattern, period) in periodPatterns) {
            if (pattern.matcher(combined).find()) {
                return period
            }
        }
        return "MONTHLY"
    }

    fun isBankPackage(packageName: String): Boolean {
        return BANK_PACKAGES.contains(packageName)
    }

    fun isRecurringPayment(notificationTitle: String?, notificationText: String?): Boolean {
        val combined = "${notificationTitle ?: ""} ${notificationText ?: ""}"
        
        for (pattern in recurringPaymentPatterns) {
            if (pattern.matcher(combined).find()) {
                return true
            }
        }
        
        for (pattern in oneTimePaymentPatterns) {
            if (pattern.matcher(combined).find()) {
                return false
            }
        }
        
        return false
    }

    fun extractPaymentData(notificationTitle: String?, notificationText: String?): ExtractedPaymentData? {
        val combined = "${notificationTitle ?: ""} ${notificationText ?: ""}"
        val matcher = amountPattern.matcher(combined)
        
        if (matcher.find()) {
            val amount = matcher.group(1) ?: ""
            val currencySymbol = matcher.group(2) ?: ""
            
            val currency = when {
                currencySymbol.contains("₸", ignoreCase = true) || currencySymbol.contains("тенге", ignoreCase = true) -> "KZT"
                currencySymbol.contains("₽", ignoreCase = true) || currencySymbol.contains("рубл", ignoreCase = true) -> "RUB"
                currencySymbol.contains("$", ignoreCase = true) || currencySymbol.contains("dollar", ignoreCase = true) || currencySymbol.contains("USD", ignoreCase = true) -> "USD"
                currencySymbol.contains("€", ignoreCase = true) || currencySymbol.contains("euro", ignoreCase = true) || currencySymbol.contains("EUR", ignoreCase = true) -> "EUR"
                currencySymbol.contains("£", ignoreCase = true) || currencySymbol.contains("GBP", ignoreCase = true) -> "GBP"
                else -> currencySymbol.uppercase()
            }
            
            val isRecurring = isRecurringPayment(notificationTitle, notificationText)
            
            return ExtractedPaymentData(
                amount = amount.replace(",", "."),
                currency = currency,
                isRecurring = isRecurring
            )
        }
        
        return null
    }

    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName.substringAfterLast(".")
        }
    }

    private data class ServiceMapping(
        val name: String,
        val primaryColor: Long,
        val secondaryColor: Long,
        val serviceType: ServiceType
    )
}
