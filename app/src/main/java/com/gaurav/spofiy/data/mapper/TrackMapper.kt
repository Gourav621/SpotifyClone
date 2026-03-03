package com.gaurav.spofiy.data.mapper

import com.gaurav.spofiy.data.remote.model.SpotifyTrackDto
import com.gaurav.spofiy.domain.model.Track

fun SpotifyTrackDto.toDomain(): Track {
    return Track(
        id = id,
        name = name,
        artistName = this.artists?.firstOrNull()?.name?:"Unknown Artist",
        imageUrl = album.images.firstOrNull()?.url,
        previewUrl = previewUrl,
        songResId = 0, // API song ke liye local song nahi
        lyrics = "Lyrics not available"
    )
}
