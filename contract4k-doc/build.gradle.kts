plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "2.0.0"
    id("maven-publish")
}

group = "com.github.monorail-team"
version = rootProject.version.toString()

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":contract4k-core"))
    implementation(project(":contract4k-message-extractor"))
    compileOnly("org.jetbrains.dokka:dokka-core:2.0.0")
    compileOnly("org.jetbrains.dokka:dokka-base:2.0.0")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.0.21")

    dokkaPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:2.0.0")
}

tasks.register<Jar>("dokkaPluginJar") {
    archiveClassifier.set("dokka-plugin")
    from(sourceSets.main.get().output)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}