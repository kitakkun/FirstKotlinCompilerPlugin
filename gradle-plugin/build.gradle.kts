plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("myPlugin") {
            id = "com.example.my-plugin"
            implementationClass = "com.example.MyPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib", "1.9.0"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.9.0")

    compileOnly("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"])
            groupId = "com.example.my-plugin"
            artifactId = "com.example.my-plugin.gradle.plugin"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
