package pers.jamestang.util

import kotlinx.serialization.Serializable

@Serializable
data class NaiveChatSession(
    val id: Int,
    val username: String,

)
