package com.markoala.tomoandroid.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.markoala.tomoandroid.data.model.user.UserProfile
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object UserRepository {
    /**
     * Firestore에 사용자 정보 저장 (suspend)
     */
    suspend fun saveUserToFirestore(userProfile: UserProfile) =
        suspendCancellableCoroutine<Unit> { cont ->
            try {
                val firestore = FirebaseFirestore.getInstance()
                val userMap = hashMapOf(
                    "uid" to userProfile.uuid,
                    "name" to userProfile.username,
                    "email" to userProfile.email
                )
                val task = firestore.collection("users").document(userProfile.uuid).set(userMap)
                task.addOnSuccessListener {
                    cont.resume(Unit)
                }.addOnFailureListener { ex ->
                    cont.resumeWithException(ex)
                }
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }
}
