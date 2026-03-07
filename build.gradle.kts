plugins {
    `java-library`
    alias(libs.plugins.versions)
}

tasks.wrapper {
    gradleVersion = libs.versions.gradle.get()
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")

    base {
        archivesName = "${rootProject.name}-${project.name}"
    }

    repositories {
        mavenCentral()
    }

    configure<JavaPluginExtension> {
        // https://docs.gradle.org/current/userguide/toolchains.html
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
            // Use Eclipse Temurin (provided by Adoptium).
            vendor.set(JvmVendorSpec.ADOPTIUM)
        }

        withSourcesJar()
        withJavadocJar()
    }

    configurations.all {
        resolutionStrategy.failOnDynamicVersions()

        // Exclude logback provided via spring.
        exclude(group = "ch.qos.logback", module = "logback-classic")

    }

    afterEvaluate {
        dependencies {
            implementation(platform(libs.spring.boot.bom))

            implementation(libs.slf4j.api)
            runtimeOnly(libs.logback.classic)

            implementation(libs.requirements)

            testRuntimeOnly(libs.junit.platform.launcher)
            testImplementation(libs.junit.jupiter)
            testImplementation(libs.assertj.core)
        }
    }

    tasks.withType<Javadoc> {
        (options as StandardJavadocDocletOptions).apply {
            // Declare custom tags so Javadoc doesn't warn about them.
            // Format: "tagname:locations:title" — locations 'a' means anywhere
            tags("require:a:Preconditions and Postconditions")
        }
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

    // tasks.named("build") { dependsOn("publishToMavenLocal") }

    tasks.test {
        useJUnitPlatform()

        // ignore failing tests
        ignoreFailures = true

        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.test)
        reports {
            xml.required = true
            html.required = true
        }
    }
}
