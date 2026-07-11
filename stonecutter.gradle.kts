plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

val modName: String = sc.properties["mod.name"]
val modVersion: String = sc.properties["mod.version"]

stonecutter active "26.2"

stonecutter {
    parameters {
        swaps["mod_id"] = "\"${sc.properties.get<String>("mod.id")}\";"
        swaps["mod_name"] = "\"${modName}\";"
    }

    tasks.order("publishModrinth")
}

val githubToken: String? = findProperty("github.token")?.toString()

publishMods {
    displayName = "$modName $modVersion"
    version = "v${modVersion}"
    changelog = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."
    type = when {
        "beta" in modVersion.lowercase() -> BETA
        "alpha" in modVersion.lowercase() -> ALPHA
        else -> STABLE
    }

    dryRun = githubToken.isNullOrBlank()

    github {
        accessToken = githubToken
        repository = sc.properties.get<String>("publish.github.repo")
        commitish = sc.properties.get<String>("publish.github.branch")
        tagName = version

        allowEmptyFiles = true
    }
}
