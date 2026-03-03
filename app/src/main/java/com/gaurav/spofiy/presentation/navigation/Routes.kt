package com.gaurav.spofiy.presentation.navigation


import kotlinx.serialization.Serializable

sealed class Routes (val route: String){

    @Serializable
    object Splash : Routes("splash")

    @Serializable
    object Welcome : Routes("welcome")

    @Serializable
    object Login : Routes("login")
    @Serializable
    object SignIn : Routes("signin")

    @Serializable
    object SignUp : Routes("signup")

    @Serializable
    object Home : Routes("home")

    @Serializable
    object Search : Routes("search")

    @Serializable
    data class DetailSong(val trackId: String) : Routes("song/{trackId}")

    @Serializable
    object Library : Routes("library")

    @Serializable
    data class ArtistDetail(val artistName: String) : Routes("artist/{artistName}")
}
