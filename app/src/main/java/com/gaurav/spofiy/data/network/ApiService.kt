package com.gaurav.spofiy.data.network

import com.gaurav.spofiy.data.remote.model.SpotifySearchResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token") // Use full URL for token
    suspend fun getToken(
        @Header("Authorization") auth: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): TokenResponse

    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = "track"
    ): SpotifySearchResponse
}

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)
