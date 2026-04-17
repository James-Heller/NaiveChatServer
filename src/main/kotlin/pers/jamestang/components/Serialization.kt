package pers.jamestang.components

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import pers.jamestang.expand.jackson3

fun Application.initializeSerialization() {
    install(ContentNegotiation) {
        clearIgnoredTypes()
        jackson3()
    }

}