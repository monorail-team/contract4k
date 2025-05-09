import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class Contract4kPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.repositories.apply {
            mavenCentral()
            maven { it.url = URI("https://jitpack.io") }
        }

        project.pluginManager.withPlugin("org.jetbrains.dokka") {
            project.logger.lifecycle("[contract4k] Dokka plugin detected, applying contract4k-doc support.")
            project.dependencies.add(
                "dokkaPlugin",
                "com.github.monorail-team:contract4k-doc:0.0.1"
            )
        }

        project.dependencies.apply {
            add("implementation", "com.github.monorail-team:contract4k-core:0.0.1")
            add("implementation", "org.aspectj:aspectjrt:1.9.21")
            add("implementation", "org.aspectj:aspectjweaver:1.9.21")
        }

        val aspectjTools = project.configurations.detachedConfiguration(
            project.dependencies.create("org.aspectj:aspectjtools:1.9.21")
        )

        val weaveAspectj = project.tasks.register("weaveAspectj") {
            it.dependsOn("compileKotlin")
            it.doLast {
                val outputDir = project.layout.buildDirectory.dir("classes/kotlin/main").get().asFile.absolutePath
                project.javaexec { exec ->
                    exec.mainClass.set("org.aspectj.tools.ajc.Main")
                    exec.classpath = project.files(aspectjTools.singleFile)
                    exec.args = listOf(
                        "-inpath", outputDir,
                        "-aspectpath", outputDir,
                        "-d", outputDir,
                        "-classpath", (
                            project.configurations.getByName("compileClasspath") +
                            project.configurations.getByName("runtimeClasspath")
                        ).asPath
                    )
                }
            }
        }

        project.tasks.named("classes") {
            it.dependsOn(weaveAspectj)
        }
    }
}