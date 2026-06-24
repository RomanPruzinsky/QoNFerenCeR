plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "backend"

// Shared DTO / API / constants module — composite build, lives in /shared.
includeBuild("../shared")
