import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
//    alias(libs.plugins.androidLibrary)
//    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "com.matmul"
version = "1.0.0"

kotlin {
    jvm()
//    androidTarget {
//        publishLibraryVariants("release")
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }
//    }
//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()
//    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

//android {
//    namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
//    compileSdk = libs.versions.android.compileSdk.get().toInt()
//    defaultConfig {
//        minSdk = libs.versions.android.minSdk.get().toInt()
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//}

val backendDir = rootProject.file("backend")
val backendBuildDir = backendDir.resolve("build")
val backendOutputDir = "${layout.buildDirectory.get()}/nativeLibs"

fun osLibName(basename: String) = when {
    org.gradle.internal.os.OperatingSystem.current().isWindows -> "$basename.dll"
    org.gradle.internal.os.OperatingSystem.current().isMacOsX -> "lib${basename}.dylib"
    else -> "lib${basename}.so"
}

val cmakeConfigure by tasks.registering(Exec::class) {
    group = "native"
    description = "Configure native CMake"
    workingDir = backendDir
    commandLine("cmake", "-S", ".", "-B", "build", "-DCMAKE_BUILD_TYPE=Release")
}

val cmakeBuild by tasks.registering(Exec::class) {
    group = "native"
    description = "Build native libs via CMake"
    dependsOn(cmakeConfigure)
    workingDir = backendDir
    commandLine("cmake", "--build", "build", "--config", "Release")
}

val copyNativeForJvm by tasks.registering(Copy::class) {
    group = "native"
    description = "Copy native runtime shared object into jvm output"
    dependsOn(cmakeBuild)
    from(backendBuildDir)
    include(osLibName("matrix_jni"), osLibName("matrixjni"), osLibName("matrix"))
    into(backendOutputDir)
}

tasks.named("compileKotlinJvm") {dependsOn(copyNativeForJvm)}
tasks.named("jvmTest") {dependsOn(copyNativeForJvm)}
