package com.gaurav.spofiy.domain.usecase

import com.gaurav.spofiy.data.repoImpl.AuthResult

import com.gaurav.spofiy.domain.model.User
import com.gaurav.spofiy.domain.repo.SpotifyRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repository: SpotifyRepo) {
    operator fun invoke(user: User, password: String): Flow<AuthResult<String>> = repository.registerUser(user, password)
}

class LoginUseCase @Inject constructor(private val repository: SpotifyRepo) {
    operator fun invoke(email: String, password: String): Flow<AuthResult<String>> = repository.loginUser(email, password)
}

class GetCurrentUserUseCase @Inject constructor(private val repository: SpotifyRepo) {
    operator fun invoke(): Flow<AuthResult<User>> = repository.getCurrentUser()
}

class LogoutUseCase @Inject constructor(private val repository: SpotifyRepo) {
    operator fun invoke(): Flow<AuthResult<String>> = repository.logout()
}
