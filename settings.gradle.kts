pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.6"
    id("dev.kikugie.loom-back-compat") version "0.3"
}

stonecutter {
    create(rootProject) {
        versions("1.21.10", "1.21.11", "26.1", "26.2")
        vcsVersion = "26.2"
    }
}

// Configures the root project Gradle name based on the value in `stonecutter.properties.toml`
rootProject.name = sc.properties["mod.name"]
