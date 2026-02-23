import me.modmuss50.mpp.ReleaseType

plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

stonecutter active "1.21.11"
val modVersion = property("mod.version").toString()

stonecutter {
    parameters {
        swaps["mod_version"] = "\"${modVersion}\";"
        swaps["minecraft"] = "\"${node.metadata.version}\";"
    }

    tasks {
        order("publishModrinth")
    }
}

publishMods.github {
    displayName = "${property("mod.name")} $modVersion"
    version = "v$modVersion"
    type = ReleaseType.STABLE
    changelog = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

    accessToken = findProperty("github.token").toString()
    repository = property("publish.github.repo").toString()
    commitish = property("publish.github.branch").toString()
    tagName = version

    allowEmptyFiles = true
}
