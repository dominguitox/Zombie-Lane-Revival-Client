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
    -Version 0.1.3 `
    -Release
```

The default output is a sibling of this repository:

- Release folder: `..\Zombie-Lane-Alpha-0.1.3`
- Release zip: `..\Zombie-Lane-Alpha-0.1.3.zip`

Options:

- `-AssetsRoot`: external assets root. The default is `C:\Proyectos\Zombie-Lane-Assets`.
- `-OutputRoot`: override the release folder. The default is `..\Zombie-Lane-Alpha-<version>`.
- `-Version`: release version. The default is `0.1.3`.
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

- The build compiles 66 classes.
- The compiled class set contains the `v0.1-alpha` classes, the launcher-validation inner class `PreLauncherDialog$FieldFilter`, and the `0.1.1` language classes (`LauncherLanguage`, `LauncherLanguage$Language`, `LauncherText`, `LanguageDialog`, `GameLanguage`). `PreLauncherDialog` and `ServerConnection` include the validator and localization changes; the intended release stubs are `zlport.dev.DevMode` and `zlport.dev.LauncherDevOptions`.
- Common files under `runtime`, `lib`, and `game-assets-ios` are byte-identical to `v0.1-alpha`.
- The headless smoke test exits 0 and does not print the `[dev]` banner.
- The `0.1.1+` language harness passes 38 checks (catalogue, state migration, byte-preserving settings update, missing-record behavior and localized validators).
- The launcher-default harness passes 8 checks with an empty profile list, and the validator harness passes 13 checks.
- The assembled folder and zip pass the release allowlist and sanitization checks.

The `0.1.2` package does not include `mission-backups` or three stale root files that existed only in the `0.1` output (`0`, `1.png`, `2.png`). Those files are not part of the canonical asset tree.

### Version 0.1.2

`0.1.2` includes the launcher validation fixes over `0.1.1`, local offline energy and cash cheats, a smaller launcher options screen, in-game music and sound-effect volume sliders, quieter startup diagnostics, a hidden console by default with an opt-in CMD setting, and a safe-defaults button. The loading-screen crash mentioned in inherited notes has not been reproduced by the maintainer and is not treated as a confirmed issue in this release.

### Version 0.1.3

With renewed opt-in and a per-tester token, the client sends one compressed session report to `https://reports.jorgelio.cl/reports/index.php`, including from LOCAL/offline gameplay. Uploads require internet, but neither gameplay nor report uploads require the game server. Uploads are capped at 1 MiB and pending reports are retried in bounded batches. The manual **Probar conexiones** button checks only the receiver with GET in LOCAL mode; in SERVER mode it checks the game server, public status page/data, and receiver. The official package and reports identify as `0.1.3`; `0.1.3c` was a test build, and `0.1.4` is the next official version. See [0.1.3 notes](docs/release-0.1.3.md) and the [receiver contract](docs/report-receiver.md). Deployment of the updated receiver and provisioning of each tester key are required before their uploads can succeed.

The `0.1.1` release introduced the local first-run language selector for English, French, Italian, German and Spanish. The choice is stored in `launcher.properties`, applied to the selected profile and synchronized back after the game exits; it never touches the server. Field validation and the always-enabled Play button are included.

See `docs/language.md`, `docs/release-0.1.2.md` and `docs/release-0.1.1.md` for the implementation and release details.

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
