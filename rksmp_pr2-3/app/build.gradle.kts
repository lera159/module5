plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("org.jetbrains.kotlin.plugin.compose")
}

android {
	namespace = "com.example.rksmp_pr2_3"
	compileSdk = 36

	defaultConfig {
		applicationId = "com.example.rksmp_pr2_3"
		minSdk = 24
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

dependencies {

	implementation("androidx.core:core-ktx:1.18.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
	implementation("androidx.activity:activity-compose:1.13.0")
	implementation(platform("androidx.compose:compose-bom:2026.04.01"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	implementation("io.coil-kt:coil-compose:2.7.0")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0-beta01")
	implementation("androidx.camera:camera-camera2:1.7.0-alpha01")
	implementation("androidx.camera:camera-view:1.7.0-alpha01")
	implementation("androidx.camera:camera-lifecycle:1.7.0-alpha01")
	implementation("com.google.accompanist:accompanist-permissions:0.37.3")
	implementation("androidx.compose.material:material-icons-extended:1.7.8")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0-beta01")
	implementation("androidx.navigation:navigation-compose:2.10.0-alpha03")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.3.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
	androidTestImplementation(platform("androidx.compose:compose-bom:2026.04.01"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
}