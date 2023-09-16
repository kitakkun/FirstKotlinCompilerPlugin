package com.example

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

// ComponentRegistrar is deprecated.
// instead of it, we must use CompilerPluginRegistrar.
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class MyCompilerRegistrar : CompilerPluginRegistrar() {
    // For now, not intended to support K2.
    override val supportsK2: Boolean get() = false

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[MyCommandLineProcessor.KEY_ENABLED] == false) {
            return
        }

        val annotations = configuration[MyCommandLineProcessor.KEY_ANNOTATIONS]
            ?: error("MyPlugin is enabled but no annotations are specified.")
        // ClassBuilderInterceptorExtension is deprecated.
        // It is not compatible with K2 compiler.
        // Instead of it, we must use
        // org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension or
        // org.jetbrains.kotlin.backend.jvm.extensions.ClassGeneratorExtension
        // ClassGeneratorExtension is simpler than IrGenerationExtension because the latter is low-level.
        IrGenerationExtension.registerExtension(MyIrGenerationExtension(annotations))
    }
}
