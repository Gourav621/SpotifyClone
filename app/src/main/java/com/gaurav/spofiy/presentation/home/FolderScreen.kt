package com.gaurav.spofiy.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gaurav.spofiy.R

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen() {
    val cyan =Color(0xFF00E5FF)
    val categories = listOf("Folders", "Playlists", "Artists", "Albums", "Podcasts")
    Scaffold(topBar = {
        TopAppBar(title = { Text("your Library", color = cyan)},
            navigationIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    tint = cyan,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .width(63.dp).height(48.dp)
                )
            },
            actions = {
                Icon(Icons.Default.Search,contentDescription = null,tint = Color.White)
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )
    },  containerColor = Color.Black) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(it)) {
            item {
                LazyRow(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories){item->
                        FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text(text = item) })
                    }
                }
            }
            item {
                QuickActions()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun QuickActions() {
    TODO("Not yet implemented")
}