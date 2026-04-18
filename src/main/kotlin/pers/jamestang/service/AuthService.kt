package pers.jamestang.service

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import pers.jamestang.expand.data
import pers.jamestang.expand.error
import pers.jamestang.service.dto.RegistryReq
import pers.jamestang.tables.Users
import pers.jamestang.util.R

object AuthService {



    suspend fun register(
        ctx: RoutingContext
    ) {
        val payload = ctx.call.receive<RegistryReq>()
        val hashed = BCrypt.with(BCrypt.Version.VERSION_2B).hashToString(12, payload.rawPassword.toCharArray())

        suspendTransaction {
            val exists = Users
                .select(Users.id)
                .where { Users.username eq payload.username }
                .firstOrNull() != null

            if (exists) {
                return@suspendTransaction ctx.call.error("用户已存在")
            }

            val id = Users.insertAndGetId {
                it[Users.username] = username
                it[Users.passwordHash] = hashed
                it[Users.displayName] = displayName
                it[Users.email] = email
                it[Users.gender] = gender
            }

            ctx.call.data(id.value)
        }
    }
}