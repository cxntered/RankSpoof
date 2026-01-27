plugins {
    id("net.fabricmc.fabric-loom-remap")
}

val mcMin = property("mod.mc_min").toString()
val mcMax = property("mod.mc_max").toString()
val mcDep = if (mcMax.isEmpty()) "~${mcMin}" else ">=${mcMin} <=${mcMax}"

version = "${property("mod.version")}+$mcMin"
base.archivesName = property("mod.name").toString()

val requiredJava = JavaVersion.VERSION_21

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    strictMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "DevAuth", "me.djtheredstoner")
    maven("https://maven.isxander.dev/releases") { name = "Xander" }
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
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // adds names to lambdas - useful for mixins
    }

    afterEvaluate {
        val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
            componentFilter {
                it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
            }
        }.files.first()

        runConfigs.all {
            ideConfigGenerated(true)

            vmArg("-XX:+AllowEnhancedClassRedefinition")
            vmArg("-javaagent:$mixinJarFile")
            property("mixin.debug.export", "true")

            runDir = "../../run"
        }

        runConfigs.remove(runConfigs["server"])
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks {
    processResources {
        val props = mapOf(
            "id" to project.property("mod.id"),
            "name" to project.property("mod.name"),
            "version" to project.property("mod.version"),
            "fabric_loader" to project.property("deps.fabric_loader"),
            "minecraft" to mcDep,
            "yacl" to project.property("deps.yacl")
        )
        inputs.properties(props)

        filesMatching("fabric.mod.json") { expand(props) }
        filesMatching("*.mixins.json") { expand("java" to "JAVA_${requiredJava.majorVersion}") }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs"))
        dependsOn("build")
    }
}
