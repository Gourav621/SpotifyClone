package com.gaurav.spofiy.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val SpotifyDark = Color(0xFF181818)
val SpotifyGreen = Color(0xFF1DB954)
val SpotifyGray = Color(0xFFB3B3B3)
val spotifyGreenTop = Color(0xFF0F3D40)   // dark teal green
val spotifyMid = Color(0xFF0B2F32)
val spotifyBlack = Color(0xFF000000)

//profileColor
val spotifyProfileColors = listOf(
    Color(0xFF1DB954), // Green
    Color(0xFF009688), // Dark Gray
    Color(0xFF8D67AB), // Purple
    Color(0xFFBA5D07), // Brown
    Color(0xFFE13300), // Red
    Color(0xFF2E77D0), // Blue
    Color(0xFFAF2896), // Pink
    Color(0xFF509BF5)  // Sky Blue
)
fun getSpotifyProfileColor(name: String): Color {
    val hash = name.hashCode()
    val index = kotlin.math.abs(hash) % spotifyProfileColors.size
    return spotifyProfileColors[index]
}