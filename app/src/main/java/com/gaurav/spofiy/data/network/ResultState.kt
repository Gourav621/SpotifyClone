package com.gaurav.spofiy.data.network

sealed class AuthState {
    object Authenticate : AuthState()
    object Unauthenticated : AuthState()
    object isLoading : AuthState()
    data class isError(val message: String) : AuthState()
}

