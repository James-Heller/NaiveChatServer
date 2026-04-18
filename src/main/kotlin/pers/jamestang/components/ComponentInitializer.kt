package pers.jamestang.components

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.componentInitializer(){

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
}