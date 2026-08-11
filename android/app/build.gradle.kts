import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ktlint)
}

val envLines = rootDir.resolve("../config/QoNFerenCeR.env").readLines()
fun envValue(key: String): String = envLines.firstOrNull { it.startsWith("$key=") }
	?.substringAfter("=")
	?.trim()
	?: throw GradleException("Missing '$key' in config/QoNFerenCeR.env")

val eventId = envValue("EVENT_ID").also {
	require(Regex("[a-zA-Z][a-zA-Z0-9_]*").matches(it)) {
		"EVENT_ID='$it' in config/QoNFerenCeR.env must start with letter and contain only letters, digits or underscores"
	}
}

val generateLauncherIcon = tasks.register("generateLauncherIcon") {
	description = "Generate app's icon from config/logo.png"

	val logo = rootDir.resolve("../config/logo.png")
	val outFile = file("src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.png")

	inputs.file(logo)
	outputs.file(outFile)

	doLast {
		val canvasSize = 432 // adaptive-icon layer: 108dp canvas @ xxxhdpi (4x)
		val safeZone = 282 // mask-safe area: 66dp circle @ xxxhdpi (~4x) — content outside may get cropped

		val source = ImageIO.read(logo) ?: throw GradleException("Could not read $logo as an image")
		val scale = minOf(safeZone.toDouble() / source.width, safeZone.toDouble() / source.height)
		val scaledWidth = (source.width * scale).toInt()
		val scaledHeight = (source.height * scale).toInt()

		val canvas = BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB)
		val graphics = canvas.createGraphics()
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		graphics.drawImage(
			source,
			(canvasSize - scaledWidth) / 2,
			(canvasSize - scaledHeight) / 2,
			scaledWidth,
			scaledHeight,
			null,
		)
		graphics.dispose()

		outFile.parentFile.mkdirs()
		ImageIO.write(canvas, "png", outFile)
	}
}

tasks.named("preBuild") { dependsOn(generateLauncherIcon) }

android {
	namespace = "tr.qonferencer"
	compileSdk {
		version = release(37)
	}

	defaultConfig {
		applicationId = "tr.qonferencer.$eventId"
		minSdk = 29
		targetSdk = 37
		versionCode = 1
		versionName = "1.0"

		buildConfigField("String", "BACKEND_BASE_URL", "\"${envValue("BACKEND_BASE_URL")}\"")
		buildConfigField("String", "KEYCLOAK_BASE_URL", "\"${envValue("KEYCLOAK_BASE_URL")}\"")

		// realm/client id match deploy/keycloak/realm-export.json, not per-environment.
		buildConfigField("String", "KEYCLOAK_REALM", "\"qonferencer\"")
		buildConfigField("String", "KEYCLOAK_CLIENT_ID", "\"qonferencer-android\"")

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}
	buildFeatures {
		compose = true
		buildConfig = true
	}
}

kotlin {
	jvmToolchain(21)
}

dependencies {
	implementation(libs.shared)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.activity.compose)
	implementation(libs.kotlinx.coroutines.android)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.material.icons.core)
	implementation(libs.androidx.compose.material.icons.extended)
	implementation(libs.androidx.datastore.preferences)
	implementation(libs.androidx.security.crypto)
	implementation(platform(libs.okhttp.bom))
	implementation(libs.okhttp)
	implementation(libs.okhttp.logging.interceptor)
	implementation(libs.retrofit)
	implementation(libs.retrofit.converter.jackson)
	implementation(platform(libs.jackson.bom))
	implementation(libs.jackson.module.kotlin)
	implementation(libs.jackson.datatype.jsr310)
	implementation(libs.coil.compose)
	implementation(libs.coil.network.okhttp)
	implementation(libs.androidx.camera.core)
	implementation(libs.androidx.camera.camera2)
	implementation(libs.androidx.camera.lifecycle)
	implementation(libs.androidx.camera.view)
	implementation(libs.mlkit.barcode.scanning)
	implementation(libs.zxing.core)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	debugImplementation(libs.androidx.compose.ui.tooling)
	debugImplementation(libs.androidx.compose.ui.test.manifest)
	ktlintRuleset(libs.compose.rules.ktlint)
}

ktlint {
	version.set("1.8.0")
	android.set(true)
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
