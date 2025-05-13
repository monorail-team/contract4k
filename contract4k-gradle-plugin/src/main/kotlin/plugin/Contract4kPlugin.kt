package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import task.GenerateContract4kDoc

class Contract4kPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project.dependencies) {
            add("implementation", "org.aspectj:aspectjrt:1.9.21")
            add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
        }

        project.tasks.register("generateContract4kDoc", GenerateContract4kDoc::class.java).configure {
            group = "documentation"
            description = "Generate HTML documentation from Contract4kDocSpec objects"
            scanPath.set(project.layout.buildDirectory.dir("classes/kotlin/main"))
            outputFile.set(project.layout.buildDirectory.file("generated/contract4k-docs/Contract4kDocReport.html"))
        }

        project.tasks.named("generateContract4kDoc").configure {
            dependsOn("classes")
        }
    }
}