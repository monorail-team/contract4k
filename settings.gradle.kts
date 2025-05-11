pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
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
        mavenCentral()
    }
}

rootProject.name = "contract4k"

include(
    "contract4k-core",
    "contract4k-doc",
    "contract4k-gradle-plugin",
    "contract4k-sample"
)
