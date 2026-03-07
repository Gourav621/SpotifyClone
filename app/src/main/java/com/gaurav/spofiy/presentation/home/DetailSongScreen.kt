package com.gaurav.spofiy.presentation.home

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.gaurav.spofiy.presentation.viewmodel.SpotifyViewModel
import com.gaurav.spofiy.domain.model.Track


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSongScreen(initialTrack: Track, viewModel: SpotifyViewModel = hiltViewModel()) {

    val primaryCyan = Color(0xFF4DD0E1)

    val controlBackground = Color(0xFF1C1C1C)
val context = LocalContext.current
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPos by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    // Smooth transition between colors
    var rawExtractedColor by remember { mutableStateOf(Color(0xFF282828)) }
    val backgroundColor by animateColorAsState(
        targetValue = rawExtractedColor,
        animationSpec = tween(durationMillis = 600),
        label = "MiniPlayerBgColor"
    )
    val track = currentTrack ?: initialTrack
    val safeDuration = if (duration > 0) duration.toFloat() else 1f
    var sliderPosition by remember { mutableStateOf(0f) }
    var isUserDragging by remember { mutableStateOf(false) }

// Sync slider with player when NOT dragging
    LaunchedEffect(currentPos) {
        if (!isUserDragging) {
            sliderPosition = currentPos.toFloat()
        }
    }
    LaunchedEffect(track.id) {
        rawExtractedColor = Color(0xFF282828)
    }
    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PLAYING FROM PLAYLIST:",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Lofi Loft",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    // Display the image of the CURRENT track
                    AsyncImage(
                        model = ImageRequest.Builder(context =context )
                            .data(track.imageUrl)
                            .crossfade(true)
                            .allowHardware(true) // CRITICAL for Palette to work
                            .build(),
                        contentDescription = track.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(track.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(track.artistName ?: "Unknown Artist", color = Color.Gray, fontSize = 18.sp)
                    }
                    Icon(Icons.Default.Share, "Share", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(Icons.Default.Favorite, "Like", tint = primaryCyan, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Slider(
                    value = sliderPosition.coerceIn(0f,safeDuration),
                    onValueChange = { viewModel.seekTo(it.toLong()) },
                    valueRange = 0f..safeDuration,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = primaryCyan,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(currentPos), color = Color.Gray, fontSize = 12.sp)
                    Text(formatTime(duration), color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.toggleShuffle()}) {
                        Icon(Icons.Default.Shuffle, "Shuffle", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = { viewModel.playPrevious() }) {
                        Icon(Icons.Default.SkipPrevious, "Previous", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Box(
                        modifier = Modifier.size(64.dp).background(primaryCyan, CircleShape).clickable { viewModel.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.playNext() }) {
                        Icon(Icons.Default.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.PlaylistPlay, "Queue", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("LYRICS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(colors = listOf(Color(0xFF26C6DA), Color(0xFF006064))),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Text(
                            text = track.lyrics,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
fun formatTime(milliseconds: Long): String {
    if (milliseconds < 0) return "0:00"
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}