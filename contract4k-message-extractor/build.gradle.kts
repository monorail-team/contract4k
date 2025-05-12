plugins {
    kotlin("jvm")
    id("maven-publish")
    application
}

group = "com.github.monorail-team"
version = rootProject.version

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:2.0.21")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")

    implementation(project(":contract4k-core"))
}

application {
    mainClass.set("MainKt")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
