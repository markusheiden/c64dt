dependencies {
    implementation(project(":assembler"))
    implementation(project(":bytes"))
    implementation(project(":common"))
    implementation(project(":reassembler"))

    implementation(libs.spring.beans)
    implementation(libs.spring.context)
    implementation(libs.jakarta.annotation.api)
}
