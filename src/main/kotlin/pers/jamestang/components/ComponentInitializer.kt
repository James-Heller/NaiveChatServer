package pers.jamestang.components

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import pers.jamestang.expand.jackson3
import pers.jamestang.tables.Users

fun Application.componentInitializer() {
    initializeDatabase()
    initializeAuthorization()
    initializeSerialization()
}


private fun Application.initializeDatabase() {
    val db = this.environment.config.config("postgres")
    val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = db.property("url").getString()
        username = db.property("user").getString()
        password = db.property("password").getString()
        maximumPoolSize = 20

    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(Users)
    }


    monitor.subscribe(ApplicationStopped){
        dataSource.close()
    }
}

private fun Application.initializeAuthorization() {

    val cfg = environment.config.config("jwt")

    val jwtAudience = cfg.property("audience").getString()
    val jwtDomain = cfg.property("domain").getString()
    val jwtRealm = "NaiveChat"
    val jwtSecret = cfg.property("secret").getString()
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

fun Application.initializeSerialization() {
    install(ContentNegotiation) {
        clearIgnoredTypes()
        jackson3()
    }

}