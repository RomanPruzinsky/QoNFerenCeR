plugins {
	// Kotlin 2.3.21 (same as android) — must support BOTH the android build's
	// Gradle 9.2.1 and the backend build's Gradle 8.14.5, since a composite
	// build compiles this module with each consumer's own Gradle.
	kotlin("jvm") version "2.3.21"
	id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "tr.qonferencer"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))
}

// JVM 11 is the lowest common denominator: the android app compiles to Java 11,
// the backend to Java 21. Targeting 11 keeps this module's bytecode consumable
// by both. DTOs / API / constants need no newer language level.
kotlin {
	compilerOptions {
		jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
	useJUnitPlatform()
}

ktlint {
	version.set("1.8.0")
	ignoreFailures.set(false)
	verbose.set(true)
	outputToConsole.set(true)
	reporters {
		reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
		reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
	}
	filter {
		exclude("**/generated/**")
		exclude("**/build/**")
	}
}
