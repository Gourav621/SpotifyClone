package com.gaurav.spofiy.di

import android.content.Context
import com.gaurav.spofiy.data.network.ApiService
import com.gaurav.spofiy.data.network.TokenManager
import com.gaurav.spofiy.data.repoImpl.SpotifyRepositoryImpl
import com.gaurav.spofiy.domain.repo.MusicPlayerManager
import com.gaurav.spofiy.domain.repo.SpotifyRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
//
//import com.gaurav.spofiy.domain.repo.MusicPlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {





    @Provides
    @Singleton
    fun provideMusicPlayerManager(@ApplicationContext context: Context): MusicPlayerManager =
        MusicPlayerManager(context)
    @Provides
    @Singleton
    fun provideSpotifyRepo(firebaseAuth: FirebaseAuth,
                           firebaseFirestore: FirebaseFirestore,
                           apiService: ApiService,tokenManager: TokenManager):
            SpotifyRepo =
        SpotifyRepositoryImpl(firebaseAuth,
            firebaseFirestore, apiService,tokenManager)
}
