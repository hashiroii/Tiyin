package kz.hashiroii.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class StringResource(
        val resId: Int,
        val formatArgs: List<Any> = emptyList()
    ) : UiText

    data class DynamicString(val value: String) : UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is StringResource -> {
                if (formatArgs.isEmpty()) {
                    stringResource(resId)
                } else {
                    stringResource(resId, *formatArgs.toTypedArray())
                }
            }
            is DynamicString -> value
        }
    }
}
