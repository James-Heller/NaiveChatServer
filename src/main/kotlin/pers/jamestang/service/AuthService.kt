package pers.jamestang.service

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import pers.jamestang.entity.User
import pers.jamestang.expand.data
import pers.jamestang.expand.error
import pers.jamestang.expand.ok
import pers.jamestang.service.dto.AuthMeResp
import pers.jamestang.service.dto.LoginReq
import pers.jamestang.service.dto.RegistryReq
import pers.jamestang.tables.Users
import pers.jamestang.util.NaiveChatSession

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
                it[Users.username] = payload.username
                it[Users.passwordHash] = hashed
                it[Users.displayName] = payload.displayName
                it[Users.email] = payload.email
                it[Users.gender] = payload.gender
            }

            ctx.call.data(id.value)
        }
    }

    suspend fun login(ctx: RoutingContext){
        val payload = ctx.call.receive<LoginReq>()

        val dbUser = suspendTransaction {
            Users
                .select(Users.id, Users.username, Users.passwordHash)
                .where { Users.username eq payload.username }
                .firstOrNull()
        }

        if (dbUser == null){
            ctx.call.error("用户不存在")
            return
        }

        val result = BCrypt.verifyer(BCrypt.Version.VERSION_2B)
            .verify(payload.password.toCharArray(), dbUser[Users.passwordHash].toCharArray())

        if (result.verified){
            ctx.call.sessions.set(NaiveChatSession(dbUser[Users.id].value, dbUser[Users.username]))
            ctx.call.ok()
        }else{
            ctx.call.error(401, "用户名或密码错误")
        }
    }

    suspend fun profile(ctx: RoutingContext){
        val principal = ctx.call.sessions.get<NaiveChatSession>()!!
        val info = suspendTransaction{User.findById(principal.id)}

        if (info == null) {
            ctx.call.error("这不应该发生，请联系管理员")
            return
        }




        ctx.call.data(AuthMeResp(
            info.username,
            info.displayName,
            info.email,
            info.gender,
        ))

    }
}