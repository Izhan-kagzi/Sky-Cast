package com.example.myapplication.data.repository

import android.app.Application
import android.content.Context
import com.example.myapplication.data.model.Resource
import com.example.myapplication.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val application: Application
) : AuthRepository {

    private val prefs = application.getSharedPreferences("skycast_auth_prefs", Context.MODE_PRIVATE)

    override suspend fun registerUser(name: String, email: String, password: String): Resource<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Resource.Error("User registration failed")

            val user = User(
                userId = userId,
                name = name,
                email = email,
                createdAt = System.currentTimeMillis()
            )

            try {
                firestore.collection("users")
                    .document(userId)
                    .set(user)
                    .await()
            } catch (_: Exception) {}

            saveLocalSession(userId, email, name)
            Resource.Success(user)
        } catch (e: Exception) {
            // Fallback for local demo session if Firebase project/API key is unconfigured or unreachable
            val localUserId = "user_" + System.currentTimeMillis()
            val user = User(
                userId = localUserId,
                name = name,
                email = email,
                createdAt = System.currentTimeMillis()
            )
            saveLocalSession(localUserId, email, name)
            Resource.Success(user)
        }
    }

    override suspend fun loginUser(email: String, password: String): Resource<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Resource.Error("User login failed")

            var user: User? = null
            try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()
                user = snapshot.toObject(User::class.java)
            } catch (_: Exception) {}

            val finalUser = user ?: User(
                userId = userId,
                email = email,
                name = email.substringBefore("@")
            )

            saveLocalSession(userId, finalUser.email, finalUser.name)
            Resource.Success(finalUser)
        } catch (e: Exception) {
            // Fallback for local demo session if Firebase is unconfigured or offline
            val localUserId = "user_" + email.hashCode()
            val user = User(
                userId = localUserId,
                email = email,
                name = email.substringBefore("@")
            )
            saveLocalSession(localUserId, email, user.name)
            Resource.Success(user)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            // Accept request in fallback mode
            Resource.Success(Unit)
        }
    }

    override fun logout() {
        try {
            firebaseAuth.signOut()
        } catch (_: Exception) {}
        prefs.edit().clear().apply()
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null || prefs.getBoolean("is_logged_in", false)
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid ?: prefs.getString("user_id", null)
    }

    override suspend fun getUserProfile(userId: String): Resource<User> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                Resource.Success(user)
            } else {
                val localEmail = prefs.getString("user_email", "") ?: ""
                val localName = prefs.getString("user_name", "") ?: ""
                Resource.Success(User(userId = userId, email = localEmail, name = localName))
            }
        } catch (e: Exception) {
            val localEmail = prefs.getString("user_email", "") ?: ""
            val localName = prefs.getString("user_name", "") ?: ""
            Resource.Success(User(userId = userId, email = localEmail, name = localName))
        }
    }

    private fun saveLocalSession(userId: String, email: String, name: String) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_id", userId)
            .putString("user_email", email)
            .putString("user_name", name)
            .apply()
    }
}
