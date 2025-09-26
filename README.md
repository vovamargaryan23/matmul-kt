# matmul-kt

Small, self-contained dense matrix multiplication library:
- C++ native backend (`backend/`)
- Kotlin Multiplatform wrapper (`library/`)
    - JVM (via JNI)
    - Kotlin/Native (Linux `linuxX64`, Windows `mingwX64`)
- Tests for JVM and Native
- Gradle automation builds native code (CMake) and stages shared libs for both JVM and K/N.

---

## Prerequisites

### Linux
- `git`
- `cmake` (≥ 3.10)
- `gcc` / `g++` and `make`
- Java JDK (11+)
- `./gradlew` (included wrapper, no need to install Gradle)

### Windows
- Git for Windows
- MinGW-w64 (or MSYS2) toolchain in `PATH` for building DLL
- `cmake`
- Java JDK (11+)


---

## Build & Run

### Linux

```bash
# Build everything (C++ + KMP + tests)
./gradlew build

# Run Kotlin/Native linux tests
./gradlew :library:linuxX64Test

# Run JVM tests
./gradlew :library:jvmTest
```

### Windows

```powershell
# Build everything
.\gradlew build

# Run Kotlin/Native windows tests
.\gradlew :library:mingwX64Test

# Run JVM tests
.\gradlew :library:jvmTest
```