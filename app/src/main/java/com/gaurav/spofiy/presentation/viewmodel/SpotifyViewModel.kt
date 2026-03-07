package com.gaurav.spofiy.presentation.viewmodel

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.gaurav.spofiy.data.common.CLIENT_ID
import com.gaurav.spofiy.data.common.CLIENT_SECRET
import com.gaurav.spofiy.data.mapper.toDomain
import com.gaurav.spofiy.data.model.dummySongs
import com.gaurav.spofiy.data.network.ApiService
import com.gaurav.spofiy.data.network.AuthState
import com.gaurav.spofiy.data.network.TokenManager
import com.gaurav.spofiy.data.repoImpl.AuthResult
import com.gaurav.spofiy.domain.model.Track
import com.gaurav.spofiy.domain.model.User
import com.gaurav.spofiy.domain.repo.MusicPlayerManager
import com.gaurav.spofiy.domain.repo.SpotifyRepo
import com.gaurav.spofiy.presentation.home.LibraryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpotifyViewModel @Inject constructor(
    private val player: MusicPlayerManager,
    private val repository: SpotifyRepo,
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val context: Context
) : ViewModel() {
    // Player States
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    // --- Authentication --- //
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState = _authState.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData = _userData.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    // Data Sections
    val dummySongs: List<Track> = dummySongs()
    private val _apiSongs = MutableStateFlow<List<Track>>(emptyList())
    val apiSongs = _apiSongs.asStateFlow()

    private val _realArtists = MutableStateFlow<List<LibraryItem>>(emptyList())
    val realArtists = _realArtists.asStateFlow()

    val allSongs = MutableStateFlow<List<Track>>(dummySongs())

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    var topMixes by mutableStateOf<List<Track>>(emptyList())
    private val _recentlyPlayed = MutableStateFlow<List<Track>>(emptyList())
    val recentlyPlayed = _recentlyPlayed.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private var currentIndex = -1
    private var spotifyToken: String? = null
    private var searchJob: Job? = null

    init {
        topMixes = dummySongs()
        startProgressLoop()
        loadUserProfile()
        setupPlayerListener()
        fetchApiSongs()
        fetchRealArtists()
    }



    //  API
    fun fetchApiSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = getOrRefreshToken()
                spotifyToken = token
                token?.let {
                    val response = apiService.searchTracks(
                        "Bearer $it",
                        "Bollywood Hits",
                        "track"
                    )
                    val tracks = response.tracks.items.map { it.toDomain() }
                    _apiSongs.value = tracks
                    allSongs.value =
                        (dummySongs + tracks).distinctBy { it.id }
                }
            } catch (e: Exception) {
                Log.e("API", e.toString())
            }
        }
    }

    private suspend fun getOrRefreshToken(): String? {
        var token = tokenManager.getToken()
        if (token == null) {
            try {
                val auth = "${CLIENT_ID}:${CLIENT_SECRET}"
                val encoded = Base64.encodeToString(auth.toByteArray(), Base64.NO_WRAP)
                val tokenResponse = apiService.getToken("Basic $encoded")
                tokenManager.saveToken(tokenResponse.accessToken, tokenResponse.expiresIn)
                token = tokenResponse.accessToken
            } catch (e: Exception) {
                Log.e("TOKEN", e.toString())
            }
        }
        return token
    }

    fun fetchRealArtists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = getOrRefreshToken() ?: return@launch
                // We'll search for popular artists to populate your Library
                val artistQuery = "Arijit Singh,Justin Bieber,The Weeknd,Taylor Swift"
                val response = apiService.searchTracks("Bearer $token", artistQuery, "track")

                // Extract unique artist names and their images from track results
                val artists = response.tracks.items.map { track ->
                    LibraryItem(
                        title = track.name,
                        subtitle = "Artist",
                        imageUrl = track.album.images.firstOrNull()?.url,
                        isArtist = true
                    )
                }.distinctBy { it.title }.take(10)

                _realArtists.value = artists
            } catch (e: Exception) { Log.e("ARTISTS", e.toString()) }
        }
    }
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        searchJob?.cancel()
        if (newQuery.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            val token = spotifyToken ?: getOrRefreshToken()
            if (token != null) {
                try {

                    val response = apiService.searchTracks("Bearer $token", newQuery, "track")
                  val track= response.tracks.items.map { it.toDomain() }
                    _searchResults.value =track
                } catch (e: Exception) { Log.e("SEARCH", e.toString()) }
            }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun onArtistClick(artistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = spotifyToken ?: getOrRefreshToken()
            if (token != null) {
                try {
                    val response = apiService.searchTracks(
                        "Bearer $token",
                        artistName,
                        "track"
                    )

                    // DTO → Domain conversion
                    val tracks = response.tracks.items.map { it.toDomain()}

                    if (tracks.isNotEmpty()) {
                        onSongClick(tracks[0])
                        allSongs.value =
                            (allSongs.value + tracks).distinctBy { it.id }
                    }

                } catch (e: Exception) {
                    Log.e("ARTIST_CLICK", e.toString())
                }
            }
        }
    }

    private fun setupPlayerListener() {
        _isPlaying.value = player.exoPlayer.isPlaying
        player.exoPlayer.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    _duration.value = player.getDuration()
                }

                if (state == Player.STATE_ENDED) {
                    playNext()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    // ---------------- SONG PLAY ----------------

    fun onSongClick(track: Track) {

        if (_currentTrack.value?.id == track.id) {
            togglePlayPause()
            return
        }
_currentTrack.value =track
        player.play(context = context,track)
        updateRecentlyPlayed(track)
    }

    fun togglePlayPause() {
        if (player.isPlaying()) player.pause() else player.resume()
    }
    private val _isShuffleOn = MutableStateFlow(false)
    val isShuffleOn = _isShuffleOn.asStateFlow()

    fun toggleShuffle() {
        _isShuffleOn.value = !_isShuffleOn.value
    }
    fun playNext() {

        val list = allSongs.value
        if (list.isEmpty()) return

        val nextTrack = if (_isShuffleOn.value) {
            list.random()
        } else {
            val currentIndex = list.indexOfFirst { it.id == _currentTrack.value?.id }
            list[(currentIndex + 1) % list.size]
        }

        onSongClick(nextTrack)
    }

    fun playPrevious() {
        val list = allSongs.value
        if (list.isEmpty()) return

        val currentIndex = list.indexOfFirst { it.id == _currentTrack.value?.id }
        val prevIndex = if (currentIndex <= 0) list.lastIndex else currentIndex - 1
        onSongClick(list[prevIndex])
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        _currentPosition.value = position
    }

    private fun startProgressLoop() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = player.currentPosition()
                _duration.value = player.getDuration()
                delay(500)
            }
        }
    }

    private fun updateRecentlyPlayed(track: Track) {
        val list = _recentlyPlayed.value.toMutableList()
        list.removeAll { it.id == track.id }
        list.add(track)
        if (list.size > 10) list.removeAt(0)
        _recentlyPlayed.value = list
    }

    fun isThisSongPlaying(track: Track): Boolean {
        return _currentTrack.value?.id == track.id && isPlaying.value
    }



    fun loadUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentUser().collect { result ->
                if (result is AuthResult.SuccessData) _userData.value = result.data
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logout().collect {
                if (it is AuthResult.Success) {
                    _authState.value = AuthState.Unauthenticated
                    _userData.value = null
                }
            }
        }
    }
    fun loginUser(userData: User, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loginUser(userData.email, password = password).collect {
                handleAuthResult(it)
                if (it is AuthResult.Success) loadUserProfile()
            }
        }
    }

    fun createUser(userData: User, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.registerUser(userData, password = password).collect {
                handleAuthResult(it)
                if (it is AuthResult.Success) loadUserProfile()
            }
        }
    }

    private fun handleAuthResult(result: AuthResult<String>) {
        when (result) {
            is AuthResult.Loading -> _authState.value = AuthState.isLoading
            is AuthResult.Success -> _authState.value = AuthState.Authenticate
            is AuthResult.Error -> _authState.value = AuthState.isError(result.message)
            else -> {}
        }
    }
}