package doc

import ContractMessageExtractor
import org.jetbrains.dokka.model.*
import org.jetbrains.dokka.model.doc.CustomTagWrapper
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.transformers.documentation.DocumentableTransformer
import java.nio.file.Paths

class Contract4kCommentTransformer(
    private val context: DokkaContext
) : DocumentableTransformer {

    private val extractedMessages: Map<String, List<String>> by lazy {
        val rootDir = Paths.get("../contract4k-core/src/main/kotlin").toFile().canonicalFile
        ContractMessageExtractor().extractMessages(rootDir)
    }

    override fun invoke(original: DModule, context: DokkaContext): DModule {
        return original.copy(
            packages = original.packages.map { pkg ->
                pkg.copy(
                    classlikes = pkg.classlikes.map { classLike ->
                        transformClassLike(pkg.dri.packageName ?: "", classLike)
                    }
                )
            }
        )
    }

    private fun transformClassLike(packageName: String, classLike: DClasslike): DClasslike {
        return when (classLike) {
            is DClass -> classLike.copy(functions = classLike.functions.map { transformFunction(it) })
            is DObject -> classLike.copy(functions = classLike.functions.map { transformFunction(it) })
            is DInterface -> classLike.copy(functions = classLike.functions.map { transformFunction(it) })
            is DAnnotation -> classLike.copy(functions = classLike.functions.map { transformFunction(it) })
            else -> classLike
        }
    }

    private fun transformFunction(function: DFunction): DFunction {
        val annotationsBySourceSet = function.extra[Annotations]?.directAnnotations ?: emptyMap()
        val annotations = annotationsBySourceSet.values.flatten()
        val contractAnnotation = annotations.firstOrNull {
            it.dri.classNames == "Contract4kWith"
        } ?: return function

        val classNameRaw = contractAnnotation.params["value"]
            ?.toString()
            ?.removePrefix("class ")
            ?.removePrefix("kotlin.")
            ?.trim()

        val containingClassFqName = function.dri.packageName?.let { pkg ->
            if (classNameRaw?.contains(".") == true) classNameRaw else "$pkg.$classNameRaw"
        } ?: return function

        val messages = extractedMessages[containingClassFqName] ?: return function

        val newDocs = function.documentation.mapValues { (_, doc) ->
            val extraText = buildString {
                append("\n\n---\n")
                append("🛡️ **Contract 검증 메시지:**\n")
                messages.forEach { msg -> append("- $msg\n") }
            }
            val extra = CustomTagWrapper(Text(extraText), "contract")
            doc.copy(children = doc.children + extra)
        }

        return function.copy(documentation = newDocs)
    }
}