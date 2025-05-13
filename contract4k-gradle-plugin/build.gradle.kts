plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "io.github.monorail_team.contract4k"
version = "0.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(localGroovy())
}

kotlin {
    jvmToolchain(21)
}

gradlePlugin {
    plugins {
        create("contract4k") {
            id = "io.github.monorail_team.contract4k"
            implementationClass = "plugin.Contract4kPlugin"
            displayName = "Contract4k Gradle Plugin"
            description = "A Kotlin DSL plugin for Design by Contract validation with AspectJ support."
            tags.set(listOf("kotlin", "dsl", "contract", "validation", "aspectj"))
            website.set("https://github.com/monorail-team/contract4k")
            vcsUrl.set("https://github.com/monorail-team/contract4k")
        }
    }
}