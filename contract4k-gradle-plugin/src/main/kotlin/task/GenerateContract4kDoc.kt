package task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.OutputStreamWriter
import java.net.URLClassLoader
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty

abstract class GenerateContract4kDoc : DefaultTask() {

    @get:InputDirectory
    abstract val scanPath: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val scanDir = scanPath.get().asFile
        val output = outputFile.get().asFile

        val classLoader = URLClassLoader(
            arrayOf(scanDir.toURI().toURL()),
            javaClass.classLoader
        )

        val docClasses = classLoader.loadContract4kDocs(scanDir)

        OutputStreamWriter(output.outputStream(), Charsets.UTF_8).use { writer ->
            writer.write("""
                <html>
                  <head>
                    <meta charset="UTF-8">
                    <title>Contract4k Documentation</title>
                  </head>
                  <body>
                    <h1>📄 Contract4k Documentation</h1>
            """.trimIndent())

            for ((name, doc) in docClasses) {
                writer.write("<hr><h2>🔧 $name</h2>\n")
                writer.write("<p><strong>Description:</strong> ${doc.description}</p>\n")

                if (doc.params.isNotEmpty()) {
                    writer.write("<p><strong>Parameters:</strong></p><ul>\n")
                    doc.params.forEach { (param, desc) ->
                        writer.write("<li><code>$param</code>: $desc</li>\n")
                    }
                    writer.write("</ul>\n")
                }

                if (doc.returns.isNotBlank()) {
                    writer.write("<p><strong>Returns:</strong> ${doc.returns}</p>\n")
                }

                if (doc.throws.isNotEmpty()) {
                    writer.write("<p><strong>Throws:</strong></p><ul>\n")
                    doc.throws.forEach { (ex, desc) ->
                        writer.write("<li><code>$ex</code>: $desc</li>\n")
                    }
                    writer.write("</ul>\n")
                }

                if (doc.author.isNotBlank()) {
                    writer.write("<p><strong>Author:</strong> ${doc.author}</p>\n")
                }

                if (doc.since.isNotBlank()) {
                    writer.write("<p><strong>Since:</strong> ${doc.since}</p>\n")
                }
            }

            writer.write("</body></html>")
        }

        println("✅ Contract4k documentation generated at: ${output.absolutePath}")
    }

    private fun ClassLoader.loadContract4kDocs(scanDir: File): Map<String, base.Documentation> {
        return this.loadClassesImplementing(scanDir, base.Contract4kDocSpec::class.java)
            .filter { it.kotlin.objectInstance != null }
            .associate { clazz ->
                val obj = clazz.kotlin.objectInstance as base.Contract4kDocSpec
                clazz.simpleName to obj.doc
            }
    }

    private fun ClassLoader.loadClassesImplementing(scanDir: File, type: Class<*>): List<Class<*>> {
        return scanDir.walkTopDown()
            .filter { it.isFile && it.extension == "class" }
            .mapNotNull { file ->
                val relativePath = file.relativeTo(scanDir).path
                    .removeSuffix(".class")
                    .replace(File.separatorChar, '.')
                try {
                    val clazz = loadClass(relativePath)
                    if (type.isAssignableFrom(clazz)) clazz else null
                } catch (_: Throwable) {
                    null
                }
            }.toList()
    }
}