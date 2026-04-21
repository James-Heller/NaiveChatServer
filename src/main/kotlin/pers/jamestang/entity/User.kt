package pers.jamestang.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pers.jamestang.expand.BaseEntity
import pers.jamestang.tables.Users


class User(id: EntityID<Int>): BaseEntity(id, Users){


    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var displayName by Users.displayName
    var passwordHash by Users.passwordHash
    val email by Users.email
    val gender by Users.gender
}