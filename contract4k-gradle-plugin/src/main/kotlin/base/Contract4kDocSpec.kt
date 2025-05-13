package base

abstract class Contract4kDocSpec {
    abstract val doc: Documentation
}

fun documentation(init: DocumentationBuilder.() -> Unit): Documentation =
    DocumentationBuilder().apply(init).build()

data class Documentation(
    val description: String,
    val params: List<Pair<String, String>>,
    val throws: List<Pair<String, String>>,
    val returns: String,
    val author: String,
    val since: String
)

class DocumentationBuilder {
    private var _description: String = ""
    private val _params = mutableListOf<Pair<String, String>>()
    private val _throws = mutableListOf<Pair<String, String>>()
    private var _returns: String = ""
    private var _author: String = ""
    private var _since: String = ""

    fun description(value: String) { _description = value }
    fun returns(value: String) { _returns = value }
    fun author(value: String) { _author = value }
    fun since(value: String) { _since = value }

    fun param(name: String, desc: String) = _params.add(name to desc)
    fun throws(name: String, desc: String) = _throws.add(name to desc)

    fun build(): Documentation = Documentation(
        description = _description,
        params = _params,
        throws = _throws,
        returns = _returns,
        author = _author,
        since = _since
    )
}