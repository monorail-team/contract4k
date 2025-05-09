plugins {
    kotlin("jvm")
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
}

group = "io.github.monorail_team.contract4k"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.aspectj:aspectjrt:1.9.21")
}