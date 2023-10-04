import com.example.MyPluginExtension

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.example:my-plugin:1.0.0")
    }
}

plugins {
    kotlin("jvm")
    id("application")
}

// because my-plugin is published to mavenLocal
// we must apply my-plugin here instead of plugins block
apply(plugin = "my-plugin")

repositories {
    mavenCentral()
    mavenLocal()
}

configure<MyPluginExtension> {
    enabled = true
    annotations = listOf("HogeAnnotation")
}

application {
    mainClass = "MainKt"
}
