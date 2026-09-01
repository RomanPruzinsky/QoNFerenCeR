plugins {
	kotlin("jvm") version "2.4.10"
	kotlin("plugin.spring") version "2.4.10"
	id("org.springframework.boot") version "3.5.16"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.4.10"
	id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "tr.qonferencer"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("tr.qonferencer:shared")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.keycloak:keycloak-admin-client:26.0.12")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
	// None of these have an app-level default (must crash if unset in real deployments) — tests
	// supply their own values the same way docker-compose does, via the process environment.
	// SPRING_DATASOURCE_* is exempt: Testcontainers' @ServiceConnection overrides it outright.
	environment(
		mapOf(
			"EVENT_ID" to "test",
			"KC_ISSUER" to "http://localhost:8080/realms/qonferencer",
			"KC_JWK_SET_URI" to "http://localhost:8080/realms/qonferencer/protocol/openid-connect/certs",
			"KC_ADMIN_URL" to "http://localhost:8080",
			"KC_BEADMIN_CLIENT_SECRET" to "test",
			"N8N_ENABLED" to "false",
			"N8N_BASE_URL" to "http://localhost:5678/webhook",
			"N8N_PATH_PREFIX" to "qonferencer_base",
			"BE_N8N_COMMS__AUTH_TOKEN" to "",
			"N8N_TIMEOUT_MS" to "3000",
		),
	)
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
