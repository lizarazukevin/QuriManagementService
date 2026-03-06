plugins {
	kotlin("jvm") version "2.2.20"
	kotlin("plugin.spring") version "2.2.20"
	id("org.springframework.boot") version "4.0.0-RC1"
	id("io.spring.dependency-management") version "1.1.7"
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
}

tasks.withType<Test> {
	useJUnitPlatform()

	// TODO: Enable tests
	enabled = false
}