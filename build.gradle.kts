plugins {
    id("java-library")
    id("com.github.ben-manes.versions")
    id("io.spring.dependency-management") apply false
}

apply(from = "gradle/javafx.gradle.kts")

repositories {
    mavenCentral()
}

java {
    // https://docs.gradle.org/current/userguide/toolchains.html
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
        // Use Eclipse Temurin (provided by Adoptium).
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

allprojects {
    group = "de.heiden.c64dt"
    version = "1.0-SNAPSHOT"
}

val slf4jVersion: String by project
val logbackVersion: String by project
val requirementsVersion: String by project
val junitPlatformVersion: String by project
val junitVersion: String by project
val assertjVersion: String by project
val springVersion: String by project
val commonsIoVersion: String by project
val annotationVersion: String by project
val jaxbVersion: String by project
val glassfishJaxbVersion: String by project

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven-publish")

    base {
        archivesName = "${rootProject.name}-${project.name}"
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"("org.slf4j:slf4j-api:$slf4jVersion")
        "runtimeOnly"("ch.qos.logback:logback-classic:$logbackVersion")

        "implementation"("com.github.cowwoc.requirements:java:$requirementsVersion")

        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
        "testImplementation"("org.junit.jupiter:junit-jupiter:$junitVersion")
        "testImplementation"("org.assertj:assertj-core:$assertjVersion")
    }

    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        dependencies {
            dependencySet("org.springframework:$springVersion") {
                entry("spring-beans")
                entry("spring-context")
                entry("spring-core")
            }
            dependency("org.springframework:spring-beans:$springVersion")
            dependency("org.springframework:spring-context:$springVersion")

            dependency("commons-io:commons-io:$commonsIoVersion")

            dependency("jakarta.annotation:jakarta.annotation-api:$annotationVersion")
            dependency("jakarta.xml.bind:jakarta.xml.bind-api:$jaxbVersion")
            dependency("org.glassfish.jaxb:jaxb-runtime:$glassfishJaxbVersion")

            // dependencySet("org.openjfx:${openjfxVersion}:${openjfxPlatform}") {
            //     entry("javafx-base")
            //     entry("javafx-controls")
            //     entry("javafx-graphics")
            // }
        }
    }

    configurations.all {
        // Exclude logback provided via spring.
        exclude(group = "ch.qos.logback", module = "logback-classic")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                // Needed because otherwise the dependencies are only in the dependency management section of the pom.
                // So gradle users are missing the version in the dependencies section and fail to resolve the dependencies.
                versionMapping {
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
            }
        }
    }
    tasks.named("build") { dependsOn("publishToMavenLocal") }

    tasks.withType<Test> {
        useJUnitPlatform()

        // ignore failing tests
        ignoreFailures = true
    }
}

tasks.wrapper {
    gradleVersion = project.property("gradleVersion") as String
}
