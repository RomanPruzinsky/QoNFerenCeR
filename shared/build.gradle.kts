plugins {
	kotlin("jvm") version "2.4.10"
	id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "tr.qonferencer"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.22")
	testImplementation(kotlin("test"))
}

val generateApiVersion = tasks.register("generateApiVersion") {
	val envFile = layout.projectDirectory.file("../config/QoNFerenCeR.env")
	val outputDir = layout.buildDirectory.dir("generated/apiVersion")
	
	inputs.file(envFile)
	outputs.dir(outputDir)
	
	doLast {
		val apiVersion = envFile.asFile.readLines()
			.first { it.startsWith("API_VERSION=") }
			.substringAfter("=")
			.trim()
		
		outputDir.get().file("tr/qonferencer/shared/GeneratedApiVersion.kt").asFile.apply {
			parentFile.mkdirs()
			writeText(
				"""
				package tr.qonferencer.shared

				internal const val API_VERSION = "$apiVersion"
				""".trimIndent() + "\n",
			)
		}
	}
}

sourceSets.main {
	kotlin.srcDir(generateApiVersion)
}

kotlin {
	compilerOptions {
		jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
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
