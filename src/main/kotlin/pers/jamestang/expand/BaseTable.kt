package pers.jamestang.expand

import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

abstract class BaseTable(tableName: String): IntIdTable(tableName) {

    val createAt = datetime("create_at").defaultExpression(CurrentDateTime)
    val updateAt = datetime("update_at").defaultExpression(CurrentDateTime)
    val deleteAt = datetime("delete_at").nullable()
    val deleted = bool("deleted").default(false)
}