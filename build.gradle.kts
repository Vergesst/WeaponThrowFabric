import org.gradle.kotlin.dsl.invoke

plugins {
    id("fabric-loom") version "1.10.1"
    id("maven-publish")
}

// basic settings of project
version = project.property("mod_version") as String
group = project.property("maven_group") as String
val targetJvmVersion = project.property("target_jvm")

// extension function
fun DependencyHandler.compileAndRuntimeOnly(
    dependencyPath: String,
    config: (ExternalModuleDependency.() -> Unit) = {}
) {
    add("modCompileOnly", dependencyPath, config)
    add("modRuntimeOnly", dependencyPath, config)
}

base {
    archivesName.set(project.property("archives_base_name") as String)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(project.property("java_version") as String)

    withSourcesJar()
}

// fabric loom settings
loom {
    splitEnvironmentSourceSets()

    // other settings
    mods {
        register("weaponthrow") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

//fabricApi {
//    configureDataGeneration {
//        client = true
//    }
//}

// repo sources
repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    mavenCentral()
    // Add Modrinth Maven repository
    maven {
        url = uri("https://api.modrinth.com/maven/")
    }
    // Add Cloth Config's official Maven repository
    maven {
        url = uri("https://maven.shedaniel.me/")
    }
    // Add TerraformersMC repository (if you need it, though Modrinth often covers it)
    maven {
        url = uri("https://maven.terraformersmc.com/releases/")
    }
    // If you are using Fabric Loom, maven.fabricmc.net is usually included, but explicitly adding doesn't hurt
    maven {
        url = uri("https://maven.fabricmc.net/")
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    // other dependencies
    compileAndRuntimeOnly("me.shedaniel.cloth:cloth-config-fabric:${project.property("cloth_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    compileAndRuntimeOnly("com.terraformersmc:modmenu:${project.property("modmenu_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),

        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // You are really a genius whose favorite type is Any? ... that's foolish

    // java version -- current 17
    options.release.set(targetJvmVersion.toString().toInt())

    // extra options to enable pattern matching in Java17 -- preview feature
    options.compilerArgs.add("--enable-preview")
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.

    }
}
