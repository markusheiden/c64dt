val openjfxPlatform: String by rootProject.extra

dependencies {
    implementation(project(":bytes"))
    implementation(project(":charset"))
    implementation(project(":common"))

    val openjfxVersion = libs.versions.openjfx.get()
    implementation("org.openjfx:javafx-base:$openjfxVersion:$openjfxPlatform")
    implementation("org.openjfx:javafx-controls:$openjfxVersion:$openjfxPlatform")
    implementation("org.openjfx:javafx-graphics:$openjfxVersion:$openjfxPlatform")
}
