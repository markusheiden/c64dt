dependencies {
    implementation(project(":assembler"))
    implementation(project(":bytes"))
    implementation(project(":charset"))
    implementation(project(":common"))

    implementation(libs.commons.io)
    implementation(libs.jakarta.xml.bind.api)
    runtimeOnly(libs.jaxb.runtime)
}
