package pers.jamestang.tables

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object OnlineUser: IntIdTable("online_user") {
    val uid = integer("uid")
    val device_id = integer("device_id").nullable()
    val session_id = varchar("session_id", 255)
    val username = varchar("username", 255)
    val session = varchar("session", 255)

}