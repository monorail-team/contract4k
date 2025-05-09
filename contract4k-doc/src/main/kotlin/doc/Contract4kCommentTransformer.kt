package doc

import org.jetbrains.dokka.model.*
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.model.doc.CustomTagWrapper
import org.jetbrains.dokka.model.Annotations
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.transformers.documentation.DocumentableTransformer
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.File

class Contract4kCommentTransformer(private val context: DokkaContext) : DocumentableTransformer {

    override fun invoke(original: DModule, context: DokkaContext): DModule {
        return original.copy(
            packages = original.packages.map { pkg ->
                pkg.copy(
                    classlikes = pkg.classlikes.map { classLike ->
                        transformClassLike(classLike)
                    }
                )
            }
        )
    }

    private fun transformClassLike(classLike: DClasslike): DClasslike {
        return when (classLike) {
            is DClass -> classLike.copy(
                functions = classLike.functions.map { transformFunction(it) }
            )

            is DObject -> classLike.copy(
                functions = classLike.functions.map { transformFunction(it) }
            )

            is DInterface -> classLike.copy(
                functions = classLike.functions.map { transformFunction(it) }
            )

            is DAnnotation -> classLike.copy(
                functions = classLike.functions.map { transformFunction(it) }
            )

            else -> classLike
        }
    }

    private fun transformFunction(function: DFunction): DFunction {
        val annotationsBySourceSet = function.extra[Annotations]?.directAnnotations ?: emptyMap()

        val annotations = annotationsBySourceSet.values.flatten()

        val contractAnnotation = annotations.firstOrNull {
            it.dri.classNames == "Contract4kWith"
        }

        if (contractAnnotation == null) return function

        val className = contractAnnotation.params["value"]
            ?.toString()
            ?.removePrefix("class ")
            ?.removePrefix("kotlin.")

        val messages = extractMessagesFromContract(className)

        val newDocs = function.documentation.mapValues { (sourceSet, doc) ->
            val extra = buildString {
                append("\n\n---\n")
                append("🛡️ **Contract 검증 메시지:**\n")
                messages.forEach { msg -> append("- $msg\n") }
            }
            val extraContent = CustomTagWrapper(Text(extra), "contract")
            doc.copy(children = doc.children + extraContent)
        }

        return function.copy(documentation = newDocs)
    }

    private fun extractMessagesFromContract(contractClassName: String?): List<String> {
        if (contractClassName == null) return emptyList()

        val messages = mutableListOf<String>()

        val environment = createKotlinCoreEnvironment()
        val ktFiles = environment.getSourceFiles()

        for (file in ktFiles) {
            file.declarations
                .asSequence()
                .filterIsInstance<org.jetbrains.kotlin.psi.KtClass>()
                .filter { it.fqName?.asString() != null && it.fqName!!.asString() == contractClassName }
                .flatMap { it.declarations }
                .filterIsInstance<org.jetbrains.kotlin.psi.KtNamedFunction>()
                .filter { it.name == "validatePre" || it.name == "validatePost" }
                .toList()
                .forEach { function ->
                    function.bodyExpression?.accept(object : org.jetbrains.kotlin.psi.KtTreeVisitorVoid() {
                        override fun visitCallExpression(expression: org.jetbrains.kotlin.psi.KtCallExpression) {
                            val callee = expression.calleeExpression?.text
                            if (callee == "message") {
                                val arg = expression.valueArguments.firstOrNull()?.getArgumentExpression()?.text
                                if (arg != null && arg.startsWith("\"")) {
                                    messages.add(arg.trim('"'))
                                }
                            }
                            super.visitCallExpression(expression)
                        }
                    })
                }
        }

        return messages
    }

    private fun createKotlinCoreEnvironment(): KotlinCoreEnvironment {
        val configuration = CompilerConfiguration().apply {
            put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
            addJvmClasspathRoots(getCurrentClasspath())
            context.configuration.sourceSets.forEach { sourceSet ->
                sourceSet.sourceRoots.forEach { file ->
                    addKotlinSourceRoot(file.absolutePath)
                }
            }
        }

        return KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
    }

    private fun getCurrentClasspath(): List<File> {
        return System.getProperty("java.class.path")
            .split(File.pathSeparator)
            .map(::File)
    }
}