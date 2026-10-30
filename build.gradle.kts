plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// The wasm build's JavaScript tooling (webpack and its dev server) comes from the Kotlin Gradle
// plugin with versions that have known advisories. None of it ships in the app, but the lock file
// is kept on patched releases: overrides here (serialize-javascript and uuid across a major: terser and sockjs use only
// serialize() and v4(), which are unchanged), the dev server's major through
// `versions`. Regenerate kotlin-js-store/yarn.lock with `./gradlew kotlinUpgradeYarnLock`.
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().apply {
        resolution("body-parser", "1.20.9")
        resolution("compression", "1.8.2")
        resolution("engine.io", "6.6.10")
        resolution("fast-uri", "3.1.9")
        resolution("follow-redirects", "1.16.1")
        resolution("js-yaml", "4.3.2")
        resolution("launch-editor", "2.14.2")
        resolution("proxy-addr", "2.0.8")
        resolution("qs", "6.16.1")
        resolution("shell-quote", "1.11.0")
        resolution("source-map-js", "1.2.2")
        resolution("serialize-javascript", "7.1.3")
        resolution("uuid", "11.1.1")
    }
}
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().versions.apply {
        webpack.version = "5.104.1"
        webpackDevServer.version = "5.2.6"
    }
}
