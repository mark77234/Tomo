package com.markoala.tomoandroid.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthManager {
    private const val TAG = "AuthManager"
    lateinit var auth: FirebaseAuth

    fun init() {
        auth = Firebase.auth
    }

    fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.localizedMessage)
            }
    }

    suspend fun signOutSuspend(context: Context): Pair<Boolean, String?> {
        return try {
            if (::auth.isInitialized) auth.signOut()
            val credentialManager = CredentialManager.create(context)
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
            Pair(true, null)
        } catch (e: ClearCredentialException) {
            Log.w(TAG, "CredentialManager clear failed: ${e.localizedMessage}")
            Pair(true, e.localizedMessage)
        } catch (e: Exception) {
            Log.e(TAG, "SignOut failed", e)
            Pair(false, e.localizedMessage)
        }
    }
}
