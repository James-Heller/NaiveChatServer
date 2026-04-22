package pers.jamestang.expand

import io.ktor.server.sessions.SessionStorage
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update
import pers.jamestang.tables.OnlineUser
import pers.jamestang.util.NaiveChatSession

class DatabaseSessionStorage: SessionStorage {
    override suspend fun invalidate(id: String) {
        suspendTransaction {
            OnlineUser.deleteWhere{ OnlineUser.session_id eq id }
        }
    }

    override suspend fun read(id: String): String {
        val session = suspendTransaction { OnlineUser.select(OnlineUser.session).where(OnlineUser.session_id eq id).map { it[OnlineUser.session] }.firstOrNull() }
        if (session == null) {
            throw NoSuchElementException("Session with id $id not found")
        }
        return session
    }

    override suspend fun write(id: String, value: String) {
        val _session = Json.decodeFromString<NaiveChatSession>(value)
        suspendTransaction {
            val existing = OnlineUser.select(OnlineUser.session, OnlineUser.session_id).where(OnlineUser.session_id eq id).firstOrNull()
            if (existing == null) {
                OnlineUser.insert {
                    it[uid] = _session.id
                    it[username] = _session.username
                    it[session_id] = id
                    it[session] = value

                }
            } else {
                OnlineUser.update({ OnlineUser.session_id eq id }) {
                    it[session] = value
                }
            }
        }
    }
}