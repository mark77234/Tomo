package com.markoala.tomoandroid.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.markoala.tomoandroid.data.model.user.UserProfile
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object UserRepository {
    /**
     * Firestore에 사용자 정보 저장 (suspend)
     */
    suspend fun saveUserToFirestore(userProfile: UserProfile, fcmToken: String? = null) =
        suspendCancellableCoroutine<Unit> { cont ->
            try {
                val firestore = FirebaseFirestore.getInstance()
                val userMap = hashMapOf(
                    "uid" to userProfile.uuid,
                    "name" to userProfile.username,
                    "email" to userProfile.email
                )
                if (!fcmToken.isNullOrBlank()) {
                    userMap["fcmToken"] = fcmToken
                }
                val task = firestore.collection("users")
                    .document(userProfile.uuid)
                    .set(userMap, SetOptions.merge())
                task.addOnSuccessListener {
                    cont.resume(Unit)
                }.addOnFailureListener { ex ->
                    cont.resumeWithException(ex)
                }
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }

    suspend fun updateFcmToken(uuid: String, fcmToken: String) =
        suspendCancellableCoroutine<Unit> { cont ->
            try {
                val firestore = FirebaseFirestore.getInstance()
                val task = firestore.collection("users")
                    .document(uuid)
                    .set(
                        mapOf(
                            "uid" to uuid,
                            "fcmToken" to fcmToken
                        ),
                        SetOptions.merge()
                    )
                task.addOnSuccessListener { cont.resume(Unit) }
                    .addOnFailureListener { ex -> cont.resumeWithException(ex) }
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }
}
