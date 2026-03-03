package com.gaurav.spofiy.domain.model

data class Track(
    val id: String,
    val name: String,
    val artistName: String,
    val imageUrl: String?,
    val previewUrl: String?,
    val songResId: Int = 0,
    val lyrics: String = "No lyrics available for this song."
)
