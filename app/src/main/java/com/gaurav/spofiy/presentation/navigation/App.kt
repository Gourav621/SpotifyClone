package com.gaurav.spofiy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gaurav.spofiy.presentation.viewmodel.SpotifyViewModel

import com.gaurav.spofiy.presentation.home.ArtistDetailScreen
import com.gaurav.spofiy.presentation.home.DetailSongScreen
import com.gaurav.spofiy.presentation.home.HomeScreen
import com.gaurav.spofiy.presentation.home.Library
import com.gaurav.spofiy.presentation.home.LoginScreen
import com.gaurav.spofiy.presentation.home.SearchScreen
import com.gaurav.spofiy.presentation.home.SignInScreen
import com.gaurav.spofiy.presentation.home.SignUpScreen
import com.gaurav.spofiy.presentation.home.SplashScreen
import com.gaurav.spofiy.presentation.home.WelcomeScreen


@Composable
fun App() {
    val navController = rememberNavController()
    val play: SpotifyViewModel = hiltViewModel()

    
    NavHost(navController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Routes.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(Routes.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(Routes.Login.route) {
            LoginScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable(Routes.SignIn.route) {
            SignInScreen(navController = navController)
        }
        composable(Routes.Home.route) {
            HomeScreen(navController)
        }
        composable(Routes.Search.route) {
            SearchScreen(navController)
        }
        composable(Routes.Library.route) {
            Library(navController)
        }
        composable("artist/{artistName}") {
            val artistName = it.arguments?.getString("artistName") ?:""
            ArtistDetailScreen(artistName, navController)
        }
        // Consistent route with trackId
        composable("song/{trackId}") { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId") ?: return@composable
            // Find track in both songs and topMixes to be safe
            val track = (play.apiSongs.value + play.topMixes).find { it.id == trackId } ?: return@composable

            DetailSongScreen(initialTrack = track)
        }
    }
}
