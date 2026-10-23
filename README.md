# Zombie Lane Client (Alpha)

Unofficial, non-commercial source release of the Zombie Lane client and its launcher.

This repository contains source code only. It does not include game assets, game data, a Java runtime, the port JARs, LWJGL, the server, server data, build tooling, tests, documentation, or generated artifacts.

## Source modules

- Client game sources: `src/game-ios`, `src/compat-ios`, `src/runtime-ios`
- Launcher sources: `src/launcher`, `src/launcher-ios`
- Desktop and platform sources: `src/platform-1001`, `src/j2me-desktop`, `src/gles-desktop`, `src/audio-desktop`, `src/compat-android`, `src/stub-runtime`

## Requirements

- Java 21 (JDK).
- The Zombie Lane game asset bundle (`game-assets-ios`) and the port JARs (`lib/*.jar`) must be provided by the user. They are not part of this repository.
- LWJGL 3.4.3 libraries are required by the desktop launcher. They are not included.

## Build

Not verified. The original build compiles a selected class list against the port JARs with `javac --release 21`. That class list, the port JARs, LWJGL, and the development-only classes referenced by the launcher are not included, so a clean build from this repository alone is not verified.

## Run

Not verified. The launcher expects the game asset bundle through `-Dzlport.ios.bundle=<path>` and the port JARs on the classpath. The original client entry point is `zlport.launcher.IosMain`; the desktop launcher entry point is `zlport.launcher.Main`.

## Server

The server is separate and is not included in this repository. Local mode can run without it; server mode requires a compatible server.

## Status

Alpha. Expect incomplete behavior, bugs, missing assets, and changes to internal interfaces.

## License

No license file is included. The license must be chosen by the maintainer before redistribution.
