package kz.hashiroii.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home : TiyinDestination

@Serializable
object Analytics : TiyinDestination

@Serializable
object Groups : TiyinDestination

@Serializable
object Profile : TiyinDestination

@Serializable
object Settings : TiyinDestination

@Serializable
data class AddSubscription(
    val subscriptionId: String? = null
) : TiyinDestination

@Serializable
data class EditSubscription(
    val serviceName: String,
    val serviceDomain: String
) : TiyinDestination

interface TiyinDestination
