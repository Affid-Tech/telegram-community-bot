plugins {
    kotlin("jvm") version "2.0.21"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}
group = "org.affidtech"
version = "1.0-SNAPSHOT"

// Define version variables
val telegramBotsVersion = "8.0.0"
val jacksonVersion = "2.18.2"
val slf4jVersion = "2.0.10"
val exposedVersion = "0.57.0"
val hikariVersion = "5.0.1"
val liquibaseVersion = "4.30.0"
val postgresDriverVersion = "42.7.2"
val botEventsVersion = "1.0.0"
val arraysExtensionsVersion = "1.0.0"
val javalinVersion = "6.4.0"
val bouncycastleVersion = 1.78

application {
    mainClass.set("org.affidtech.telegrambots.community.Main")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Telegram Bot Java SDK
    implementation("org.telegram:telegrambots-webhook:$telegramBotsVersion"){
        exclude("org.bouncycastle", "bcprov-jdk18on")
        exclude("org.eclipse.jetty", "jetty-server")
        exclude("io.javalin.community.ssl", "ssl-plugin")
        exclude("io.javalin", "io.javalin:javalin")
    }
    implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("io.javalin.community.ssl:ssl-plugin:$javalinVersion")
    implementation("org.telegram:telegrambots-client:$telegramBotsVersion")
    implementation("org.telegram:telegrambots-extensions:$telegramBotsVersion")
    // Events extension
    implementation("com.github.Affid:telegram-bots-events:$botEventsVersion")

    // Yaml parsing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    // HikariCP for database connection pooling
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    implementation("org.slf4j:slf4j-simple:$slf4jVersion")

    // Exposed ORM by JetBrains
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.github.Affid:exposed-postgres-arrays-extensions:$arraysExtensionsVersion")

    implementation("org.liquibase:liquibase-core:$liquibaseVersion")

    // Database driver (replace `postgresql` with your specific database's driver)
    implementation("org.postgresql:postgresql:$postgresDriverVersion")

    // Testing (optional)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
