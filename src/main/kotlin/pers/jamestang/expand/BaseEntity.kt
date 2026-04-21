package pers.jamestang.expand

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity

abstract class BaseEntity(
    id: EntityID<Int>,
    table: BaseTable,
) : IntEntity(id) {

    val createAt by table.createAt
    var updateAt by table.updateAt
    var deleteAt by table.deleteAt
    var deleted by table.deleted
}

