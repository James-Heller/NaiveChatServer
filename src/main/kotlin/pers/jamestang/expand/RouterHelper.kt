package pers.jamestang.expand

import io.ktor.server.response.respond
import io.ktor.server.routing.*
import pers.jamestang.util.R

suspend fun RoutingCall.ok() = this.respond(R.ok())
suspend inline fun <reified T> RoutingCall.data(data: T) = this.respond(R.data(data))
suspend fun RoutingCall.error(message: String) = this.respond(R.error(message))
suspend fun RoutingCall.error(code: Int, message: String) = this.respond(R.error(code, message))