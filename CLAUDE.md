# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test

```shell
./gradlew build          # build all submodules and publish to Maven local
./gradlew test           # run all tests
./gradlew :assembler:test                          # run tests for a single submodule
./gradlew :assembler:test --tests "*.FooTest"      # run a single test class
./gradlew publishToMavenLocal                      # publish without building
```

`build` always triggers `publishToMavenLocal` (wired in `build.gradle.kts`). Tests use JUnit Jupiter and ignore failures by default (`ignoreFailures = true`) — check JaCoCo reports rather than the build result to spot test failures.

## Architecture

Java library targeting C64 development tooling. All submodules share the package root `de.heiden.c64dt.<module>` and are published as Maven artifacts named `c64dt-<module>`.

**Dependency graph (leaves → dependents):**

- `common`, `bytes` — shared utilities, no inter-module deps
- `charset` → `bytes`, `common` — PETSCII/C64 charset encoding/decoding
- `assembler` → `bytes`, `charset`, `common` — 6502 opcodes, disassembler, code buffer
- `disk` — D64/D71 disk image reading (GCR encoding, BAM, directory, sector I/O)
- `gui` → `bytes`, `charset`, `common` — Swing and JavaFX components for C64 display (uses JavaFX plugin)
- `reassembler` → `assembler`, `bytes`, `charset`, `common` — higher-level reassembly with detectors, labels, XML persistence
- `net` → `bytes`, `charset`, `common`, `disk` — network drive protocol (NetDrive)
- `browser` → `charset`, `common`, `disk`, `gui` — disk image browser UI
- `reassembler-gui` → `assembler`, `bytes`, `common`, `reassembler` — Swing GUI for the reassembler, Spring-wired

## Gradle Setup

- Versions are centralized in `gradle/libs.versions.toml`
- All submodule config (jacoco, maven-publish, toolchain, common dependencies) lives in the root `build.gradle.kts` via `subprojects {}`
- Java 25 (Eclipse Temurin) via Gradle toolchain; `foojay-resolver-convention` auto-provisions it
- The `gui` module is the only one requiring the JavaFX Gradle plugin
