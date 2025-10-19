package com.markoala.tomoandroid.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.markoala.tomoandroid.data.api.userApi
import com.markoala.tomoandroid.data.model.user.BaseResponse
import com.markoala.tomoandroid.data.model.user.UserProfile
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object AuthRepository {
    /**
     * Firebase 현재 사용자 정보로 UserProfile 생성
     */
    fun getCurrentUserProfile(): UserProfile? {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (firebaseUser != null) {
            UserProfile(
                uuid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                username = firebaseUser.displayName ?: ""
            )
        } else {
            null
        }
    }

    /**
     * Firestore에 해당 uuid의 사용자 문서가 존재하는지 확인
     */
    suspend fun checkUserExists(uuid: String): Boolean = suspendCancellableCoroutine { cont ->
        try {
            val firestore = FirebaseFirestore.getInstance()
            val task = firestore.collection("users").document(uuid).get()
            task.addOnSuccessListener { snapshot ->
                cont.resume(snapshot.exists())
            }.addOnFailureListener { ex ->
                cont.resumeWithException(ex)
            }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    /**
     * 서버에 사용자 정보 전송 (Retrofit 동기 호출을 IO 스레드에서 수행)
     */
    suspend fun signUp(userProfile: UserProfile): Response<BaseResponse<Unit>> =
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                userApi.signup(userProfile).execute()
            } catch (e: Exception) {
                throw e
            }
        }
}
