package pers.jamestang.tables

import pers.jamestang.expand.BaseTable


object Users: BaseTable("users") {
    val username = varchar("username", 64).uniqueIndex("username_unique")
    val passwordHash = varchar("password_hash", 64)
    val displayName = varchar("display_name", 64).nullable()
    val email = varchar("email", 64).nullable()
    val gender = varchar("gender", 1).nullable()
}