package com.markoala.tomoandroid.data.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.markoala.tomoandroid.data.model.UserData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object UserRepository {
    /**
     * Firestore에 사용자 정보 저장 (suspend)
     */
    suspend fun saveUserToFirestore(userData: UserData) =
        suspendCancellableCoroutine<Unit> { cont ->
            try {
                val firestore = FirebaseFirestore.getInstance()
                val userMap = hashMapOf(
                    "uid" to userData.uuid,
                    "name" to userData.username,
                    "email" to userData.email
                )
                val task = firestore.collection("users").document(userData.uuid).set(userMap)
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
