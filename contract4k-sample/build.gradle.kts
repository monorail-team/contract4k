plugins {
    kotlin("jvm")
}

group = "io.github.monorail_team.contract4k"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":contract4k-core"))
}