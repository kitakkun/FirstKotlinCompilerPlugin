package com.example

import org.gradle.api.Project

class MyPlugin: org.gradle.api.Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(
            "myPlugin",
            MyPluginExtension::class.java,
        )
    }
}

open class MyPluginExtension {
    var enabled: Boolean = true
    var annotations: List<String> = emptyList()
}
