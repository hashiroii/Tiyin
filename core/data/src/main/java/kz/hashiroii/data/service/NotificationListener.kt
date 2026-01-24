package kz.hashiroii.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class NotificationListener : NotificationListenerService() {

    companion object {
        const val TAG = "NotificationListener"

        private var notificationHandler: ((StatusBarNotification) -> Unit)? = null

        fun setNotificationHandler(handler: (StatusBarNotification) -> Unit) {
            notificationHandler = handler
        }

        private val SUBSCRIPTION_PATTERN = Pattern.compile(
            "(?i)(subscription|подписка|абонемент|payment|платеж|оплата|billing|биллинг|" +
            "renewal|продление|charge|списание|charged|списано|paid|оплачено|" +
            "cost|стоимость|amount|сумма|price|цена|fee|плата|due|к оплате|" +
            "auto.?renew|автопродление|recurring|повторяющийся|monthly|ежемесячно|" +
            "annual|годовой|yearly|ежегодно|\\d+\\s*(₸|₽|\\$|€|£|тенге|рубль|dollar|euro))",
            Pattern.CASE_INSENSITIVE
        )

        private val BANK_RECURRING_PATTERN = Pattern.compile(
            "(?i)(subscription|подписка|автопродление|recurring|повторяющийся|" +
            "auto.?renew|автоплатеж|автосписание|ежемесячно|monthly|yearly|ежегодно)",
            Pattern.CASE_INSENSITIVE
        )

        private val BANK_ONE_TIME_PATTERN = Pattern.compile(
            "(?i)(one.?time|разов|единоразов|single|один раз|покупка|purchase)(?!.*(subscription|подписка|recurring))",
            Pattern.CASE_INSENSITIVE
        )
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val BANK_PACKAGES = listOf(
        "kz.kaspi.yield",
        "com.halykbank",
        "kz.kaspi.kaspi",
        "kz.halykbank.mobile",
        "kz.jysanbank.mobile"
    )

    private fun isSubscriptionNotification(title: String?, text: String?): Boolean {
        val titleStr = title?.toString() ?: ""
        val textStr = text?.toString() ?: ""
        val combined = "$titleStr $textStr"
        return SUBSCRIPTION_PATTERN.matcher(combined).find()
    }

    private fun isBankRecurringPayment(title: String?, text: String?): Boolean {
        val titleStr = title?.toString() ?: ""
        val textStr = text?.toString() ?: ""
        val combined = "$titleStr $textStr"
        return BANK_RECURRING_PATTERN.matcher(combined).find() && 
               !BANK_ONE_TIME_PATTERN.matcher(combined).find()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        sbn?.let { notification ->
            serviceScope.launch {
                try {
                    val title = notification.notification.extras.getCharSequence("android.title")
                    val text = notification.notification.extras.getCharSequence("android.text")
                    val packageName = notification.packageName

                    Log.d(TAG, "Notification received from: $packageName")
                    Log.d(TAG, "Notification title: $title")
                    Log.d(TAG, "Notification text: $text")

                    val isBankPackage = BANK_PACKAGES.contains(packageName)
                    
                    if (isBankPackage) {
                        if (isBankRecurringPayment(title?.toString(), text?.toString())) {
                            Log.d(TAG, "Bank recurring payment detected")
                            notificationHandler?.invoke(notification)
                        }
                    } else {
                        if (isSubscriptionNotification(title?.toString(), text?.toString())) {
                            Log.d(TAG, "Subscription notification detected")
                            notificationHandler?.invoke(notification)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing notification", e)
                }
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification listener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Notification listener disconnected")
    }
}
