import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.OutputStreamWriter

class Contract4kProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val map = resolver.getSymbolsWithAnnotation("contract4k.annotation.Contract4kWith")
            .filterIsInstance<KSFunctionDeclaration>()
            .groupBy { it.parentDeclaration as KSClassDeclaration }

        for ((classDeclaration, functionDeclarations) in map) {
            generateAspectClass(classDeclaration, functionDeclarations)
        }

        return emptyList()
    }

    private fun generateAspectClass(classDecl: KSClassDeclaration, functions: List<KSFunctionDeclaration>) {
        val className = classDecl.simpleName.asString()
        val fqClassName = classDecl.qualifiedName?.asString() ?: return
        val file = codeGenerator.createNewFile(
            Dependencies(false),
            "contract4k.generated",
            "Contract4k${className}Aspect"
        )

        val imports = collectImports(functions)
        val writer = OutputStreamWriter(file, Charsets.UTF_8)

        writer.write("package contract4k.generated\n\n")
        imports.sorted().forEach { writer.write("import $it\n") }

        writer.write("\n@Aspect\nclass Contract4k${className}Aspect {\n")
        functions.forEach { function ->
            writer.write(generateAspectMethod(fqClassName, function))
        }
        writer.write("}\n")
        writer.close()
    }

    private fun collectImports(functions: List<KSFunctionDeclaration>): Set<String> {
        val base = mutableSetOf(
            "org.aspectj.lang.ProceedingJoinPoint",
            "org.aspectj.lang.annotation.Around",
            "org.aspectj.lang.annotation.Aspect",
            "contract4k.contract.Contract4kDsl",
            "contract4k.contract.Fourth",
            "contract4k.contract.Fifth",
            "contract4k.contract.and"
        )

        val contracts = functions.mapNotNull {
            it.annotations.firstOrNull {
                it.shortName.getShortName() == "Contract4kWith"
            }?.arguments?.firstOrNull {
                it.name?.asString() == "value"
            }?.value as? KSType
        }.mapNotNull {
            it.declaration.qualifiedName?.asString()
        }

        val parameterImports = functions.flatMap { function ->
            function.parameters.mapNotNull {
                it.type.resolve().declaration.qualifiedName?.asString()
            }
        }

        val returnTypeImports = functions.mapNotNull {
            it.returnType?.resolve()?.declaration?.qualifiedName?.asString()
        }

        return base + contracts + parameterImports + returnTypeImports
    }

    private fun generateAspectMethod(
        fqClassName: String,
        function: KSFunctionDeclaration
    ): String {
        val methodName = function.simpleName.asString()
        val contractClass = function.annotations.first {
            it.shortName.getShortName() == "Contract4kWith"
        }.arguments.first {
            it.name?.asString() == "value"
        }.value as KSType

        val contractName = contractClass.declaration.simpleName.asString()
        val returnType = function.returnType?.resolve()?.declaration?.qualifiedName?.asString() ?: "Any"

        val paramTypes = function.parameters.mapNotNull {
            it.type.resolve().declaration.qualifiedName?.asString()
        }

        val inputType = resolveInputType(paramTypes)
        val inputExpr = resolveInputExpr(paramTypes)

        return  """
            
                @Around("execution(* $fqClassName.$methodName(..))")
                fun validate${methodName.replaceFirstChar { it.uppercase() }}(joinPoint: ProceedingJoinPoint): Any {
                    val args = joinPoint.args
                    val contract: Contract4kDsl<$inputType, $returnType> = $contractName
                    val input = $inputExpr
                    
                    println("사전 조건 검증 시작")
                    contract.validatePre(input)
                    println("사전 조건 검증 종료")
                    val result = joinPoint.proceed() as $returnType
                    println("사후 조건 검증 시작")
                    contract.validatePost(input, result)
                    println("사후 조건 검증 종료")
                    
                    return result
                }
                
            """.trimIndent()
    }

    private fun resolveInputType(params: List<String>): String = when (params.size) {
        1 -> params[0]
        2 -> "kotlin.Pair<${params[0]}, ${params[1]}>"
        3 -> "kotlin.Triple<${params[0]}, ${params[1]}, ${params[2]}>"
        4 -> "contract4k.contract.Fourth<${params[0]}, ${params[1]}, ${params[2]}, ${params[3]}>"
        5 -> "contract4k.contract.Fifth<${params[0]}, ${params[1]}, ${params[2]}, ${params[3]}, ${params[4]}>"
        else -> "Any"
    }

    private fun resolveInputExpr(params: List<String>): String = when (params.size) {
        1 -> "args[0] as ${params[0]}"
        2 -> "args[0] as ${params[0]} to args[1] as ${params[1]}"
        3 -> "args[0] as ${params[0]} to args[1] as ${params[1]} and args[2] as ${params[2]}"
        4 -> "args[0] as ${params[0]} to args[1] as ${params[1]} and args[2] as ${params[2]} and args[3] as ${params[3]}"
        5 -> "args[0] as ${params[0]} to args[1] as ${params[1]} and args[2] as ${params[2]} and args[3] as ${params[3]} and args[4] as ${params[4]}"
        else -> "error(\"Too many arguments\")"
    }
}
