pluginManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    plugins {
        kotlin("jvm") version "1.9.0" apply false
        id("com.example.my-plugin") version "1.0.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}
