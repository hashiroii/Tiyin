package kz.hashiroii.analytics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kz.hashiroii.designsystem.theme.TiyinTheme

@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Preview(name = "Light Theme", showBackground = true)
@Composable
private fun AnalyticsScreenLightPreview() {
    TiyinTheme(themePreference = "Light") {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            AnalyticsScreen()
        }
    }
}

@Preview(name = "Dark Theme", showBackground = true)
@Composable
private fun AnalyticsScreenDarkPreview() {
    TiyinTheme(themePreference = "Dark") {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            AnalyticsScreen()
        }
    }
}
