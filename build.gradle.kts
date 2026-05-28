import dev.detekt.gradle.Detekt

plugins {
	alias(libs.plugins.detekt)
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.spring)
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency)
	alias(libs.plugins.kover)
}

group = "com.quri"
version = "0.1.0"
description = "Central management service for Quri."

// ── Kotlin Compiler ───────────────────────────────────────────────────────────
kotlin {
	jvmToolchain(21)
	compilerOptions {
		// Treat platform types (Java nullability) as errors rather than warnings.
		// param-property enables annotation targets on constructor parameters.
		freeCompilerArgs.addAll(
			"-Xjsr305=strict",
			"-Xannotation-default-target=param-property")
	}
}

// ── Detekt (static analysis + ktlint formatting) ──────────────────────────────
// Ref: https://detekt.dev/docs/2.0.0-alpha.1/intro
detekt {
	config.setFrom(file("config/detekt/detekt.yml"))
	buildUponDefaultConfig = true
}

repositories {
	mavenCentral()
}

// ── Dependencies ──────────────────────────────────────────────────────────────
// Spring Boot BOM manages versions for all spring.* and jackson.* artifacts.
// Do not specify versions for Spring dependencies — let the BOM resolve them.
dependencies {
	// ── Spring ────────────────────────────────────────────────────────────────
	implementation(libs.spring.boot.starter)
	implementation(libs.spring.boot.starter.oauth2.resource.server)
	implementation(libs.spring.boot.starter.security)
	implementation(libs.spring.boot.starter.webflux)

	// ── Kotlin ────────────────────────────────────────────────────────────────
	// Required for WebFlux to bridge suspend functions into Reactor Mono/Flux.
	implementation(libs.kotlinx.coroutines.reactor)

	// ── Internal ──────────────────────────────────────────────────────────────
	implementation(libs.quri.models)
	implementation(libs.smithy.java.core)

	// ── Database ──────────────────────────────────────────────────────────────
	// Ref: https://www.mongodb.com/docs/drivers/kotlin/coroutine/current/getting-started/
	implementation(libs.mongodb.driver.kotlin.coroutine)
	implementation(libs.mongodb.bson.kotlinx)

	// ── Observability ─────────────────────────────────────────────────────────
	// Formats logback logs to structured JSON logging for analysis
	// Ref: https://github.com/logfellow/logstash-logback-encoder
	implementation(libs.logstash.logback.encoder)

	// ── Test ──────────────────────────────────────────────────────────────────
	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.kotest.runner.junit5)
	testImplementation(libs.kotest.assertions.core)
	testImplementation(libs.kotest.property)
	testImplementation(libs.mockk)

	testImplementation(libs.spring.boot.starter.test) {
		exclude(module = "mockito-core")
	}

	// ── Code Quality ──────────────────────────────────────────────────────────
	// Ref: https://detekt.dev/docs/intro
	detektPlugins(libs.detekt.rules.ktlint.wrapper)

	// ── Other ──────────────────────────────────────────────────────────
	// Netty dns resolver for macos arm64
	// Ref: https://github.com/netty/netty/issues/11020
	runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.2.12.Final:osx-aarch_64")
}

// ── Detekt Reports ────────────────────────────────────────────────────────────
tasks.withType<Detekt>().configureEach {
	reports {
		checkstyle.required.set(true)
		html.required.set(true)
		sarif.required.set(true)
		markdown.required.set(true)
	}
}

// ── Test ──────────────────────────────────────────────────────────────────────
tasks.withType<Test> {
	useJUnitPlatform()
}

// ── Coverage ──────────────────────────────────────────────────────────────────
kover {
	reports {
		total {
			html {
				onCheck = false
			}
			verify {
				rule {
					minBound(60)
				}
			}
		}
		filters {
			excludes {
				classes(
					// exclude Spring wiring and config from coverage stats
					"*.QuriManagementServiceApplication*",
					"*.SecurityConfig*",
					"*.SmithyJacksonConfig*",
					"*.MongoClientProvider*",
					"*.MongoDatabaseProvider*",
				)
			}
		}
	}
}