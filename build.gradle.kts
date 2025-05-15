plugins {
    kotlin("jvm") version "2.0.21"
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
}

group = "io.github.monorail_team.contract4k"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.aspectj:aspectjrt:1.9.21")
    implementation("org.slf4j:slf4j-api:2.0.16")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.18")
}