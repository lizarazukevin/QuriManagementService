package com.quri.management.clients

import com.mongodb.kotlin.client.coroutine.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Provides a configured [MongoClient] bean for the application context.
 *
 * Connection parameters are sourced from environment variables:
 * - `MONGO_USERNAME` — database user
 * - `MONGO_PASSWORD` — database password
 * - `MONGO_CLUSTER` — Atlas cluster hostname (e.g. `cluster0.abc12.mongodb.net`)
 * - `MONGO_APP_NAME` — application name shown in Atlas monitoring
 *
 * For local development, set these in your IDE's run configuration environment variables.
 * For production, inject via your secrets manager (e.g. AWS Secrets Manager, Vault).
 */
@Configuration
class MongoClientProvider {

    @Bean
    fun mongoClient(): MongoClient {
        val username = requireEnv("MONGO_USERNAME")
        val password = requireEnv("MONGO_PASSWORD")
        val cluster = requireEnv("MONGO_CLUSTER")
        val appName = requireEnv("MONGO_APP_NAME")

        val uri = "mongodb+srv://$username:$password@$cluster/?appName=$appName"

        return MongoClient.create(uri)
    }

    /**
     * Retrieves a required environment variable, failing fast at startup if missing.
     *
     * Prefer this over [System.getenv] directly to surface misconfiguration
     * immediately rather than receiving a null-related error deep in the call stack.
     *
     * @param name the environment variable name
     * @throws IllegalStateException if the variable is not set or blank
     */
    private fun requireEnv(name: String): String =
        System.getenv(name)?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "Required environment variable '$name' is not set. " +
                        "Check your run configuration or deployment secrets."
            )
}