package com.gaurav.spofiy.presentation.utils

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.gaurav.spofiy.presentation.viewmodel.SpotifyViewModel
import com.gaurav.spofiy.domain.model.Track
import com.gaurav.spofiy.ui.theme.SpotifyGray

@Composable
fun MiniPlayer(
    track: Track,
    viewModel: SpotifyViewModel,
    onClick: () -> Unit
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val progressFraction = if (duration > 0) progress.toFloat() / duration.toFloat() else 0f
    
    val context = LocalContext.current
    val defaultColor = Color(0xFF282828)
    var rawExtractedColor by remember { mutableStateOf(defaultColor) }

    // Smooth transition between colors
    val backgroundColor by animateColorAsState(
        targetValue = rawExtractedColor,
        animationSpec = tween(durationMillis = 600),
        label = "MiniPlayerBgColor"
    )

    val imageUrl = track.imageUrl

    // Reset color to default briefly when track changes to allow new extraction
    LaunchedEffect(track.id) {
        rawExtractedColor = defaultColor
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .allowHardware(false) // CRITICAL for Palette to work
                    .build(),
                contentDescription = null,
                onSuccess = { result ->
                    val bitmap = (result.result.image.asDrawable(context.resources) as? BitmapDrawable)?.bitmap
                    bitmap?.let {
                        Palette.from(it).generate { palette ->
                            palette?.let { p ->
                                // Try to get the best dark color for the background
                                val colorInt = p.getDarkVibrantColor(
                                    p.getDarkMutedColor(
                                        p.getMutedColor(
                                            p.getDominantColor(defaultColor.hashCode())
                                        )
                                    )
                                )
                                rawExtractedColor = Color(colorInt)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Song • Spotify",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.Devices,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Real-time Progress Bar at the bottom
        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}
