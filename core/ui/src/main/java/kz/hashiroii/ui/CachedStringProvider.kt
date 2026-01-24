package kz.hashiroii.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class CachedStringProvider(private val context: Context) {
    private val cache = mutableMapOf<String, String>()
    private val defaultPackageName = context.packageName
    
    fun getString(resourceName: String, packageName: String? = null, default: String): String {
        val pkgName = packageName ?: defaultPackageName
        val key = "$pkgName:$resourceName"
        return cache.getOrPut(key) {
            val resId = context.resources.getIdentifier(resourceName, "string", pkgName)
            if (resId != 0) {
                context.getString(resId)
            } else {
                default
            }
        }
    }
}

@Composable
fun rememberCachedStringProvider(): CachedStringProvider {
    val context = LocalContext.current
    return remember { CachedStringProvider(context) }
}
