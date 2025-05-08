plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

group = "com.github.monorail-team"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":contract4k-core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.21-1.0.27")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets["main"].resources.srcDir("src/main/resources")