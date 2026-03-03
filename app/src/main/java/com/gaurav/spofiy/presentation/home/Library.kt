package com.gaurav.spofiy.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.gaurav.spofiy.R
import com.gaurav.spofiy.SpotifyViewModel
import com.gaurav.spofiy.presentation.navigation.Routes
import com.gaurav.spofiy.presentation.utils.MiniPlayer

import com.gaurav.spofiy.ui.theme.SpotifyGray
import com.gaurav.spofiy.ui.theme.getSpotifyProfileColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Library(navController: NavController, viewModel: SpotifyViewModel = hiltViewModel()) {
    val userData by viewModel.userData.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val realArtists by viewModel.apiSongs.collectAsState() // Using apiSongs as a proxy for artists
    
    val name = userData?.name ?: "Guest User"
    val firstLetter = name.firstOrNull()?.uppercase() ?: "G"
    val profileImage = userData?.profilePicture
    val profileColor = getSpotifyProfileColor(name)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val categories = listOf("Playlists", "Artists", "Albums", "Podcasts & Shows")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF121212),
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(Modifier.height(48.dp))
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(profileColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImage != null) {
                            AsyncImage(
                                model = profileImage,
                                contentDescription = null,
                                modifier = Modifier.clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = firstLetter,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "View Profile", color = SpotifyGray, fontSize = 14.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = Color.DarkGray)
                NavigationDrawerItem(
                    label = { Text("Log out", color = Color.Red) },
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Routes.Welcome.route) { popUpTo(0) }
                    },
                    icon = { Icon(Icons.Default.Logout, null, tint = Color.Red) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Your Library",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(profileColor),
                                contentAlignment = Alignment.Center
                            ) {
                                if (profileImage != null) {
                                    AsyncImage(
                                        model = profileImage,
                                        contentDescription = null,
                                        modifier = Modifier.clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(firstLetter, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Routes.Search.route) }) {
                            Icon(Icons.Default.Search, "Search", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Add, "Add", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                )
            },
            bottomBar = {
                Column {
                    if (currentTrack != null) {
                        MiniPlayer(
                            track = currentTrack!!,
                            viewModel = viewModel,
                            onClick = { navController.navigate("song/${currentTrack!!.id}") }
                        )
                    }
                    SpotifyBottomBar(navController)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
            ) {
                // Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChipItem(category)
                    }
                }

                // Sort Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Recents", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Library List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Pinned Liked Songs
                    item {
                        LibraryListItem(
                            LibraryItem("Liked Songs", "Playlist • 124 songs", null, isPinned = true)
                        ) {
                            // Navigate to Liked Songs
                        }
                    }
                    
                    // Real Artists from API
                    items(realArtists) { track ->
                        LibraryListItem(
                            LibraryItem(
                                title = track.artistName ?: "Unknown Artist",
                                subtitle = "Artist",
                                imageUrl = track.imageUrl,
                                isArtist = true
                            )
                        ) {
                            navController.navigate("artist/${track.artistName}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(text: String) {
    Surface(
        modifier = Modifier.height(32.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF282828)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, fontSize = 13.sp)
        }
    }
}

@Composable
fun LibraryListItem(item: LibraryItem, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val imageModifier = Modifier
            .size(64.dp)
            .clip(if (item.isArtist) CircleShape else RoundedCornerShape(4.dp))
        
        if (item.title == "Liked Songs") {
            Box(
                modifier = imageModifier.background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(Color(0xFF450AF5), Color(0xFFC4EFD9))
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Favorite, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        } else {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.musium)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.isPinned) {
                    Icon(
                        Icons.Default.PushPin,
                        null,
                        tint = Color(0xFF1DB954),
                        modifier = Modifier.size(12.dp).padding(end = 4.dp)
                    )
                }
                Text(
                    text = item.subtitle ?: "",
                    color = SpotifyGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

data class LibraryItem(
    val title: String,
    val subtitle: String?,
    val imageUrl: String?,
    val isArtist: Boolean = false,
    val isPinned: Boolean = false
)

@Preview(showBackground = true)
@Composable
fun LibraryPreview() {
    val navController = rememberNavController()
    Library(navController = navController)
}
