package com.gaurav.spofiy.domain.usecase

import com.gaurav.spofiy.domain.model.Track
import com.gaurav.spofiy.domain.repo.SpotifyRepo
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val repository: SpotifyRepo
) {
    suspend operator fun invoke(query: String = "Global Top Hits",type: String = "track"): List<Track> {
        val apiSongs = repository.searchTracks(query,type)
        val localSongs = repository.getLocalDummySongs()
        // Merge and remove duplicates by ID
        return (localSongs + apiSongs).distinctBy { it.id }
    }
}
