import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ktlint)
	alias(libs.plugins.oss.licenses)
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

val generateLogoAssets = tasks.register("generateLogoAssets") {
	description = "Generate app's launcher icon + in-app logo drawable from config/logo.png"

	val logo = rootDir.resolve("../config/logo.png")
	val iconOutFile = file("src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.png")
	val drawableOutFile = file("src/main/res/drawable-nodpi/logo.png")

	inputs.file(logo)
	outputs.file(iconOutFile)
	outputs.file(drawableOutFile)

	doLast {
		fun scaledCopy(source: BufferedImage, maxDimension: Int): BufferedImage {
			val scale = minOf(1.0, maxDimension.toDouble() / maxOf(source.width, source.height))
			val width = (source.width * scale).toInt()
			val height = (source.height * scale).toInt()
			val copy = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			val graphics = copy.createGraphics()
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
			graphics.drawImage(source, 0, 0, width, height, null)
			graphics.dispose()
			return copy
		}

		val source = ImageIO.read(logo) ?: throw GradleException("Could not read $logo as an image")

		val logoDrawableMaxDimension = 512
		val canvasSize = 432 // adaptive-icon layer: 108dp canvas @ xxxhdpi (4x)
		val safeZone = 282 // mask-safe area: 66dp circle @ xxxhdpi (~4x) — content outside may get cropped
		
		val scaledLogo = scaledCopy(source, safeZone)
		val canvas = BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB)
		val canvasGraphics = canvas.createGraphics()
		canvasGraphics.drawImage(
			scaledLogo,
			(canvasSize - scaledLogo.width) / 2,
			(canvasSize - scaledLogo.height) / 2,
			null,
		)
		canvasGraphics.dispose()
		iconOutFile.parentFile.mkdirs()
		ImageIO.write(canvas, "png", iconOutFile)

		drawableOutFile.parentFile.mkdirs()
		ImageIO.write(scaledCopy(source, logoDrawableMaxDimension), "png", drawableOutFile)
	}
}

tasks.named("preBuild") { dependsOn(generateLogoAssets) }

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
	implementation(libs.qrcode.kotlin)
	implementation(libs.play.services.oss.licenses)

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
