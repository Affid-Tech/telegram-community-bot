package org.affidtech.telegrambots.community.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.InputStream
import java.util.regex.Pattern

fun parseYamlWithEnvVariables(yamlContent: String): String {
    val envPattern = Pattern.compile("""\$\{([a-zA-Z_][a-zA-Z0-9_]*)?(:([^}]*))?}""")
    return envPattern.matcher(yamlContent).replaceAll { match ->
        val envVar = match.group(1)
        val defaultValue = match.group(3)
        System.getenv(envVar) ?: defaultValue ?: throw IllegalArgumentException("Missing environment variable: $envVar")
    }
}

fun <T> loadConfig(configClass: Class<T>, resourcePath: String = "application.yml"): T {
    val resourceStream: InputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
        ?: throw IllegalArgumentException("Resource not found: $resourcePath")

    // Read the resource content as a string
    val yamlContent = resourceStream.bufferedReader().use { it.readText() }.let { parseYamlWithEnvVariables(it) }

    val mapper = ObjectMapper(YAMLFactory()).apply {
        registerModule(KotlinModule.Builder().enable(KotlinFeature.NullIsSameAsDefault).build()) // For Kotlin data class support
    }

    return mapper.readValue(yamlContent, configClass)
}
