import com.example.MyPluginExtension

plugins {
    kotlin("jvm")
    id("application")
    id("com.example.my-plugin")
}

configure<MyPluginExtension> {
    enabled = true
    annotations = listOf("HogeAnnotation")
}

application {
    mainClass = "MainKt"
}
