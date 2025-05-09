plugins {
    kotlin("jvm") version "2.0.21"
}

group = "io.github.monorail_team.contract4k"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}