package com.example

import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

// KotlinGradleSubplugin has been removed in Kotlin 1.7.0.
// https://kotlinlang.org/docs/compatibility-guide-17.html#basic-terms
// some interfaces have been changed.
// Also, org.gradle.api.Plugin<T> and KotlinGradleSubplugin are merged into KotlinCompilerPluginSupportPlugin.
// Specify this class to build.gradle instead of Plugin class.
@Suppress("unused")
@AutoService(KotlinCompilerPluginSupportPlugin::class)
class MyPlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        target.extensions.create(
            "myPlugin",
            MyPluginExtension::class.java,
        )
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return kotlinCompilation.project.plugins.hasPlugin(MyPlugin::class.java)
    }

    // Implementation is a bit different from KotlinGradleSubplugin's one.
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val extension =
            kotlinCompilation.project.extensions.findByType(MyPluginExtension::class.java) ?: MyPluginExtension()
        if (extension.enabled && extension.annotations.isEmpty()) {
            error("MyPlugin is enabled but no annotations are specified.")
        }

        val annotationOptions = extension.annotations.map {
            SubpluginOption(key = "myPluginAnnotation", value = it)
        }
        val enabledOption = SubpluginOption(
            key = "enabled",
            value = extension.enabled.toString(),
        )
        return kotlinCompilation.target.project.provider {
            listOf(enabledOption) + annotationOptions
        }
    }

    override fun getCompilerPluginId(): String {
        return "my-plugin"
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = "com.example.my-plugin",
            artifactId = "kotlin-plugin",
            version = "1.0.0",
        )
    }
}
