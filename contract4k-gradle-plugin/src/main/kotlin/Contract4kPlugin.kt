import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class Contract4kPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val version = this::class.java.`package`.implementationVersion
            ?: project.findProperty("contract4k.version")?.toString()
            ?: "0.0.3"

        project.repositories.apply {
            mavenCentral()
//            maven { it.url = URI("https://jitpack.io") }
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            project.dependencies.apply {
                add("implementation", "com.github.monorail-team:contract4k-core:$version")
                add("implementation", "org.aspectj:aspectjrt:1.9.21")
                add("implementation", "org.aspectj:aspectjweaver:1.9.21")
            }
        }

        project.plugins.withId("org.jetbrains.dokka") {
            project.logger.lifecycle("[contract4k] Dokka plugin detected, injecting contract4k-doc support.")
//            project.dependencies.add("dokkaPlugin", "com.github.monorail-team:contract4k-doc:$version")
        }
    }
}