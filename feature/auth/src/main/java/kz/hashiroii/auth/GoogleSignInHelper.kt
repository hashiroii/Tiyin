package kz.hashiroii.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getSignInClient(): GoogleSignInClient {
        val webClientId = getWebClientId()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        
        return GoogleSignIn.getClient(context, gso)
    }
    
    fun getSignInIntent(): Intent {
        return getSignInClient().signInIntent
    }
    
    suspend fun handleSignInResult(data: Intent?): Result<String> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.await()
            val idToken = account.idToken
            if (idToken != null) {
                Result.success(idToken)
            } else {
                Result.failure(Exception("ID token is null"))
            }
        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getWebClientId(): String {
        val resources = context.resources
        val resourceId = resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (resourceId != 0) {
            resources.getString(resourceId)
        } else {
            throw IllegalStateException("default_web_client_id not found. Please add it to strings.xml from google-services.json")
        }
    }
}
