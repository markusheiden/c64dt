import org.apache.tools.ant.taskdefs.condition.Os

extra["openjfxPlatform"] = when {
    Os.isFamily(Os.FAMILY_WINDOWS) -> "win"
    Os.isFamily(Os.FAMILY_MAC) -> if (Os.isArch("aarch64")) "mac-aarch64" else "mac"
    Os.isFamily(Os.FAMILY_UNIX) -> if (Os.isArch("aarch64")) "linux-aarch64" else "linux"
    else -> error("Unsupported OS")
}
