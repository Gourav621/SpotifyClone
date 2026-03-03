package com.gaurav.spofiy.domain.usecase

import com.gaurav.spofiy.domain.model.Track
import com.gaurav.spofiy.domain.repo.SpotifyRepo
import javax.inject.Inject

class SearchTracksUseCase @Inject constructor(
    private val repository: SpotifyRepo
) {
    suspend operator fun invoke(query: String,type: String): List<Track> {
        val apiResults = repository.searchTracks(query,type = type)
        val localResults = repository.getLocalDummySongs().filter { 
            it.name.contains(query, ignoreCase = true) 
        }
        return (localResults + apiResults).distinctBy { it.id }
    }
}

class GetInitialSongsUseCase @Inject constructor(
    private val repository: SpotifyRepo
) {
    suspend operator fun invoke(): List<Track> {
        val apiSongs = repository.searchTracks("Global Top Hits",type = "track")
        val localSongs = repository.getLocalDummySongs()
        return (localSongs + apiSongs).distinctBy { it.id }
    }
}
