plugins {
    kotlin("jvm") version "2.0.21"
    id("application")
}

group = "org.affidtech"
version = "1.0-SNAPSHOT"


application {
    mainClass.set("org.affidtech.telegrambots.Main.kt")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Ktor Server Framework
    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")

    // Telegram Bot Java SDK
    implementation("org.telegram:telegrambots-webhook:8.0.0")
    implementation("org.telegram:telegrambots-client:8.0.0")
    implementation("org.telegram:telegrambots-extensions:8.0.0")
    //Events extension
    implementation("com.github.Affid:telegram-bots-events:1.0.0")

    //Yaml parsing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")

    // HikariCP for database connection pooling
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("org.slf4j:slf4j-simple:2.0.10")

    // Exposed ORM by JetBrains
    implementation("org.jetbrains.exposed:exposed-core:0.57.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.57.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.57.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.57.0") // Support for Java Time API
    implementation("com.github.Affid:exposed-postgres-arrays-extensions:1.0.0")

    implementation("org.liquibase:liquibase-core:4.30.0")


    // Database driver (replace `postgresql` with your specific database's driver)
    implementation("org.postgresql:postgresql:42.6.0")

    // Testing (optional)
    testImplementation("io.ktor:ktor-server-tests:2.3.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}