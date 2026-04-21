package pers.jamestang

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import pers.jamestang.routes.authRoutes

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
	routing {
		authRoutes()
	}
}
