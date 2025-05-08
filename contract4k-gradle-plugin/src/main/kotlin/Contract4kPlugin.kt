import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Copy
import java.net.URI

class Contract4kPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1. repositories 설정
        project.repositories.apply {
            mavenCentral()
            maven { repo ->
                repo.setUrl(URI("https://jitpack.io"))
            }
        }

        // 2. Kotlin JVM plugin 적용 시 toolchain 설정
        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            val kotlinExt = project.extensions.findByName("kotlin") as? Any
            try {
                val extClass = Class.forName("org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension")
                val jvmToolchainMethod = extClass.getMethod("jvmToolchain", Int::class.java)
                jvmToolchainMethod.invoke(kotlinExt, 21)
            } catch (e: Exception) {
                project.logger.warn("Cannot configure jvmToolchain. Kotlin DSL might be missing.")
            }
        }

        // 3. KSP 적용
        project.pluginManager.apply("com.google.devtools.ksp")

        // 4. dependencies 추가
        project.dependencies.apply {
            add("implementation", "com.github.monorail-team:contract4k:0.0.1")
            add("ksp", "com.github.monorail-team:contract4k:0.0.1")
            add("implementation", "org.aspectj:aspectjrt:1.9.21")
            add("implementation", "org.aspectj:aspectjweaver:1.9.21")
        }

        // 5. AspectJ 위빙 설정
        val aspectjTools = project.configurations.detachedConfiguration(
            project.dependencies.create("org.aspectj:aspectjtools:1.9.21")
        )
        val aspectjRuntime = project.configurations.detachedConfiguration(
            project.dependencies.create("org.aspectj:aspectjrt:1.9.21")
        )

        val weaveAspectj = project.tasks.register("weaveAspectj") { task ->
            task.dependsOn("compileKotlin")
            task.doLast {
                project.javaexec { exec ->
                    exec.mainClass.set("org.aspectj.tools.ajc.Main")
                    exec.classpath = project.files(aspectjTools.singleFile)
                    exec.args = listOf(
                        "-inpath", "${project.buildDir}/classes/kotlin/main",
                        "-aspectpath", "${project.buildDir}/classes/kotlin/main",
                        "-d", "${project.buildDir}/classes/kotlin/main",
                        "-classpath", (
                                project.configurations.getByName("compileClasspath") +
                                        project.configurations.getByName("runtimeClasspath") +
                                        project.files(aspectjRuntime.singleFile)
                                ).asPath
                    )
                }
            }
        }

        // 6. classes 태스크가 위빙 이후 실행되도록
        project.tasks.named("classes").configure {
            it.dependsOn(weaveAspectj)
        }

        // 7. Copy 태스크 중복 방지 설정
        project.tasks.withType(Copy::class.java).configureEach {
            it.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}