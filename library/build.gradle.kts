import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Copy
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "com.matmul"
version = "1.0.0"

kotlin {
    jvm()

    mingwX64()
    linuxX64()

    mingwX64 {
        binaries.all {
            
            
            val dllPath = rootProject.file("backend/build/libmatrix.dll").absolutePath
            linkerOpts(dllPath)
        }

        compilations["main"].cinterops.create("matrix") {
            defFile(project.file("src/nativeInterop/matrix.def"))
            includeDirs(project.file("${rootDir}/backend/include"))
        }
        compilations["test"].cinterops.create("matrix") {
            defFile(project.file("src/nativeInterop/matrix.def"))
            includeDirs(project.file("${rootDir}/backend/include"))
        }
    }

    linuxX64 {
        binaries.all {
            
            val libPath = rootProject.file("backend/build/libmatrix.so").absolutePath
            linkerOpts(libPath)
        }

        compilations["main"].cinterops.create("matrix") {
            defFile(project.file("src/nativeInterop/matrix.def"))
            includeDirs(project.file("${rootDir}/backend/include"))
        }
        compilations["test"].cinterops.create("matrix") {
            defFile(project.file("src/nativeInterop/matrix.def"))
            includeDirs(project.file("${rootDir}/backend/include"))
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

val backendDir = rootProject.file("backend")
val backendBuildDir = backendDir.resolve("build")
val nativeStagingDir = layout.buildDirectory.dir("nativeLibs") 
val nativeStagingFile get() = nativeStagingDir.get().asFile

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

val copyNativeForModule by tasks.registering(Copy::class) {
    group = "native"
    description = "Copy native runtime shared objects into module build/nativeLibs"
    dependsOn(cmakeBuild)
    from(backendBuildDir)
    include(osLibName("matrix_jni"), osLibName("matrixjni"), osLibName("matrix"))
    into(nativeStagingDir)
}

tasks.matching { it.name.startsWith("cinterop") || it.name.startsWith("link") }.configureEach {
    dependsOn(cmakeBuild)
}

tasks.withType(Test::class.java).configureEach {
    dependsOn(copyNativeForModule)
    
    doFirst {
        val libPath = nativeStagingFile.absolutePath
        jvmArgs("-Djava.library.path=$libPath")
    }
}
tasks.withType(JavaExec::class.java).configureEach {
    dependsOn(copyNativeForModule)
    doFirst {
        val libPath = nativeStagingFile.absolutePath
        jvmArgs("-Djava.library.path=$libPath")
    }
}


tasks.withType(KotlinNativeTest::class.java).configureEach {
    dependsOn(copyNativeForModule)
    doFirst {
        val libPath = nativeStagingFile.absolutePath
        environment("LD_LIBRARY_PATH", libPath)
        environment("DYLD_LIBRARY_PATH", libPath)
        
        val currentPath = System.getenv("PATH") ?: ""
        environment("PATH", libPath + File.pathSeparator + currentPath)
    }
}


tasks.matching { it.name.startsWith("link") && it.name.contains("Test") }.configureEach {
    dependsOn(copyNativeForModule)
    doLast {
        val stagedDir = nativeStagingDir.get().asFile
        if (!stagedDir.exists()) {
            logger.lifecycle("No staged native libs found at: ${stagedDir.absolutePath}")
            return@doLast
        }

        val binRoot = layout.buildDirectory.dir("bin").get().asFile
        if (!binRoot.exists()) {
            logger.lifecycle("No binaries directory found at: ${binRoot.absolutePath}")
            return@doLast
        }

        
        val stagedFiles = stagedDir.listFiles { f ->
            f.isFile && (f.name.startsWith("libmatrix") ||
                         f.name.startsWith("matrix") ||
                         f.name.startsWith("libmatrixjni") || f.name.startsWith("matrixjni"))
        }?.toList() ?: emptyList()

        if (stagedFiles.isEmpty()) {
            logger.lifecycle("No staged native files matched in ${stagedDir.absolutePath}")
            return@doLast
        }

        
        binRoot.walkTopDown().filter { it.isFile && it.name.endsWith(".kexe") }.forEach { kexeFile ->
            val targetDir = kexeFile.parentFile
            stagedFiles.forEach { file ->
                copy {
                    from(file)
                    into(targetDir)
                    include(file.name)
                }
                logger.lifecycle("Copied ${file.name} -> ${targetDir.absolutePath}")
            }
        }
    }
}

tasks.named("compileKotlinJvm") { dependsOn(copyNativeForModule) }
tasks.named("jvmTest") { dependsOn(copyNativeForModule) }
