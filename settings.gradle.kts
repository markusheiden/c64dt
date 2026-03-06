pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
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
