@file:Suppress("PublicApiImplicitType")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

val javaVersion = "11"
val pluginSinceBuild = "203"
val pluginUntilBuild = ""
val pluginVersion = "1.3.0"

//ant.importBuild("release.xml")

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.vladsch.PluginDevelopersToolbox"
version = pluginVersion

repositories {
    mavenLocal()
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2020.3.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf())
}

dependencies {
    annotationProcessor("junit:junit:4.13.2")
    testImplementation("junit:junit:4.13.2")
}

sourceSets {
    main {
        java {
            setSrcDirs(mutableListOf("src/main/java"))
            resources.setSrcDirs(mutableListOf("src/main/resources", "src/main/resources-flex"))
        }

        kotlin {
            setSrcDirs(mutableListOf("src/main/java"))
        }
    }

    test {
        java {
            setSrcDirs(mutableListOf("src/test/java"))
        }

        kotlin {
            setSrcDirs(mutableListOf("src/test/java"))
        }
    }

}

tasks { // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion
    }

    processResources {

    }

    patchPluginXml {
        sinceBuild.set(pluginSinceBuild)
        untilBuild.set(pluginUntilBuild)
        version.set(pluginVersion)
    }

    buildPlugin {

    }

    runPluginVerifier {

    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
