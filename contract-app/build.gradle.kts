plugins {
    kotlin("jvm") version "2.0.21"

    // compile time weaving
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
}

group = "com.github.monorail-team"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dirs("../contract-lib/build/libs")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.monorail-team:contract-lib:1.0-SNAPSHOT")
    implementation("org.aspectj:aspectjrt:1.9.21")
    aspect("com.github.monorail-team:contract-lib:1.0-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}