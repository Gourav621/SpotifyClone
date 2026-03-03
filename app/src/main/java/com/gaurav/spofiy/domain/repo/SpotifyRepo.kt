package com.gaurav.spofiy.domain.repo

import com.gaurav.spofiy.domain.model.Track
import com.gaurav.spofiy.domain.model.User
import com.gaurav.spofiy.data.repoImpl.AuthResult
import kotlinx.coroutines.flow.Flow

interface SpotifyRepo {
    fun registerUser(user: User, password: String): Flow<AuthResult<String>>
    fun loginUser(email: String, password: String): Flow<AuthResult<String>>
    fun getCurrentUser(): Flow<AuthResult<User>>
    fun logout(): Flow<AuthResult<String>>
    suspend fun searchTracks(query: String, type: String): List<Track>
    fun getLocalDummySongs(): List<Track>
}
