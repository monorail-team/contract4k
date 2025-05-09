package doc

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.DokkaPluginApiPreview
import org.jetbrains.dokka.plugability.PluginApiPreviewAcknowledgement


class Contract4kDokkaPlugin : DokkaPlugin() {
    val contractTransformer by extending {
        CoreExtensions.documentableTransformer providing ::Contract4kCommentTransformer
    }

    @OptIn(DokkaPluginApiPreview::class)
    override fun pluginApiPreviewAcknowledgement(): PluginApiPreviewAcknowledgement {
        return PluginApiPreviewAcknowledgement
    }
}