package pers.jamestang.routes

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pers.jamestang.service.AuthService

fun Route.authRoutes() {
    route("/v1/auth"){
        post("/register", AuthService::register)
        post("/login", AuthService::login)

        authenticate {
            get("profile", AuthService::profile)
        }
    }
}