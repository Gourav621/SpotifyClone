package com.gaurav.spofiy.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.gaurav.spofiy.R
import com.gaurav.spofiy.presentation.viewmodel.SpotifyViewModel
import com.gaurav.spofiy.domain.model.Track

import com.gaurav.spofiy.presentation.navigation.Routes
import com.gaurav.spofiy.presentation.utils.MiniPlayer
import com.gaurav.spofiy.ui.theme.SpotifyGray
import com.gaurav.spofiy.ui.theme.getSpotifyProfileColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: SpotifyViewModel = hiltViewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userData by viewModel.userData.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val apiSongs by viewModel.apiSongs.collectAsState()

    val name = userData?.name ?: "Guest User"
    val firstLetter = name.firstOrNull()?.uppercase() ?: "G"
    val profileColor = getSpotifyProfileColor(name)
    var showAddAccountDialog by remember { mutableStateOf(false) }


    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
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
                        Text(firstLetter, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("View Profile", color = SpotifyGray, fontSize = 13.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = Color.DarkGray)

                NavigationDrawerItem(
                    label = { Text("Add account", color = Color.White) },
                    selected = false,
                    onClick = {


                    },
                    icon = { Icon(Icons.Default.Add, null, tint = Color.White) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                NavigationDrawerItem(
                    label = { Text("What's new", color = Color.White) },
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Bolt, null, tint = Color.White) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                NavigationDrawerItem(
                    label = { Text("Settings and privacy", color = Color.White) },
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Settings, null, tint = Color.White) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                Spacer(Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Log out", color = Color.Red) },
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Routes.Welcome.route) { popUpTo(0) { inclusive = true } }
                    },
                    icon = { Icon(Icons.AutoMirrored.Default.Logout, null, tint = Color.Red) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Black,
            bottomBar = {
                Column {
                    if (currentTrack != null) {
                        MiniPlayer(
                            track = currentTrack!!,
                            viewModel = viewModel,
                            onClick = { navController.navigate("song/${currentTrack!!.id}") }
                        )
                    }
                    SpotifyBottomBar(navController = navController)
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFF0B2F32), Color.Black)))
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    HomeHeader(name, profileColor) { scope.launch { drawerState.open() } }
                    Spacer(Modifier.height(24.dp))
                }

                // 1. Grid: Continue Listening (Dummy take 6)
                item {
                    Text("Continue Listening", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                }

                val songs = viewModel.dummySongs.take(6)


                items(songs.chunked(2)) { rowSongs ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSongs.forEach { song ->
                            RowCard(song, Modifier.weight(1f)) {
                                viewModel.onSongClick(song)
                            }
                        }

                        if (rowSongs.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }

                item{
                    // 2. Row: Recently Played (Dynamic reversed)
                    if (recentlyPlayed.isNotEmpty()) {
                        HomeRowSection("Recently Played", recentlyPlayed) { viewModel.onSongClick(it) }
                    }

                    // 3. Row: Top Mixes (Dummy start)
                    HomeRowSection("Your Top Mixes", viewModel.dummySongs, onTrackClick = {viewModel.onSongClick(it)})

                    // 4. Row: API Songs (Start of list)
                    if (apiSongs.isNotEmpty()) {
                        HomeRowSection("Trending API Hits", apiSongs) { viewModel.onSongClick(it) }
                    }

                    // 5. Row: Last Top Mixes (Dummy reversed)
                    HomeRowSection("Jump Back In", viewModel.dummySongs.reversed()) { viewModel.onSongClick(it) }

                    // 6. Row: API Global Hits (API reversed)
                    if (apiSongs.isNotEmpty()) {
                        HomeRowSection("Global API Charts", apiSongs.reversed()) { viewModel.onSongClick(it) }
                    }
                }

                item { Spacer(Modifier.height(120.dp)) }
            }
        }
    }
}

@Composable
fun HomeHeader(name: String, profileColor: Color, onProfileClick: () -> Unit) {
    var selectedTab by remember { mutableStateOf("All") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(35.dp).clip(CircleShape).background(profileColor).clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(name.firstOrNull()?.uppercase() ?: "G", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))

            FilterChip(
                selected = selectedTab == "All",
                onClick = { selectedTab = "All" },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1DB954),
                    selectedLabelColor = Color.Black,
                    containerColor = Color(0xFF282828),
                    labelColor = Color.White
                ),
                border = null,
                shape = RoundedCornerShape(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = selectedTab == "Music",
                onClick = { selectedTab = "Music" },
                label = { Text("Music") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1DB954),
                    selectedLabelColor = Color.Black,
                    containerColor = Color(0xFF282828),
                    labelColor = Color.White
                ),
                border = null,
                shape = RoundedCornerShape(20.dp)
            )

        }
        Row {
            Icon(Icons.Default.Notifications, null, tint = Color.White)
            Spacer(Modifier.width(16.dp))
            Icon(Icons.Default.History, null, tint = Color.White)
            Spacer(Modifier.width(16.dp))
            Icon(Icons.Default.Settings, null, tint = Color.White)
        }
    }
}

@Composable
fun HomeRowSection(title: String, tracks: List<Track>, onTrackClick: (Track) -> Unit) {
    Column {
        Spacer(Modifier.height(24.dp))
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(tracks) { track ->
                AlbumCard(track) { onTrackClick(track) }
            }
        }
    }
}

@Composable
fun RowCard(track: Track, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier.height(56.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF282828)).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.imageUrl ?: R.drawable.musium,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(8.dp))
        Text(track.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun AlbumCard(track: Track, onClick: () -> Unit) {
    Column(Modifier.width(155.dp).clickable { onClick() }) {
        AsyncImage(
            model = track.imageUrl ?: R.drawable.musium,
            contentDescription = null,
            modifier = Modifier.size(155.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text(track.name, color = Color.White, maxLines = 1, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(track.artistName ?: "Unknown", color = SpotifyGray, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
fun SpotifyBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(
        containerColor = Color.Black.copy(alpha = 0.95f),
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = currentRoute == Routes.Home.route,
            onClick = {
                if (currentRoute != Routes.Home.route) {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = SpotifyGray,
                indicatorColor = Color.Transparent,
                selectedTextColor = Color.White,
                unselectedTextColor = SpotifyGray
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Search.route,
            onClick = {
                if (currentRoute != Routes.Search.route) {
                    navController.navigate(Routes.Search.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Search, null) },
            label = { Text("Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = SpotifyGray,
                indicatorColor = Color.Transparent,
                selectedTextColor = Color.White,
                unselectedTextColor = SpotifyGray
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Library.route,
            onClick = {
                if (currentRoute != Routes.Library.route) {
                    navController.navigate(Routes.Library.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Default.LibraryMusic, null) },
            label = { Text("Your Library") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = SpotifyGray,
                indicatorColor = Color.Transparent,
                selectedTextColor = Color.White,
                unselectedTextColor = SpotifyGray
            )
        )
    }
}


