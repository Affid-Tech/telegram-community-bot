package org.affidtech.telegrambots.exposed.posgtres

import org.jetbrains.exposed.sql.ArrayColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryBuilder

fun <T> Column<List<T>?>.contains(value: T): Op<Boolean> {
    val columnName = this.name
    val table = this.table.tableName
    val sqlType = this.columnType.sqlType()

    return object : Op<Boolean>() {
        override fun toQueryBuilder(queryBuilder: QueryBuilder) {
            // Use parameterized query for safety and correctness
            queryBuilder.append("$table.$columnName @> ARRAY[")
            queryBuilder.registerArgument(
                // Ensure types are properly registered
                columnType, listOf(value) // Wrap the value in a List to match the expected type
            )
            queryBuilder.append("]::$sqlType")
        }
    }
}

fun <T> Column<List<T>?>.append(item: T): Op<List<T>> {
    val columnName = this.name
    val table = this.table.tableName
    val sqlType = (this.columnType as ArrayColumnType<*,*>).delegate

    return object : Op<List<T>>() {
        override fun toQueryBuilder(queryBuilder: QueryBuilder) {
            // Use the array_append PostgreSQL function
            queryBuilder.append("array_append($table.$columnName,")
            queryBuilder.registerArgument(sqlType, item)
            queryBuilder.append(")")
        }
    }
}

fun <T> Column<List<T>?>.remove(item: T): Op<List<T>> {
    val columnName = this.name
    val table = this.table.tableName
    val sqlType = (this.columnType as ArrayColumnType<*,*>).delegate

    return object : Op<List<T>>() {
        override fun toQueryBuilder(queryBuilder: QueryBuilder) {
            // Use the array_append PostgreSQL function
            queryBuilder.append("array_remove($table.$columnName,")
            queryBuilder.registerArgument(sqlType, item)
            queryBuilder.append(")")
        }
    }
}
