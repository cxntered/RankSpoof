plugins {
    id("dev.kikugie.loom-back-compat")
    id("me.modmuss50.mod-publish-plugin")
}

val modName: String = sc.properties["mod.name"]
val modVersion: String = sc.properties["mod.version"]

version = "$modVersion+${sc.current.version}"
base.archivesName = modName

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

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
    loomx.applyMojangMappings()

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
            "version" to modVersion,
            "name" to modName,
            "description" to sc.properties["mod.description"],
            "mc_compat" to (sc.properties.getOrNull<String>("mod.mc_compat") ?: sc.current.version),
            "yacl" to sc.properties["deps.yacl"]
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") { expand(props) }
        filesMatching("*.mixins.json") { expand("java" to "JAVA_${requiredJava.majorVersion}") }
    }

    jar {
        inputs.property("archivesName", base.archivesName)

        from("LICENSE") {
            rename { "${it}_${inputs.properties["archivesName"]}" }
        }
    }

    register<Copy>("buildAndCollect") {
        description = "Builds mod jars and copies results to `build/libs/`"
        group = "build"

        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs"))
    }
}

// make sure `modrinth.token` and `github.token` are set in your user gradle properties
publishMods {
    file = loomx.modJar.get().archiveFile
    displayName = "$modName $modVersion for ${sc.current.version}"
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

        val mcReleases = sc.properties.rawOrNull("mod:mc_releases")?.asList()?.map { it.toString() }
        minecraftVersions.addAll(mcReleases ?: listOf(sc.current.version))

        requires("yacl")
        optional("modmenu")
    }

    // github release is created in `stonecutter.gradle.kts`
    github {
        accessToken = property("github.token").toString()
        parent(rootProject.tasks.named("publishGithub"))
    }
}
