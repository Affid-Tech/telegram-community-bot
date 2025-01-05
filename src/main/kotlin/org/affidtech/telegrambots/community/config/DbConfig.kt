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
        jdbcUrl = "jdbc:postgresql://localhost:5432/community_bot"
        username = "admin"
        password = "admin"
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 10
        schema = "public"
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
    
    Database.connect(dataSource) // Connect Exposed to the already-migrated database
}