plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "c64dt"

include("assembler")
include("browser")
include("bytes")
include("charset")
include("common")
include("disk")
include("gui")
include("net")
include("reassembler")
include("reassembler-gui")
