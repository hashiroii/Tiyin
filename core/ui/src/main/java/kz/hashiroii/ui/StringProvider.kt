package kz.hashiroii.ui

import android.content.Context

class StringProvider(private val context: Context) {
    
    fun getString(resourceName: String, packageName: String, default: String): String {
        val resId = context.resources.getIdentifier(resourceName, "string", packageName)
        return if (resId != 0) {
            context.getString(resId)
        } else {
            default
        }
    }
}
