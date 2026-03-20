import dev.detekt.gradle.Detekt

plugins {
	kotlin("jvm") version "2.2.20"
	kotlin("plugin.spring") version "2.2.20"
	id("org.springframework.boot") version "4.0.0-RC1"
	id("io.spring.dependency-management") version "1.1.7"
	id("dev.detekt") version("2.0.0-alpha.1")
}

group = "com.quri"
version = "0.1.0"
description = "Central management service for Quri."

kotlin {
	jvmToolchain(21)
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

detekt {
	config.setFrom(file("config/detekt/detekt.yml"))
	buildUponDefaultConfig = true
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring - DI container only, no web server
	implementation("org.springframework.boot:spring-boot-starter")

	// Smithy server
	implementation(libs.quri.models)
	implementation(libs.smithy.java.server.core)
	implementation(libs.smithy.java.server.netty)
	implementation(libs.smithy.java.aws.server.restjson)

	// MongoDB - https://www.mongodb.com/docs/drivers/kotlin/coroutine/current/getting-started/
	implementation(libs.mongodb.driver.kotlin.coroutine)
	implementation(libs.mongodb.bson.kotlinx)

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test")

	// Detekt formatter rules --> https://detekt.dev/docs/intro
	detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:2.0.0-alpha.1")
}

tasks.withType<Detekt>().configureEach {
	reports {
		checkstyle.required.set(true)
		html.required.set(true)
		sarif.required.set(true)
		markdown.required.set(true)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()

	// TODO: Enable tests
	enabled = false
}