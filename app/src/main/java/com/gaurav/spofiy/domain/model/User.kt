package com.gaurav.spofiy.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profilePicture: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
