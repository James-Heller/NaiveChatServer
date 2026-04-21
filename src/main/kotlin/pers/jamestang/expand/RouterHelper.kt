package pers.jamestang.expand

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import pers.jamestang.util.R

suspend fun RoutingCall.ok() = this.respond(HttpStatusCode.OK, R.ok())
suspend inline fun <reified T> RoutingCall.data(data: T) = this.respond(HttpStatusCode.OK, R.data(data))
suspend fun RoutingCall.error(message: String) = this.respond(HttpStatusCode.InternalServerError, R.error(message))
suspend fun RoutingCall.error(code: Int, message: String) = this.respond(HttpStatusCode.fromValue(code), R.error(code, message))
