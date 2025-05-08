plugins {
    kotlin("jvm") version "2.0.21"
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
}

group = "com.github.monorail-team"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // aspectj
    implementation("org.aspectj:aspectjrt:1.9.21")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}