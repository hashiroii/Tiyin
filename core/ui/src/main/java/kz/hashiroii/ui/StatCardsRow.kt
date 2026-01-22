package kz.hashiroii.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatCardsRow(
    modifier: Modifier = Modifier,
    activeSubscriptions: Int,
    totalSpending: String,
    activeTitle: String,
    spendingTitle: String,
    subscriptionLabel: String,
    onActiveClick: (() -> Unit)? = null,
    onSpendingClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = activeTitle,
            value = activeSubscriptions.toString(),
            subtitle = subscriptionLabel,
            onClick = onActiveClick,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = spendingTitle,
            value = totalSpending,
            subtitle = subscriptionLabel,
            onClick = onSpendingClick,
            modifier = Modifier.weight(1f)
        )
    }
}
