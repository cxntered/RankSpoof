plugins {
    id("me.modmuss50.mod-publish-plugin")
}

val modVersion: String = sc.properties["mod.version"]

val mcMin: String = sc.properties["mod.mc_min"]
val mcMax: String = sc.properties["mod.mc_max"]
val mcDep = if (mcMax.isEmpty()) "~${mcMin}" else ">=${mcMin} <=${mcMax}"

version = "$modVersion+$mcMin"
base.archivesName = property("mod.name").toString()

val requiredJava = JavaVersion.VERSION_21

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    strictMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "DevAuth", "me.djtheredstoner")
    strictMaven("https://maven.isxander.dev/releases", "Xander", "dev.isxander", "org.quiltmc.parsers")
    strictMaven("https://maven.terraformersmc.com/releases", "TerraformersMC", "com.terraformersmc")
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${property("deps.devauth")}")
    modImplementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}+${sc.current.version}-fabric")
    modImplementation("com.terraformersmc:modmenu:${property("deps.mod_menu")}")
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
            runDirectory = rootProject.file("run")

            jvmArguments.add("-XX:+AllowEnhancedClassRedefinition")
            jvmArguments.add("-javaagent:$mixinJarFile")
            systemProperties.put("devauth.enabled", "true")
            systemProperties.put("mixin.debug.export", "true")
        }
    }

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // adds names to lambdas
    }

    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks {
    processResources {
        val props = mapOf(
            "id" to sc.properties["mod.id"],
            "name" to sc.properties["mod.name"],
            "version" to modVersion,
            "fabric_loader" to sc.properties["deps.fabric_loader"],
            "minecraft" to mcDep,
            "yacl" to sc.properties["deps.yacl"]
        )
        inputs.properties(props)

        filesMatching("fabric.mod.json") { expand(props) }
        filesMatching("*.mixins.json") { expand("java" to "JAVA_${requiredJava.majorVersion}") }
    }

    register<Copy>("buildAndCollect") {
        description = "Builds mod jars and copies results to `build/libs/`"
        group = "build"

        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs"))
    }
}

// make sure `modrinth.token` and `github.token` are set in your user gradle properties
publishMods {
    file = project.tasks.remapJar.get().archiveFile
    displayName = modVersion
    version = "v$modVersion"
    changelog = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."
    type = when {
        "beta" in modVersion.lowercase() -> BETA
        "alpha" in modVersion.lowercase() -> ALPHA
        else -> STABLE
    }

    modLoaders.add("fabric")

    modrinth {
        accessToken = property("modrinth.token").toString()
        projectId = sc.properties.get<String>("publish.modrinth.id")

        if (mcMax.isEmpty()) {
            minecraftVersions.add(mcMin)
        } else {
            minecraftVersionRange { start = mcMin; end = mcMax }
        }

        requires("yacl")
        optional("modmenu")
    }

    // github release is created in `stonecutter.gradle.kts`
    github {
        accessToken = property("github.token").toString()
        parent(rootProject.tasks.named("publishGithub"))
    }
}
