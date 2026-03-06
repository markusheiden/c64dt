pluginManagement {
    val versionsPluginVersion: String by settings
    val dependencyManagementPluginVersion: String by settings
    plugins {
        id("com.github.ben-manes.versions") version versionsPluginVersion
        id("io.spring.dependency-management") version dependencyManagementPluginVersion
    }
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlinVersion"]}")
    }
}

rootProject.name = "c64dt"

// Include all subfolder which contains a Gradle build file with the name of the folder
rootProject.projectDir.listFiles()?.filter { it.isDirectory }?.forEach { dir ->
    val dirName = dir.name
    if (dir.resolve("$dirName.gradle.kts").exists()) {
        println("[${rootProject.name}] Adding subproject '$dirName'")

        include(dirName)
        project(":$dirName").apply {
            projectDir = dir
            buildFileName = "$dirName.gradle.kts"
        }
    }
}
