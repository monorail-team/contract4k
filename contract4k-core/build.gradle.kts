plugins {
    kotlin("jvm")
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
    id("maven-publish")
}

group = "com.github.monorail-team"
version = rootProject.version.toString()

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.aspectj:aspectjrt:1.9.21")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}