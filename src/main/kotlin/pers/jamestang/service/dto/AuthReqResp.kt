package pers.jamestang.service.dto

data class RegistryReq(
    val username: String,
    val rawPassword: String,
    val displayName: String?,
    val email: String?,
    val gender: String?
)