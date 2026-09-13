plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.ploceus)
    alias(libs.plugins.mod.publish)
}

ploceus.setIntermediaryGeneration(2)

group = property("mod.group") as String
version = property("mod.version") as String

repositories {
    mavenCentral()
    maven("https://maven.legacyfabric.net/")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.legacy.yarn)
    modImplementation(libs.fabric.loader)
    modRuntimeOnly(libs.devauth)
    ploceus.dependOsl(libs.versions.osl.get())
}

tasks.processResources {
    fun MutableMap<String, String>.register(key: String, property: String) {
        val value = project.property(property) as String
        inputs.property(key, value)
        set(key, value)
    }

    val props = buildMap {
        register("id", "mod.id")
        register("version", "mod.version")
        register("name", "mod.name")
        register("description", "mod.description")
        register("minecraft", "mod.mc_version")
    }

    filesMatching("fabric.mod.json") { expand(props) }
}

loom {
    afterEvaluate {
        val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
            componentFilter {
                it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
            }
        }.files.first()

        runConfigs.named("client") {
            generateRunConfig = true
            preferGradleTask = true
            runDirectory = rootProject.file("run")

            jvmArguments.add("-XX:+AllowEnhancedClassRedefinition")
            jvmArguments.add("-javaagent:$mixinJarFile")
            systemProperties.put("devauth.enabled", "true")
            systemProperties.put("mixin.debug.export", "true")
        }

        runConfigs.named("server") { generateRunConfig = false }
    }

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }
}

// make sure `modrinth.token` and `github.token` are set in your user gradle properties
val modrinthToken: String? = findProperty("modrinth.token")?.toString()
val githubToken: String? = findProperty("github.token")?.toString()

publishMods {
    file = tasks.remapJar.flatMap { it.archiveFile }

    val modName = property("mod.name") as String
    val modVersion = property("mod.version") as String
    displayName = "$modName $modVersion"
    version = "v$modVersion"
    type = when {
        "beta" in modVersion.lowercase() -> BETA
        "alpha" in modVersion.lowercase() -> ALPHA
        else -> STABLE
    }

    changelog = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."
    modLoaders.add("ornithe")

    dryRun = modrinthToken.isNullOrBlank() || githubToken.isNullOrBlank()

    modrinth {
        accessToken = modrinthToken
        projectId = property("publish.modrinth.id") as String
        minecraftVersions.add(property("mod.mc_version") as String)
    }

    github {
        accessToken = githubToken
        repository = property("publish.github.repo") as String
        commitish = property("publish.github.branch") as String
        tagName = version
    }
}
