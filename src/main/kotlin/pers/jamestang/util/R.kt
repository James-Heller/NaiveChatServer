package pers.jamestang.util

import kotlinx.serialization.Serializable

@Serializable
data class R<T>(
    val code: Int,
    val data: T? = null,
    val msg: String
){

    companion object {
        fun ok() = R(200, data = null, "OK")
        inline fun <reified T> data(data: T) = R(200, data, "OK")
        fun error(msg: String) = R(500, data = null, msg = msg)
        fun error(code: Int, msg: String) = R(code = code, data = null, msg = msg)
    }
}
