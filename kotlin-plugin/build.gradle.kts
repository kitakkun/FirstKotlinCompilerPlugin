plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    // in the latest version, we must specify implementation instead of compileOnly
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.0")

    compileOnly("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"])
            groupId = "com.example"
            artifactId = "kotlin-plugin"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
