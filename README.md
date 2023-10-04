# FirstKotlinCompilerPlugin

[日本語版はこちら](README-ja.md)

This repository contains a simple Kotlin compiler plugin implementation
for the purpose of demonstrating how to write a Kotlin compiler plugin.

## Module structure

- `gradle-plugin`: Gradle plugin to apply the Kotlin compiler plugin to any projects.
- `kotlin-plugin`: Custom compiler plugin implementation.
- `demo`: Demo project to see the compiler plugin in action.

## How it modifies the code

This compiler plugin is quite simple.
It just adds statements to print out current system time at the beginning of function.
Annotation classes to be processed can be specified in the Gradle plugin configuration.

#### Input code:

```kotlin
annotation class HogeAnnotation

@HogeAnnotation
fun main() {
}
```

#### Output code should be something like this:

```kotlin
fun main() {
    val startTime = System.currentTimeMillis()
    println(startTime)
}
```

### Gradle plugin configuration

If `enabled` is set to `false`, the compiler plugin will not be applied.
`annotations` is a list of annotation classes to be processed by the compiler plugin.

#### Groovy

```groovy
myPlugin {
    enabled = true
    annotations = ["HogeAnnotation"]
}
```

#### KTS

```kotlin
configure<MyPluginExtension> {
    enabled = true
    annotations = listOf("HogeAnnotation")
}
```

### How to test on your local machine

1. Run `./gradlew publishToMavenLocal` to publish `gradle-plugin` and `kotlin-plugin` to maven local.
2. Run `./gradlew demo:run` to see the compiler plugin in action.

By default, you will see the output like this:

```
> Task :demo:run
1696419167450
```

## Additional resources

Want to know more about Kotlin compiler plugin? These resources might be helpful for you.
Note that some of them are a bit outdated.

- [KotlinConf 2018 - Writing Your First Kotlin Compiler Plugin by Kevin Most](https://www.youtube.com/watch?v=w-GMlaziIyo)
- [Crash Course on the Kotlin Compiler by Amanda Hinchman-Dominguez](https://www.youtube.com/watch?v=wUGfuWHCqrc)
- [K2 Compiler plugins by Mikhail Glukhikh](https://www.youtube.com/watch?v=Pl-89n9wDqo)
