import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.3.0" // Adds runServer and runMojangMappedServer tasks for testing
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1" // Generates plugin.yml based on the Gradle config
}

group = "org.esoteric"
version = "0.1.0"
description = "The Minecraft plugin behind all the survival features of The Slimy Swamp Minecraft server."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.EsotericOrganisation:tss-core-plugin:0.2.1:dev-all")
    compileOnly("com.github.EsotericOrganisation:tss-ranks-plugin:0.2.2:dev")

    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}

bukkitPluginYaml {
    name = "TSSSurvival"
    authors.addAll("Esoteric Organisation", "Esoteric Enderman")
    description = project.description

    version = project.version.toString()
    apiVersion = "1.21"
    main = "org.esoteric.tss.minecraft.plugins.survival.TSSSurvivalPlugin"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    depend.addAll("TSSCore", "TSSRanks")
}
