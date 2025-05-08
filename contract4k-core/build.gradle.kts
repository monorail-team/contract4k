plugins {
    kotlin("jvm")
}

group = "com.github.monorail-team"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(kotlin("stdlib"))
}