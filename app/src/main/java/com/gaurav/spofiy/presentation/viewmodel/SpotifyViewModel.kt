//package com.gaurav.spofiy.presentation.viewmodel
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.gaurav.spofiy.domain.model.Track
//import com.gaurav.spofiy.domain.model.User
//import com.gaurav.spofiy.domain.repo.MusicPlayerManager
//import com.gaurav.spofiy.domain.usecase.*
//import com.gaurav.spofiy.data.repoImpl.AuthResult
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//import androidx.media3.common.Player
//import androidx.media3.common.MediaItem
//import androidx.media3.common.MediaMetadata
//import com.gaurav.spofiy.data.network.AuthState
//
//@HiltViewModel
//class SpotifyViewModel @Inject constructor(
//    private val player: MusicPlayerManager,
//    private val searchTracksUseCase: SearchTracksUseCase,
//    private val getInitialSongsUseCase: GetInitialSongsUseCase,
//    private val loginUseCase: LoginUseCase,
//    private val registerUseCase: RegisterUseCase,
//    private val getCurrentUserUseCase: GetCurrentUserUseCase,
//    private val logoutUseCase: LogoutUseCase
//) : ViewModel() {
//
//    // Player UI State
//    private val _isPlaying = MutableStateFlow(false)
//    val isPlaying = _isPlaying.asStateFlow()
//
//    private val _currentPosition = MutableStateFlow(0L)
//    val currentPosition = _currentPosition.asStateFlow()
//
//    private val _duration = MutableStateFlow(0L)
//    val duration = _duration.asStateFlow()
//
//    private val _currentTrack = MutableStateFlow<Track?>(null)
//    val currentTrack = _currentTrack.asStateFlow()
//
//    // Data State
//    private val _allSongs = MutableStateFlow<List<Track>>(emptyList())
//    val allSongs = _allSongs.asStateFlow()
//
//    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
//    val searchResults = _searchResults.asStateFlow()
//
//    private val _recentlyPlayed = MutableStateFlow<List<Track>>(emptyList())
//    val recentlyPlayed = _recentlyPlayed.asStateFlow()
//
//    // Auth State
//    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
//    val authState = _authState.asStateFlow()
//
//    private val _userData = MutableStateFlow<User?>(null)
//    val userData = _userData.asStateFlow()
//
//    private var searchJob: Job? = null
//
//    init {
//        loadInitialData()
//        setupPlayerListener()
//        startProgressLoop()
//    }
//
//    private fun loadInitialData() {
//        viewModelScope.launch {
//            _allSongs.value = getInitialSongsUseCase()
//            getCurrentUserUseCase().collect { result ->
//                if (result is AuthResult.SuccessData) _userData.value = result.data
//            }
//        }
//    }
//
//    private fun setupPlayerListener() {
//        player.exoPlayer.addListener(object : Player.Listener {
//            override fun onPlaybackStateChanged(state: Int) {
//                if (state == Player.STATE_READY) _duration.value = player.getDuration()
//                if (state == Player.STATE_ENDED) playNext()
//            }
//            override fun onIsPlayingChanged(playing: Boolean) { _isPlaying.value = playing }
//        })
//    }
//
//    fun onSongClick(track: Track) {
//        if (_currentTrack.value?.id == track.id) {
//            togglePlayPause()
//            return
//        }
//
//        val metadata = MediaMetadata.Builder().setTitle(track.name).setArtist(track.artistName).build()
//        val mediaItem = MediaItem.Builder().setMediaMetadata(metadata)
//
//        if (track.songResId != 0) {
//            mediaItem.setUri(androidx.media3.datasource.RawResourceDataSource.buildRawResourceUri(track.songResId))
//        } else if (!track.previewUrl.isNullOrEmpty()) {
//            mediaItem.setUri(track.previewUrl)
//        }
//
//        player.exoPlayer.setMediaItem(mediaItem.build())
//        player.exoPlayer.prepare()
//        player.exoPlayer.play()
//
//        _currentTrack.value = track
//        updateRecentlyPlayed(track)
//    }
//
//    private fun updateRecentlyPlayed(track: Track) {
//        val list = _recentlyPlayed.value.toMutableList()
//        list.removeAll { it.id == track.id }
//        list.add(0, track)
//        if (list.size > 10) list.removeLast()
//        _recentlyPlayed.value = list
//    }
//
//    fun togglePlayPause() {
//        if (player.isPlaying()) player.pause() else player.resume()
//    }
//
//    fun playNext() {
//        val list = _allSongs.value
//        if (list.isEmpty()) return
//        val idx = list.indexOfFirst { it.id == _currentTrack.value?.id }
//        onSongClick(list[(idx + 1) % list.size])
//    }
//
//    fun playPrevious() {
//        val list = _allSongs.value
//        if (list.isEmpty()) return
//        val idx = list.indexOfFirst { it.id == _currentTrack.value?.id }
//        val prev = if (idx <= 0) list.lastIndex else idx - 1
//        onSongClick(list[prev])
//    }
//
//    fun onSearch(query: String) {
//        searchJob?.cancel()
//        searchJob = viewModelScope.launch {
//            delay(500)
//            _searchResults.value = searchTracksUseCase(query, type = "")
//        }
//    }
//
//    fun login(email: String, pass: String) {
//        viewModelScope.launch {
//            loginUseCase(email, pass).collect { result ->
//                handleAuthResult(result)
//                if (result is AuthResult.Success) loadInitialData()
//            }
//        }
//    }
//
//    fun register(user: User, pass: String) {
//        viewModelScope.launch {
//            registerUseCase(user, pass).collect { result ->
//                handleAuthResult(result)
//                if (result is AuthResult.Success) loadInitialData()
//            }
//        }
//    }
//
//    fun logout() {
//        viewModelScope.launch {
//            logoutUseCase().collect {
//                _authState.value = AuthState.Unauthenticated
//                _userData.value = null
//            }
//        }
//    }
//
//    private fun handleAuthResult(result: AuthResult<String>) {
//        when (result) {
//            is AuthResult.Loading -> _authState.value = AuthState.isLoading
//            is AuthResult.Success -> _authState.value = AuthState.Authenticate
//            is AuthResult.Error -> _authState.value = AuthState.isError(result.message)
//            else -> {}
//        }
//    }
//
//    private fun startProgressLoop() {
//        viewModelScope.launch {
//            while (true) {
//                if (player.isPlaying()) _currentPosition.value = player.currentPosition()
//                delay(500)
//            }
//        }
//    }
//
//    fun seekTo(pos: Long) = player.seekTo(pos)
//}
//
//
