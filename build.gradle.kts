plugins {
	id("fabric-loom") version "1.10.5"
	id("maven-publish")
}

class ModData {
	val id = property("mod.id").toString()
	val name = property("mod.name").toString()
	val version = property("mod.version").toString()
	val group = property("mod.group").toString()
}

class ModDependencies {
	operator fun get(name: String) = property("deps.$name").toString()
}

val mod = ModData()
val deps = ModDependencies()
val mcVersion = stonecutter.current.version
val mcDep = property("mod.mc_dep").toString()

version = "${mod.version}+$mcVersion"
group = mod.group

base {
	archivesName.set(mod.id)
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
	maven("https://maven.shedaniel.me/")
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://maven.nucleoid.xyz/")
}

loom {
	splitEnvironmentSourceSets()

	mods.create("breadcrumbs") {
		sourceSet(sourceSets["main"])
		sourceSet(sourceSets["client"])
	}
	runConfigs.all {
		ideConfigGenerated(true)
		runDir = "../../run"
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:$mcVersion")
	mappings("net.fabricmc:yarn:$mcVersion+build.${deps["yarn_build"]}:v2")
	modImplementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${deps["fabric_api"]}")

	modImplementation("eu.pb4:placeholder-api:${deps["placeholder_api"]}")

	modApi("me.shedaniel.cloth:cloth-config-fabric:${deps["cloth_config"]}") {
		exclude(group = "net.fabricmc.fabric-api")
	}
	modApi("com.terraformersmc:modmenu:${deps["modmenu"]}")
}

tasks.processResources {
	inputs.property("id", mod.id)
	inputs.property("name", mod.name)
	inputs.property("version", mod.version)
	inputs.property("mcdep", mcDep)
	inputs.property("javaver", java.sourceCompatibility.toString())
	inputs.property("fabricapiver", deps["fabric_api"])
	inputs.property("modmenuver", deps["modmenu"])
	inputs.property("clothconfigver", deps["cloth_config"])
	inputs.property("placeholderapiver", deps["placeholder_api"])

	val map = mapOf(
		"id" to mod.id,
		"name" to mod.name,
		"version" to mod.version,
		"mcdep" to mcDep,
		"javaver" to java.sourceCompatibility.toString(),
		"fabricapiver" to deps["fabric_api"],
		"modmenuver" to deps["modmenu"],
		"clothconfigver" to deps["cloth_config"],
		"placeholderapiver" to deps["placeholder_api"],
	)

	filesMatching("fabric.mod.json") { expand(map) }
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
	val java = if (stonecutter.eval(mcVersion, ">=1.20.6")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	targetCompatibility = java
	sourceCompatibility = java
}

tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(tasks.remapJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
	dependsOn("build")
}