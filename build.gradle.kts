import dev.detekt.gradle.Detekt

plugins {
	alias(libs.plugins.detekt)
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.spring)
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency)
	alias(libs.plugins.kover)
	idea
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

// ── Source Sets ───────────────────────────────────────────────────────────────
// Creates isolated source set for infra-backed integration tests
// Inherits code boundaries + testing libraries from standard unit tests
val integrationSourceSet = sourceSets.create("integration") {
	kotlin {
		compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
		runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
	}
}

// Inherits test implementation and runtime libraries into integration suite
configurations[integrationSourceSet.implementationConfigurationName]
	.extendsFrom(configurations.testImplementation.get())
configurations[integrationSourceSet.runtimeOnlyConfigurationName]
	.extendsFrom(configurations.testRuntimeOnly.get())

// Forces IntelliJ IDEA to recognize this custom set as a designated test src root
// a.k.a. turns the folder green
idea {
	module {
		testSources.from(integrationSourceSet.kotlin.srcDirs)
		testResources.from(integrationSourceSet.resources.srcDirs)
	}
}

// ── Detekt (static analysis + ktlint formatting) ──────────────────────────────
// Ref: https://detekt.dev/docs/2.0.0-alpha.1/intro
detekt {
	config.setFrom(file("config/detekt/detekt.yml"))
	buildUponDefaultConfig = true
	source.from(
		sourceSets.main.get().kotlin,
		sourceSets.test.get().kotlin,
		integrationSourceSet.kotlin
	)
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
	testImplementation(libs.spring.boot.starter.test) {
		exclude(module = "mockito-core")
	}
	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.kotest.runner.junit5)
	testImplementation(libs.kotest.assertions.core)
	testImplementation(libs.kotest.property)
	testImplementation(libs.mockk)

	// ── Integration Test ──────────────────────────────────────────────────────
	val integrationImplementation by configurations

	// Support for constructor dependency injection, bridges Kotest's coroutine execution model
	// with Spring's requirement for JUnit, enabling proper application context + caching
	// Ref: https://kotest.io/docs/extensions/spring.html
	integrationImplementation(libs.kotest.extensions.spring)

	// Target and replace a specific Spring bean inside the active application context with MockK mock
	// Ref: https://github.com/Ninja-Squad/springmockk
	integrationImplementation(libs.springmockk)

	// Manage services running inside containers, integrates with JUnit to start up containers before
	// tests run and are practical for writing integration tests against a real backend service (e.g. Mongo, MySQL)
	// Ref: https://docs.spring.io/spring-boot/reference/testing/testcontainers.html
	integrationImplementation(libs.spring.boot.testcontainers)

	// Downloads, starts, and stops genuine MongoDB service inside isolated container
	// Ref: https://testcontainers.com/modules/mongodb/
	integrationImplementation(libs.testcontainers.mongodb)

	// MVC related tests, tests the web controller layer
	integrationImplementation(libs.spring.boot.starter.webflux.test)

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
tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

// Registers integration test suite, runs sequentially after local unit tests have passed
tasks.register<Test>("integration") {
	description = "Runs integration tests against real infrastructure"
	group = "verification"
	testClassesDirs = integrationSourceSet.output.classesDirs
	classpath = integrationSourceSet.runtimeClasspath
	shouldRunAfter("test")
}

// ── Coverage ──────────────────────────────────────────────────────────────────
kover {
	reports {
		total {
			html { onCheck = false }
			verify {
				rule { minBound(90) }
			}
		}
		filters {
			excludes {
				classes(
					// exclude Spring wiring and config from coverage stats
					"*.QuriManagementServiceApplication*",
					"*.SecurityConfig*",
					"*.SmithyJacksonConfig*",
					"*.serialization*",	// managed by Smithy
					"*.MongoClientProvider*",
					"*.MongoDatabaseProvider*",
					"*.TestMongoDatabaseProvider*",
					"*.MongoSchema*",
				)
			}
		}
	}
}