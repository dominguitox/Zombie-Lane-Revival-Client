# Zombie Lane Client (Alpha)

Unofficial, non-commercial source release of the Zombie Lane client and its launcher.

This repository contains the sanitized client source and the release build script. It does not include game assets, game data, a Java runtime, the port JARs, LWJGL binaries, the server, server data, or generated release artifacts. The release is assembled locally from user-supplied assets.

## Source modules

- Client game sources: `src/game-ios`, `src/compat-ios`, `src/runtime-ios`
- Launcher sources: `src/launcher`, `src/launcher-ios`
- Desktop and platform sources: `src/platform-1001`, `src/j2me-desktop`, `src/gles-desktop`, `src/audio-desktop`, `src/compat-android`, `src/stub-runtime`
- Release stubs: `src-release` (the no-op player-edition `DevMode` and `LauncherDevOptions`)
- Build tooling: `tools`

The public tree does not contain `src-dev`, server code, server data, or development launcher hooks.

## Requirements

- Windows with PowerShell 5.1 or newer.
- A JDK 21 or newer with `javac` on `PATH`, or pass `-JavacPath`. The build was verified with Temurin 25.0.4.1.
- Git, for source provenance and the `-Release` check.
- An external assets root containing:
  - `runtime\bin\java.exe` (Java 21 or newer runtime; this runtime is copied into the release)
  - `lib\` (the port JARs and LWJGL 3.4.3)
  - `game-assets-ios\` (the game asset bundle)

The build does not require `runtime\bin\javac.exe` in the assets root. The compiler comes from the JDK on `PATH` or from `-JavacPath`.

## Build

From the repository root:

```powershell
powershell -ExecutionPolicy Bypass -File tools\build-client.ps1 `
    -AssetsRoot "C:\path\to\Zombie-Lane-Assets" `
    -Version 0.1.1 `
    -Release
```

The default output is a sibling of this repository:

- Release folder: `..\Zombie-Lane-Alpha-0.1.1`
- Release zip: `..\Zombie-Lane-Alpha-0.1.1.zip`

Options:

- `-AssetsRoot`: external assets root. The default is `C:\Proyectos\Zombie-Lane-Assets`.
- `-OutputRoot`: override the release folder. The default is `..\Zombie-Lane-Alpha-<version>`.
- `-Version`: release version. The default is `0.1.1`.
- `-JavacPath`: explicit JDK compiler path.
- `-Release`: require clean, committed sources. Use this for a public release.
- `-SkipZip`: assemble the folder but do not create the zip.
- `-SkipSmoke`: skip the headless launcher smoke test.

Without `-Release`, uncommitted sources are allowed and `VERSION.txt` records the build as dirty. A public release must be built with `-Release`.

The build:

1. Compiles `tools/client-classes.txt` against `src` and `src-release` with `--release 21` and UTF-8.
2. Verifies the no-op release `DevMode` and `LauncherDevOptions` bytecode with `javap`.
3. Copies `runtime`, `lib`, and `game-assets-ios` from the assets root, excluding backup directories.
4. Copies the compiled classes into `client-overrides` and adds the launcher, `VERSION.txt`, and `RELEASE-NOTES.txt`.
5. Rejects server code and data, sources, tools, tests, docs, build scripts, dev classes, smoke/probe classes, archives, and other unexpected entries.
6. Runs a headless `zlport.launcher.IosMain --help` smoke test and checks that the `[dev]` banner is absent.
7. Creates and verifies the zip, then prints its size and SHA-256.

### Build verification

Verified on 2026-10-23 on Windows with Temurin 25.0.4.1 and the bundled Java 21.0.8 runtime:

- The build compiles 62 classes.
- The compiled class set contains the `v0.1-alpha` classes plus the launcher-validation inner classes (`PreLauncherDialog$1` and `PreLauncherDialog$FieldFilter`). `PreLauncherDialog` and `ServerConnection` include the validator changes; the intended release stubs are `zlport.dev.DevMode` and `zlport.dev.LauncherDevOptions`.
- Common files under `runtime`, `lib`, and `game-assets-ios` are byte-identical to `v0.1-alpha`.
- The headless smoke test exits 0 and does not print the `[dev]` banner.
- The assembled folder and zip pass the release allowlist and sanitization checks.

The `0.1.1` package does not include `mission-backups` or three stale root files that existed only in the `0.1` output (`0`, `1.png`, `2.png`). Those files are not part of the canonical asset tree.

### Version 0.1.1

`0.1.1` is the first release assembled from the sanitized public tree. It fixes the player-edition build (no development hooks) and replaces the previous all-source compile with the explicit client class list.

The remaining launcher fix planned for the final `0.1.1` release is additional languages. Field validation is included; the loading-screen crash is still under investigation.

## Run

Extract the zip and run:

```text
Run Zombie Lane Alpha.bat
```

The launcher defaults to `http://127.0.0.1:6767/dead/`. Change the server in the launcher or pass `-Dzlport.server.url=...` to Java. A Java runtime is bundled in `runtime\`; no system Java installation is required.

Local mode can run without a server. Server mode requires a compatible server.

## Sanitization

The public source tree and the generated release are checked for development and server artifacts:

- No `src-dev`, server code, server data, or user credential files.
- `src-release` contains only the no-op release stubs.
- `client-overrides` is generated and ignored by Git.
- `runtime`, `lib`, and `game-assets-ios` are ignored by Git and supplied externally.
- The release contains only `runtime`, `lib`, `game-assets-ios`, `client-overrides`, `Run Zombie Lane Alpha.bat`, `VERSION.txt`, and `RELEASE-NOTES.txt`.

## Server

The server is separate and is not included in this repository. Local mode can run without it; server mode requires a compatible server.

## Status

Alpha. Expect incomplete behavior, bugs, missing assets, and changes to internal interfaces.

## License

No license file is included. The license must be chosen by the maintainer before redistribution.
