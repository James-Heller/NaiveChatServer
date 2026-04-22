package pers.jamestang.components

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.respond
import io.ktor.server.sessions.*
import io.ktor.util.hex
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import pers.jamestang.expand.DatabaseSessionStorage
import pers.jamestang.tables.OnlineUser
import pers.jamestang.tables.Users
import pers.jamestang.util.NaiveChatSession
import pers.jamestang.util.R

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
        SchemaUtils.create(Users, OnlineUser)
    }


    monitor.subscribe(ApplicationStopped){
        dataSource.close()
    }
}

private fun Application.initializeAuthorization() {
    val secret = environment.config.config("session")
    val signKey = secret.property("secret").getString()
    val encryptionKey = secret.property("encryptionKey").getString()
    install(Authentication) {
        session<NaiveChatSession>(){
            validate {
                
            }

            challenge {
                call.respond(R.error(401, "未登录"))
            }
        }
    }
    install(Sessions) {
        val signKey = hex(signKey)
        val hashKey = hex(encryptionKey)
    header<NaiveChatSession>("NC_SESSION", DatabaseSessionStorage()) {
        transform(SessionTransportTransformerEncrypt(hashKey, signKey))
    }
}
}

fun Application.initializeSerialization() {
    install(ContentNegotiation) {
        json(json = Json {
            explicitNulls = false
            ignoreUnknownKeys = true

        }, contentType = ContentType.Application.Json)
    }

}