rootProject.name = "c64dt"

// Include all subfolders which contain a Gradle build file with the name of the folder
rootProject.projectDir
    .walk()
    .maxDepth(1)
    .filter { it.isDirectory && it != rootProject.projectDir }
    .forEach { dir ->
        val moduleName = dir.name
        val gradleFileRegex = Regex("${Regex.escape(moduleName)}\\.gradle.kts")

        val buildFile = dir.listFiles()?.find { file ->
            file.isFile && gradleFileRegex.matches(file.name)
        }

        if (buildFile != null) {
            println("[${rootProject.name}] Adding subproject '$moduleName'")

            include(moduleName)
            project(":$moduleName").projectDir = dir
            project(":$moduleName").buildFileName = buildFile.name
        }
    }
