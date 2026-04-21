package pers.jamestang.service.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegistryReq(
    val username: String,
    val rawPassword: String,
    val displayName: String?,
    val email: String?,
    val gender: String?
)

@Serializable
data class LoginReq(
    val username: String,
    val password: String,
)
@Serializable
data class AuthMeResp (
    val username: String,
    val displayName: String?,
    val email: String?,
    val gender: String?
)