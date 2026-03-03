package com.gaurav.spofiy.data.repoImpl

import android.util.Base64
import androidx.media3.common.util.Log
import com.gaurav.spofiy.data.common.CLIENT_ID
import com.gaurav.spofiy.data.common.CLIENT_SECRET
import com.gaurav.spofiy.data.model.dummySongs
import com.gaurav.spofiy.data.network.ApiService
import com.gaurav.spofiy.data.network.TokenManager
import com.gaurav.spofiy.domain.model.Track
import com.gaurav.spofiy.domain.model.User
import com.gaurav.spofiy.domain.repo.SpotifyRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : SpotifyRepo {

    override fun registerUser(user: User, password: String): Flow<AuthResult<String>> =
        callbackFlow {
            trySend(AuthResult.Loading)
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid ?: ""
                    val userMap = hashMapOf("name" to user.name, "email" to user.email, "uid" to userId)
                    firebaseFirestore.collection("userSpotify").document(userId).set(userMap)
                        .addOnSuccessListener { trySend(AuthResult.Success("User registered successfully")) }
                        .addOnFailureListener { trySend(AuthResult.Error(it.message.toString())) }
                }.addOnFailureListener { trySend(AuthResult.Error(it.message.toString())) }
            awaitClose { close() }
        }

    override fun loginUser(email: String, password: String): Flow<AuthResult<String>> = callbackFlow {
        trySend(AuthResult.Loading)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { trySend(AuthResult.Success("User logged in successfully")) }
            .addOnFailureListener { trySend(AuthResult.Error(it.message.toString())) }
        awaitClose { close() }
    }

    override fun getCurrentUser(): Flow<AuthResult<User>> = callbackFlow {
        trySend(AuthResult.Loading)
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firebaseFirestore.collection("userSpotify").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document?.exists() == true) {
                        trySend(AuthResult.SuccessData(User(
                            id = userId,
                            name = document.getString("name") ?: "",
                            email = document.getString("email") ?: ""
                        )))
                        Log.d("data","view${document.getString("name")}")
                    } else { trySend(AuthResult.Error("User not found")) }
                }.addOnFailureListener { trySend(AuthResult.Error(it.message.toString())) }
        } else { trySend(AuthResult.Error("No session")) }
        awaitClose { close() }
    }

    override fun logout(): Flow<AuthResult<String>> = callbackFlow {
        firebaseAuth.signOut()
        trySend(AuthResult.Success("Logged out"))
        awaitClose { close() }
    }



    override suspend fun searchTracks(query: String, type: String): List<Track> {
        val token = getOrRefreshToken() ?: return emptyList()
        return try {
            val response = apiService.searchTracks("Bearer $token", query, type)
            response.tracks.items.map { dto ->
                Track(
                    id = dto.id,
                    name = dto.name,
                    artistName = dto.artists.firstOrNull()?.name ?: "Unknown",
                    imageUrl = dto.album.images.firstOrNull()?.url,
                    previewUrl = dto.previewUrl,
                    songResId = 0
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    override fun getLocalDummySongs(): List<Track> = dummySongs().map {
        Track(it.id, it.name, it.artistName ?: "Unknown", it.imageUrl, it.previewUrl, it.songResId)
    }

    private suspend fun getOrRefreshToken(): String? {
        var token = tokenManager.getToken()
        if (token == null) {
            val auth = "$CLIENT_ID:$CLIENT_SECRET"
            val encoded = Base64.encodeToString(auth.toByteArray(), Base64.NO_WRAP)
            val response = apiService.getToken("Basic $encoded")
            tokenManager.saveToken(response.accessToken, response.expiresIn)
            token = response.accessToken
        }
        return token
    }
}
sealed class AuthResult<out T> {
    object Loading : AuthResult<Nothing>()
    data class Success(val data: String) : AuthResult<Nothing>()
    data class SuccessData<out T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}