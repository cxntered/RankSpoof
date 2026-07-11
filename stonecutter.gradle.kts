import me.modmuss50.mpp.ReleaseType

plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

val modName: String = sc.properties["mod.name"]
val modVersion: String = sc.properties["mod.version"]

stonecutter active "1.21.11"

stonecutter {
    parameters {
        swaps["mod_id"] = "\"${sc.properties.get<String>("mod.id")}\";"
        swaps["mod_name"] = "\"${modName}\";"
    }

    tasks.order("publishModrinth")
}

publishMods.github {
    displayName = "$modName $modVersion"
    version = "v${modVersion}"
    changelog = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."
    type = when {
        "beta" in modVersion.lowercase() -> ReleaseType.BETA
        "alpha" in modVersion.lowercase() -> ReleaseType.ALPHA
        else -> ReleaseType.STABLE
    }

    accessToken = property("github.token").toString()
    repository = sc.properties.get<String>("publish.github.repo")
    commitish = sc.properties.get<String>("publish.github.branch")
    tagName = version

    allowEmptyFiles = true
}
