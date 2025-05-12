plugins {
    kotlin("jvm") version "2.0.21"
}

group = "io.github.monorail-team.contract4k"
version = rootProject.version.toString()

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

tasks.register("publishAllToLocal") {
    dependsOn(
        ":contract4k-core:publishToMavenLocal",
        ":contract4k-doc:publishToMavenLocal",
        ":contract4k-gradle-plugin:publishToMavenLocal",
        ":contract4k-message-extractor:publishToMavenLocal"
    )
}