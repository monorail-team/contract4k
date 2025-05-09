plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "io.github.monorail_team.contract4k"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":contract4k-core"))
    implementation("org.jetbrains.dokka:dokka-core:2.0.0")
    implementation("org.jetbrains.dokka:dokka-base:2.0.0")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.0.21")

    dokkaPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:2.0.0")
}

tasks.register<Jar>("dokkaPluginJar") {
    archiveClassifier.set("dokka-plugin")
    from(sourceSets.main.get().output)
}
