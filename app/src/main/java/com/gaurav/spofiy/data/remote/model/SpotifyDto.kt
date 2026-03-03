package com.gaurav.spofiy.data.remote.model

import com.google.gson.annotations.SerializedName

data class SpotifySearchResponse(
    @SerializedName("tracks") val tracks: SpotifyTracks
)

data class SpotifyTracks(
    @SerializedName("items") val items: List<SpotifyTrackDto>
)

data class SpotifyTrackDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("album") val album: SpotifyAlbumDto,
    @SerializedName("preview_url") val previewUrl: String?,
    @SerializedName("artists") val artists: List<SpotifyArtistDto>
)
data class SpotifyArtistDto(
    @SerializedName("name")
    val name: String
)
data class SpotifyAlbumDto(
    @SerializedName("images") val images: List<SpotifyImageDto>
)
data class SpotifyArtistNameADto(
    @SerializedName("name") val name: List<SpotifyImageDto>
)

data class SpotifyImageDto(
    @SerializedName("url") val url: String
)
