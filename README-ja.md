# FirstKotlinCompilerPlugin

[English version is here](README.md)

このリポジトリは、Kotlin Compiler Pluginの作成方法を理解する目的で作成したものです。
簡単なコンパイラプラグインの実装が含まれていますので、もしよろしければご参考まで。

## モジュール構成

- `gradle-plugin`: Gradleプラグインの実装部分になります。Kotlinコンパイラプラグインを任意のプロジェクトに適用するためのものです。
- `kotlin-plugin`: Kotlinコンパイラプラグインの実装部分になります。
- `demo`: コンパイラプラグインの動作を確認するためのデモプロジェクトです。

## プラグインの挙動

このプラグインは、関数の先頭に現在時刻を出力するコードを追加するだけのものです。
Gradleプラグインの設定で、処理対象のアノテーションクラスを指定することができます。

#### 入力コード:

```kotlin
annotation class HogeAnnotation

@HogeAnnotation
fun main() {
}
```

#### 出力コード（イメージ）:

```kotlin
fun main() {
    val startTime = System.currentTimeMillis()
    println(startTime)
}
```

### Gradleプラグインの設定

`enabled`が`false`に設定されている場合、コンパイラプラグインは適用されません。
`annotations`には、コンパイラプラグインで処理するアノテーションクラスのリストを指定します。

#### Groovy

```groovy
myPlugin {
    enabled = true
    annotations = ["HogeAnnotation"]
}
```

### KTS

```kotlin
configure<MyPluginExtension> {
    enabled = true
    annotations = listOf("HogeAnnotation")
}
```

### ローカル環境でのテスト方法

1. `./gradlew publishToMavenLocal`を実行して、`gradle-plugin`と`kotlin-plugin`をmaven localに公開します。
2. `./gradlew demo:run`を実行して、コンパイラプラグインの動作を確認します。

デフォルトでは、以下のような出力が表示されます。

```
> Task :demo:run
1696419167450
```

## 参考資料

Kotlin Compiler Pluginについて詳しく知りたい方は、以下の資料が参考になると思います。
一部情報が古くそのまま適用できないものも含まれていますが、基本的な構造は大きくは変わっていません。

- [KotlinConf 2018 - Writing Your First Kotlin Compiler Plugin by Kevin Most](https://www.youtube.com/watch?v=w-GMlaziIyo)
- [Crash Course on the Kotlin Compiler by Amanda Hinchman-Dominguez](https://www.youtube.com/watch?v=wUGfuWHCqrc)
- [K2 Compiler plugins by Mikhail Glukhikh](https://www.youtube.com/watch?v=Pl-89n9wDqo)
