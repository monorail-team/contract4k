import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.*
import java.io.File

class ContractMessageExtractor {

    fun extractMessages(sourceRoot: File): Map<String, List<String>> {
        val config = CompilerConfiguration().apply {
            put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, org.jetbrains.kotlin.cli.common.messages.MessageCollector.NONE)
            addKotlinSourceRoot(sourceRoot.absolutePath)
            addJvmClasspathRoots(System.getProperty("java.class.path")
                .split(File.pathSeparator)
                .map(::File)
                .filter { it.exists() })
        }

        val environment = KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            config,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )

        val messagesByClass = mutableMapOf<String, List<String>>()

        environment.getSourceFiles().forEach { file ->
            file.accept(object : KtTreeVisitorVoid() {
                override fun visitClass(klass: KtClass) {
                    val fqn = klass.fqName?.asString() ?: return
                    val messages = mutableListOf<String>()

                    klass.declarations
                        .filterIsInstance<KtNamedFunction>()
                        .filter { it.name == "validatePre" || it.name == "validatePost" }
                        .forEach { function ->
                            function.accept(object : KtTreeVisitorVoid() {
                                override fun visitCallExpression(expr: KtCallExpression) {
                                    val callee = expr.calleeExpression?.text ?: return
                                    val parent = expr.parent

                                    if (parent is KtDotQualifiedExpression) {
                                        val receiver = parent.receiverExpression
                                        if (receiver is KtStringTemplateExpression) {
                                            val message = receiver.entries.joinToString("") { it.text }
                                            if (callee == "means" || callee == "quickFix") {
                                                messages.add(message)
                                            }
                                        }
                                    }
                                    super.visitCallExpression(expr)
                                }
                            })
                        }

                    if (messages.isNotEmpty()) {
                        messagesByClass[fqn] = messages
                    }
                }
            })
        }

        return messagesByClass
    }
}