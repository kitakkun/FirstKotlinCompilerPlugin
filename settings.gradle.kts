pluginManagement {
    repositories {
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "1.9.0" apply false
        kotlin("kapt") version "1.9.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":gradle-plugin")
include(":kotlin-plugin")
