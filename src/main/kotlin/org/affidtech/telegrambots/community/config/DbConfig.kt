package org.affidtech.telegrambots.community.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database

val dataSource: HikariDataSource by lazy {
    HikariDataSource(HikariConfig().apply {
        jdbcUrl = appConfig.db.jdbcUrl
        username = appConfig.db.username
        password = appConfig.db.password
        driverClassName = appConfig.db.driverClassName
        maximumPoolSize = 10
        schema = appConfig.db.schema
    })
}

fun runLiquibase() {
    dataSource.connection.use { connection ->
        val database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(connection))
        Liquibase("db/changelog/db.changelog-master.yml", ClassLoaderResourceAccessor(), database).use {
            liquibase -> liquibase.update()
        }
    }
}

fun initDatabase() {
    runLiquibase() // Ensure schema migrations are applied first

    val schema = Schema("community_bot")
    SchemaUtils.setSchema(schema)
    
    Database.connect(dataSource) // Connect Exposed to the already-migrated database
}
