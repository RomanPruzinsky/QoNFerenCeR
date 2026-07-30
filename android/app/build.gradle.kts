plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ktlint)
}

android {
	namespace = "tr.qonferencer"
	compileSdk {
		version = release(37)
	}

	defaultConfig {
		applicationId = "tr.qonferencer"
		minSdk = 29
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

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
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation("tr.qonferencer:shared")
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
	implementation(libs.androidx.navigation.compose)
	implementation(libs.androidx.datastore.preferences)

	implementation(platform(libs.koin.bom))
	implementation(libs.koin.android)
	implementation(libs.koin.androidx.compose)

	implementation(platform(libs.okhttp.bom))
	implementation(libs.okhttp)
	implementation(libs.okhttp.logging.interceptor)
	implementation(libs.retrofit)
	implementation(libs.retrofit.converter.jackson)
	implementation(platform(libs.jackson.bom))
	implementation(libs.jackson.module.kotlin)

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
	androidTestImplementation(platform(libs.androidx.compose.bom))
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
