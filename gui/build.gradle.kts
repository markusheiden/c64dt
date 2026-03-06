plugins {
    alias(libs.plugins.javafx)
}

javafx {
    version = libs.versions.openjfx.get()
    modules("javafx.base", "javafx.controls", "javafx.graphics")
}

dependencies {
    implementation(project(":bytes"))
    implementation(project(":charset"))
    implementation(project(":common"))
}
