pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("com.gradle.plugin-publish") version "1.2.1"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "contract4k"

include(
    "contract4k-core",
    "contract4k-ksp",
    "contract4k-gradle-plugin"
)
