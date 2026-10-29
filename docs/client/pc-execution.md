# PC port execution path

**Applies to:** the `0.1.2` release and its sources. Last reviewed: 2026-10-25.


Source-level findings are **[hypothesis: port]**: they describe the supplied 1.4.4 PC package and its Java sources, not a clean APK. The local profile's configuration and logs were inspected read-only; the logs document successful startup/rendering, but gameplay was not visually observed during this analysis.

For a broader explanation of the source layers, thread model, game initialization,
state machine, and platform adapters, see [Java port architecture and runtime](./port-architecture.md).

## Confirmed entrypoint

The release is run with `Run Zombie Lane Alpha.bat` at the release root, which starts the bundled runtime on the `lib/` and `client-overrides/` classpath with entrypoint `zlport.launcher.IosMain` and a default `game-assets-ios` bundle next to it. Running `zlport.launcher.IosMain` with `--help` reports the `zombie-lane-ios` options, a fixed 1136x640 logical screen. Those details match `../../src/launcher-ios/zlport/launcher/IosMain.java`, not the separate desktop `../../src/launcher/zlport/launcher/Main.java`.

The bundled Java 21 runtime can invoke the same entrypoint directly. From the port directory, run:

```powershell
.\runtime\bin\java.exe -cp "lib\*" zlport.launcher.IosMain
```

This direct invocation uses the packaged JARs and runtime; it is not a build from `src/`. The equivalent help-only check, which does not open the game window, is:

```powershell
.\runtime\bin\java.exe -cp "lib\*" zlport.launcher.IosMain --help
```

The direct help invocation and `ZombieLane.exe --help` were both verified. The native EXE's internal JVM arguments were not recovered, so the direct Java command is the reproducible launch path established from the available evidence. The launcher's JAR manifest has no `Main-Class`; use the classpath command rather than `java -jar`.

## Startup sequence

1. `IosMain` reads launcher options and, on a normal game start, opens the pre-game launcher (LOCAL/SERVER mode, profile selection, window and game options) before loading settings or the game. `--no-launcher` restores the older behavior (the plain profile-picker dialog). It then installs logging and chooses the app bundle directory. By default this is `game-assets-ios` next to the installation; `-Dzlport.ios.bundle=DIR` overrides it.
2. It installs `PcIosPlatform` for the iOS-specific platform APIs and an `IosDriver` for drawing/touch/audio, using a 1136x640 logical resolution.
3. It creates the shared `Launcher`, which initializes GLFW, creates the window and OpenGL 2.1 context, and starts the GL/render thread.
4. The launcher reflectively creates the sole accepted MIDlet, `com.dchoc.DCiDead`, then invokes its activity create/resume lifecycle methods. The game code is in `game-ios.jar`.
5. The shared loop forwards desktop input and presents frames; audio is supplied through the desktop OpenAL adapter when enabled.

The separate `zlport.launcher.Main` entrypoint in `launcher.jar` is not the one selected by the packaged EXE. It has different command-line options and configurable MIDlet/screen sizing, so do not use it as the normal 1.4.4 startup path.

### Local profile picker

On normal startup, `IosMain` shows a dialog listing numeric profile folders under `%APPDATA%\ZombieLane-1.4.4\players`. Choose **Iniciar** to run that profile, **Crear perfil nuevo** to create a clean profile and return to the picker, or **Cancelar** to exit without starting the game. Each profile stores its RMS records, settings, and logs under its own folder, so profiles do not share save files. A new profile directory contains only its display-name metadata; the game initializes its own records from defaults/resources. If the old flat-layout `files` directory contains RMS records and there are no profile folders yet, startup copies those records to a generated **Perfil existente** folder; the old files remain untouched.

The profile folder key is generated locally as a 15-digit number; it is not a game account ID. `--data-dir DIR` and `-Dzlport.dataDir=DIR` set the profile root and still select profiles below that root (inside the launcher screen, or the old picker dialog with `--no-launcher`). Use `--no-profile-picker` to bypass profile selection and open the specified data directory directly, or `--no-launcher` to skip the pre-game launcher entirely. The new pre-launcher flags (`--mode local|server`, `--server HOST:PORT`, `--user NAME`) are documented in [revival-implementations.md](./revival-implementations.md).

### Player action audit log

The PC runtime appends one JSON object per line to `actions.jsonl` in the active profile folder. It records player move, attack, construction, loot, and farming attempts, with a UTC timestamp, profile folder ID, action type, energy cost, neighbor-visit state, and target type/item/coordinates when available. Failed or rejected attempts are recorded too because logging happens as the game action starts. The file is kept separate from launcher and crash logs and is not rotated.

In this tree `Launcher.java` no longer installs `OfflineNetwork` implicitly, and the launcher installs `OfflineNetwork` explicitly and only when the player selects LOCAL mode, blocking game HTTP/HTTPS traffic; see [revival-implementations.md](./revival-implementations.md). With explicit alpha consent and a tester key, a separate diagnostic client can upload the session report, including a copy of the player-action log. LOCAL's manual connection check probes only the report receiver; it never probes the game server or public status page. As a client-side file, the action log is user-modifiable and is not authoritative proof for cheat prevention.

## Non-Java files in the port

Outside `game-assets-ios`, the packaged release is mainly a self-contained runtime and its dependencies:

- `ZombieLane.exe` is the Windows launcher wrapper. The actual Java classes and their dependencies are in `lib\*.jar`.
- `runtime\` is a bundled Windows x64 Java 21.0.8 runtime. Its `bin`, `lib`, and `conf` directories contain the JVM, tools, modules, and runtime configuration, so the user does not need to install Java separately.
- Native libraries in the LWJGL JARs provide Windows bindings for GLFW/window input, OpenGL, OpenAL audio, and LWJGL's JNI layer. They are supporting code for the desktop adapter, not game assets.
- The other runtime files (for example fonts, security policies, certificates, and legal notices) belong to the bundled Java runtime; they are not game content.

The non-Java files under `src\` are mostly packaging and dependency metadata rather than additional game logic:

- `META-INF\MANIFEST.MF` files describe the packaged JARs; the inspected manifests contain only `Manifest-Version`, not an executable main-class declaration.
- LWJGL `META-INF\INDEX.LIST` and `.sha1` files are JAR indexing and native-library checksum metadata. `META-INF\native-image\*.json` supplies reflection/JNI metadata for native-image tooling.
- `src\platform-1001\META-INF\zlport\game-core.properties` records the imported platform layer's class-filtering counts and source/rules hashes.
- `src\compat-android` holds selective Android/MIDLET-adjacent API shims; their files are plain stub/adapter sources and no provenance metadata (such as a `NOTICE`) ships with them.
- The `lwjgl-*-natives-windows` source directories include Windows DLLs; the corresponding LWJGL modules also carry native metadata for other operating systems. The packaged PC runtime itself is Windows-specific.

The `src\` tree has no Maven, Gradle, or Ant build descriptor, so these files do not by themselves provide a ready-to-run source build. See [port editability](./port-editability.md) for the source-layout and rebuild limitations.

## GameEngine lifecycle and persistence

The decompiled `GameEngine` is best read as a state machine, not as a static singleton with one-purpose helpers. Its lifecycle is organized around integer states: `0` for init/loading, `1` for area transition, `2`/`4`/`6` for active gameplay modes, `7`/`8` for intro presentation, and `9` for a save/travel checkpoint. The same code path reconfigures input blocking, music, HUD state, notifications, and the loading screen depending on the next state.

The actual save/resume semantics are also significant. `pauseGame()` triggers `setSaveGameState(true)` when the game is already loaded and no transaction is active; `saveGameState()` persists the profile and current area when the tutorial is complete, and `resumeGame()` re-establishes the app state by resetting the session, reactivating music, and restarting frame timers. This explains why the PC port can suspend and resume while still using a single `PlayerProfile` model and area-backed persistence.

The critical implication is that the Java port's persistence is not a separate game state object; it is a serialized projection of `PlayerProfile` plus the active area's data, stored through the J2ME record-store shims. When porting or debugging save semantics, treat this as a lifecycle boundary: the profile is persisted as bytes, the area data is persisted separately, and both are expected to be restored only after the correct state transition sequence.

## Assets and per-user data

- App resources are read from `game-assets-ios` beside the installation. `PcIosPlatform` opens bundle files first and falls back to its cache for resources and PNG textures.
- `%APPDATA%\ZombieLane-1.4.4` is the default profile root on Windows. Normal startup stores each profile's RMS files, settings, logs, preferences, and cache below `players\<id>`; `--data-dir DIR` or `-Dzlport.dataDir=DIR` changes the profile root but keeps the picker enabled.
- The resource bundle is separate from the Java source and JARs. Editing an image/audio/data resource means modifying or replacing the corresponding file in the bundle, not recompiling `game-ios.jar`.
- The inspected bundle has 1,264 files: 763 PNGs, 60 WAVs, 438 extensionless files, and three other root files (`Info.plist.properties`, `rms.sqlite`, and `intro_iphone.mov`) plus three stale root files (`0`, `1.png`, `2.png`) that the `0.1.1` release assembly drops; the canonical release bundle therefore has 1,261 files (761 PNGs, 437 extensionless). These counts do not establish which assets the game successfully loads.
- Image atlases are organized as `bundle_N/0` metadata and numbered PNG textures such as `bundle_N/1.png`. `DavinciUtilities.textureInit()` reads the bundle index, while texture IDs select the bundle, texture, and atlas region. Use the game metadata/resource IDs to identify an image before replacing a PNG; filenames alone are not descriptive.
- `Toolkit` loads packed resource archives such as `r1`, standalone numeric resources, and localization files such as `l0_0`. It now checks the shared missions directory (`<data-root>\missions`, or `-Dzlport.ios.missions`) for a pack file before the bundle, so an edited `r1`/`l0_0` applies to every profile. `ResourceIDs` maps many symbolic game names to numeric IDs, but those names should be cross-checked against actual references before treating them as authoritative.
- `Toolkit.getImage(int)` is an explicit `UnsupportedOperationException` stub in the 1.4.4 source. This is a known compatibility gap if execution reaches that API.

## Offline behavior and game overrides

- The source `Launcher.run()` does not install `OfflineNetwork`; that class remains dormant in the source tree. The packaged launcher JAR may still contain the older offline implementation. In this tree, LOCAL-mode launches install `OfflineNetwork` explicitly from `IosMain`, so game HTTP requests fail immediately without opening sockets. The separate diagnostic-report client can upload only with explicit alpha consent; this does not guarantee external services are available, and retired official hosts should not be relied upon.
- The active launcher reads `zombie-lane.properties` from the selected profile directory. It saves window position, size, maximized state, and fullscreen state on exit; the pre-game launcher ([revival-implementations.md](./revival-implementations.md)) also edits most of the other keys, and invalid values still fall back to defaults with a warning in the log. The launcher's own state (last mode, host, port, remembered user, last profile) lives in `launcher.properties` at the profile root.
- The 1.4.4 PC runtime exposes four launcher-managed game settings: `energia.recarga` (default 30 seconds, valid 1-3600; the "Infinite energy" checkbox writes `1`, and any other value already in the file is kept), `spawn.multiplicador` (default 2.0, valid 1.0-5.0, still a launcher field), `vizinho.intervalo` (default 5 minutes, valid 1-1440, no longer a launcher field — edit the file by hand; the launcher preserves the value), and the `0.1.2` offline cheat `trucos.billetes` (`true`/`false`, written only for local profiles and ignored for server launches). All of them are port-specific; the iPhone baselines cited in the settings writer are 300 seconds, 1.0, and 1440 minutes, and none establish original PC defaults.

The code references supporting these findings are [`Toolkit.java`](../../src/game-ios/com/dchoc/toolkit/Toolkit.java), [`DavinciUtilities.java`](../../src/game-ios/com/dchoc/spriteobject/DavinciUtilities.java), [`ServerAssetLoader.java`](../../src/game-ios/com/dchoc/spriteobject/ServerAssetLoader.java), [`OfflineNetwork.java`](../../src/launcher/zlport/launcher/OfflineNetwork.java), and [`Settings.java`](../../src/launcher/zlport/launcher/Settings.java).

## Save data and compatibility boundaries

- The bundled `game-assets-ios/rms.sqlite` is a SQLite database with one `record(id, value)` table and **zero rows** in this copy. The inspected PC MIDlet persistence path instead uses J2ME `RecordStore` in the selected data directory: `Toolkit.readRecord`/`writeRecord` open one named store, while `AppContext.getFileStreamPath` places its backing data under `<player-data-dir>\files\<name>_data\1` when launched through the profile picker (or `<data-dir>\files\<name>_data\1` with an explicit override). Do not assume that `rms.sqlite` contains the PC player's save.
- This `RecordStore` implementation stores only a single record (`1`) as a file; `listRecordStores()` returns null, and `getNumRecords()`/`setRecord()` are stubs. A new port or save-system improvement should first identify every caller and define migration/backup behavior rather than treating this as a complete J2ME RMS implementation.
- Android APIs in `compat-android.jar` are selective shims, not a full Android runtime. The provided SQLite layer accepts a narrow SQL/table shape and explicitly marks some `SQLiteOpenHelper` constructors as not ported. `AppContext` also reports Android package/version metadata from `1.0.01` even though this launcher starts the iOS 1.4.4 game. Audit any code that branches on Android package metadata or uses APIs beyond the implemented shim before extending the port.
- `launcher-ios` includes `IosClasspath`, a checker that detects multiple `DCiDead` classes or known 1.0.01 artifacts on the classpath. No call site was found in `src/`; consider wiring it into a future build/launch validation instead of assuming it currently protects execution.

### Player profile record format

`RMS_PROFILE` is a raw binary serialization, not a SQLite row or a text/config file. `GameEngine.savePlayerProfile()` serializes `PlayerProfile.save()` and writes the resulting byte array as the store's single record (`1`); loading reads that record and passes it to `PlayerProfile.load()`. The current profile writer allocates 1,024 bytes, and `DChocByteArray.getBytes()` returns the full buffer, so the serialized fields are followed by zero padding. Do not edit offsets based on one save without parsing the variable-length fields and version-dependent suffix.

The format is sequential and big-endian for byte, boolean, short, and int values. `writeUTF` uses Java modified UTF-8 with a big-endian unsigned-short byte length. The helper-encoded long is two big-endian ints (high word then low word). The current field order is:

1. **Profile header** (`Profile.save()`): UTF data-version string; five optional UTF strings, each prefixed by a boolean (`name`, `picture URL`, user ID, Facebook ID, Game Center ID); short level; byte active-avatar index; byte avatar count.
2. **Avatar entries** (`AvatarDescription.saveData()`), repeated by avatar count: short gender, short character ID, short equipped weapon ID (`-1` means none), short male-clothing count followed by that many short item IDs, then short female-clothing count and its short item IDs.
3. **Player progression** (`PlayerProfile.save()`): int session count, score, XP, energy maximum, energy, cash, coins, stone, and food, in that order.
4. **Inventory** (`Inventory.save()`): short entry count; each entry is a short item ID and int amount. Booster entries additionally contain int duration and a helper-encoded long use timestamp. Item IDs/types determine whether the booster fields are present.
5. **Player state**: helper-encoded long energy-charge timestamp; int accepted-neighbor count followed by that many UTF user IDs; UTF friend code; int energy-charge-time maximum.
6. **Versioned tail**: boolean apology-dialog flag, int resume count, boolean tutorial-complete flag, and boolean starter-pack-purchased flag. The loader gates some of these reads by the parsed profile version; when writing, the current code emits the full tail.

The optional profile strings and accepted-neighbor IDs can identify or refer to a player. Treat the record as private, redact those values when sharing a decoded dump, and back up the original before testing save-format changes. The profile version is also used for compatibility decisions, so preserve it and test older records when changing serialization.

### Inspecting RMS records as JSON

A read-only helper that decodes the known local layouts (leaving the source records untouched) and the output schema catalog are maintained in the separate revival repository. The catalog distinguishes shared definitions (mission/objective definitions and packed initial area/item data) from individual mutable progress. The `RMS_MISSIONS` store contains each player's mission/achievement progress; area RMS stores contain mutable scene state. Their serialization layouts are common, but their saved values are not universal or identical between players; installation preferences and prompt/experiment state are local to each profile.

The decoder handles profile fields and booster inventory entries, object payloads in current scenes, mission/achievement state against the extracted mission definitions, Rob's place profile/timers, A/B and rate-us records, and all eight settings bytes. Version-dependent mission parsing checks saved IDs against the definitions. `decodeStatus` is `complete` only when all fields are parsed and the remaining fixed-capacity tail is zero; non-zero tails and unknown/legacy formats are marked partial rather than discarded.

The combined JSON is a readable inspection view, not a lossless backup or game-import format. It contains player game state and is specific to the local save snapshot; do not publish it without reviewing it. The exporter never writes profile names, picture URLs, account IDs, social IDs, accepted-neighbor IDs, friend codes, or raw record bytes. It retains only presence/count information for those private fields.

## Runtime forensics for local issues

When a live behavior question cannot be answered from source reading alone, the local installation leaves enough read-only evidence to reconstruct a session sequence without instrumenting anything first. Everything below describes the launcher's data directory (`%APPDATA%\ZombieLane-1.4.4` for the installed launcher); these files contain private identifiers and personal gameplay data, so they are never committed, attached, or quoted beyond aggregate facts.

- **Session timestamps come from record mtimes.** Every store under `players/<id>/files/<STORE>_data/` keeps a single record named `1`. Because each save overwrites the record, the mtime of that record is the last save time of that store, which lets you group a day's activity into sessions (`logs/zombie-lane.log` and its rotated `.1`, `.2`, ... files confirm the session boundaries: each shows profile id, save folder, window size, `encerrando`, and frame counts).
- **Bootstrap saves are normal.** A profile that only booted (no gameplay) still writes a couple of record rotations a few seconds after start and on exit; the presence of a record therefore does not imply captured gameplay, and the *absence* of writes during a played session is itself evidence that mid-session saves did not reach the store.
- **The backup stores lag the primary ones.** `RMS_PROFILE_BACKUP_data/1` holds the previous live state (the rotation copies the outgoing live record before the next live write). Reading both gives you the last two committed states; compare their session counters and currency ints to see what a session changed.
- **Session reports capture the economy paths.** Each finished session writes a zip under `logs/informes/pendientes/` (moved to `enviados/` once uploaded). Inside: `informe.txt` (duration, frames, exit code), `acciones.jsonl` (the action audit log), `configuracion.txt`, `lanzador.txt`, `equipo.txt`, `sesion.json`, and copies of that session's logs. For money questions, `acciones.jsonl` is decisive: the tracker's `Spend PC` rows carry `paid_currency_delta` and `paid_currency_balance`, i.e. the wallet value the game computed at purchase time, and `Session Started` rows carry `session_count`, which tells you whether a session started from a loaded record or from defaults. A persistent row of `Recurso no encontrado: bundle_XXX` tracking errors during `DavinciUtilities.textureInit` is a known offline-mode artifact, unrelated to saves.
- **The profile progression is parseable with the project's own reader.** `RMS_PROFILE_data/1` can be decoded byte-for-byte by mirroring the save writers: `Profile.save()` header (UTF version `1_4_5`; five boolean-prefixed UTF strings; short level; byte active avatar; byte avatar count), then per avatar `AvatarDescription.saveData()`: short gender, short character id, short weapon id (a single read — the loader has no separate attachment field), short male-item count, the male item ids, short female-item count, the female item ids. Note the loader quirk: it reads gender/character/weapon, then `maleCount`, then reads one short before the male loop and reuses the last loop read as the female count, so a mirror parser must reproduce exactly that sequence instead of the writer's apparent order. After the avatars, `PlayerProfile.save()` writes int fields in order: session count, score, XP, energy max, energy, cash, coins, stone, food (then inventory, timers, neighbor list, friend code, and the version-gated tail). A scratch parser built on the game's `DChocByteArray.decode`-style reads (`com.dchoc.toolkit.DChocByteArray` from the compiled override set) prints those values; hand-decoding works, but the weapon field above is an easy single-byte-shift trap that silently corrupts every int after the avatar block.
- **A worked example of the method**: the tool's first cheat-cash iteration produced a report that the save was not reversible. The record forensics showed the final session started with the floor value already in the live record and ended saving only the spent difference, while the spend tracking showed purchases that did not move the visible balance. Combined with record mtimes, that narrowed the question to which writer put the floored value into the save, rather than how sessions were ordered.

## Local profile inspection

The directory `%APPDATA%\ZombieLane-1.4.4` was inspected read-only before the local-profile picker was added. Its `files/` records are a legacy flat-layout snapshot. On first startup, the picker now copies these records into a new `players/<id>/files/` folder without modifying the originals, so the existing save appears in the profile list.

- `files/` contains eleven one-record stores, including `RMS_ROBSPLACE` and the non-`RMS_` `settings` store. The full source-referenced record catalog lives in the separate revival repository. `GameEngine.loadPlayerProfile()` explicitly reads `RMS_PROFILE_BACKUP` as a fallback.
- The primary and backup profile/gamefield files have different hashes. The profile records differ in only one byte in the inspected snapshot (the saved session counter); the home scene and its backup contain the same number and types of objects but differ in some tile positions. The missions file and its backup are byte-identical. This is a point-in-time comparison, not proof that a record is valid.
- These files contain player/game state and should be backed up before experimenting with serialization, migration, or save logic. Do not copy them into source control or attach them to bug reports; personal IDs and raw record contents are not reproduced here.
- `zombie-lane.properties` has overrides rather than defaults: focus-loss pausing is off, zoom step is 1.06, log level is INFO, energy recharge is 10 seconds, spawn multiplier is 2.0, and neighbour interval is 5 minutes. Window geometry is persisted too and may change between runs; the game's logical resolution remains 1136x640.
- `preferences.properties` identifies version 1.4.4 and stores a persistent generated advertising identifier under two preference keys. The identifier value is intentionally omitted. Avoid committing/sharing this file; code generates a UUID once and persists it.
- `Library/Caches` contained 635 directories and no files in the inspected snapshot. This looks like cache directory scaffolding without cached asset payloads; combined with the offline HTTP adapter, remote assets may remain unavailable. This does not establish which bundles are needed for a particular game screen.
- The current log records a successful startup on this machine: Java 21.0.8, Intel Iris Xe reporting OpenGL 4.6, OpenAL Soft, creation/resume of `com.dchoc.DCiDead`, then 589 rendered frames over 16.0 seconds (36.9 FPS) before shutdown. This confirms startup and frame rendering ran, not that all game screens, controls, or save paths work.
- The current log has no `--gl-debug` activation message or GL error entries, so it is not a GL-debug test. It records three offline network attempts. An older rotated log contains 43,943 frames over 1,316.9 seconds (33.4 FPS) and OpenAL `alGenSources` warnings, including a report of 186 errors since the previous warning; audio should be retested separately if sound problems are reported.
- The `SEVERE` entry `android.AJ: MIDLet$onDestroy` appears on normal shutdown. The source calls `Log.e("AJ", "MIDLet$onDestroy")` unconditionally in the lifecycle callback, so this line alone is not evidence of a crash.
- The profile serialization is variable-length inside a fixed-capacity record. The decoder reports the zero-padded tail length, but inventory `amount` is generic and should not automatically be interpreted as a count of owned weapons.
- The data version `1_4_5` is not necessarily a game/app version mismatch: the bundle declares `CFBundleVersion=1.4.4` and separately `ServerAssetVersion=1_4_5`. Profile, home, circus, missions, and rate-us records inspected here begin with the `1_4_5` data version.
- Scene records contain object-type-specific fields and fixed-capacity padding. `RMS_ROBSPLACE` is not a scene: it serializes a profile followed by action count and three timers. The settings record contains eight bytes (language plus seven flags) before fixed-buffer padding. Mission records serialize mission/achievement state, zombie count, delayed setups, active spawns, non-reset objectives, and version-gated timestamps; mission definitions ([missions.md](./missions.md)) are required to follow their state-dependent fields. Current-source save/load code is the basis of these format descriptions.

The user's `preferences.properties` also contains a persistent generated advertising identifier. Its value was deliberately excluded from this note; treat the file as private local data.

Relevant implementation references: [`GameEngine.java`](../../src/game-ios/com/dchoc/idead/GameEngine.java), [`Profile.java`](../../src/game-ios/com/dchoc/idead/player/Profile.java), [`PlayerProfile.java`](../../src/game-ios/com/dchoc/idead/player/PlayerProfile.java), [`Inventory.java`](../../src/game-ios/com/dchoc/idead/player/Inventory.java), [`AvatarDescription.java`](../../src/game-ios/com/dchoc/idead/player/AvatarDescription.java), [`SceneLoader.java`](../../src/game-ios/com/dchoc/idead/isometric/SceneLoader.java), [`ObjectLoader.java`](../../src/game-ios/com/dchoc/idead/isometric/ObjectLoader.java), [`MissionManager.java`](../../src/game-ios/com/dchoc/idead/missions/MissionManager.java), [`ABTestManager.java`](../../src/game-ios/com/dchoc/idead/tracking/ABTestManager.java), [`DChocByteArray.java`](../../src/compat-ios/com/dchoc/toolkit/DChocByteArray.java), [`MIDlet.java`](../../src/j2me-desktop/javax/microedition/midlet/MIDlet.java), [`Log.java`](../../src/compat-android/android/util/Log.java), and [`PcIosPlatform.java`](../../src/runtime-ios/zlport/ios/runtime/PcIosPlatform.java).

## Controls, scaling, and diagnostics

- In the current GLFW input adapter, left mouse is converted to a single-pointer touch; the wheel sends zoom events and cursor movement sends hover events. The iOS `IosDriver` returns `false` for keyboard down/up, so keyboard gameplay/navigation is not implemented through that driver. Escape is converted to the legacy Android back key by `InputQueue`, but it still reaches the iOS driver, which rejects key events. These are code-path observations, not tested in-game behavior.
- F11 or Alt+Enter toggles fullscreen in the shared launcher. The game draws at a fixed 1136x640 logical resolution; resizing fits that aspect ratio in the framebuffer, so unused letterbox space is expected for a different aspect ratio and is excluded from mouse/touch coordinates.
- The separate, non-iOS `launcher.jar` path uses a different dynamic logical-size policy (minimum 1280x600), configurable MIDlet, and different help options. Keep it distinct when testing the actual 1.4.4 executable.
- For troubleshooting, `--gl-debug` enables per-call OpenGL error checks and `--no-audio` isolates audio startup. Setting `log.nivel=FINE` in `zombie-lane.properties` increases logging; logs and crash reports are written below the selected data directory's `logs` folder. The shared launcher reports calls to tracked not-ported stubs when it exits; an early crash may prevent the summary, so inspect the crash report/log as well.

The code references supporting these findings are [`RecordStore.java`](../../src/platform-1001/javax/microedition/rms/RecordStore.java), [`AppContext.java`](../../src/compat-android/zlport/android/AppContext.java), [`SQLiteDatabase.java`](../../src/compat-android/android/database/sqlite/SQLiteDatabase.java), [`IosClasspath.java`](../../src/launcher-ios/zlport/launcher/IosClasspath.java), [`InputQueue.java`](../../src/launcher/zlport/launcher/InputQueue.java), [`IosDriver.java`](../../src/runtime-ios/zlport/ios/runtime/IosDriver.java), and [`Presentation.java`](../../src/launcher/zlport/launcher/Presentation.java).

## OpenGL debug mode

For a temporary diagnostic run, launch the actual 1.4.4 entrypoint from PowerShell in the port install directory:

```powershell
.\runtime\bin\java.exe -cp "lib\*" zlport.launcher.IosMain --gl-debug
```

You can also pass `--gl-debug` to `ZombieLane.exe`. The equivalent Java system-property form is:

```powershell
.\runtime\bin\java.exe -Dzlport.gl.debug=true -cp "lib\*" zlport.launcher.IosMain
```

The property goes before `-cp` and the main class. With either form, the launcher logs `GL en modo de depuración: glGetError después de cada llamada` when the GL context is ready. To keep this run's log separate, add `--data-dir "$env:TEMP\ZombieLane-gl-debug"`; otherwise the default Windows log is `%APPDATA%\ZombieLane-1.4.4\logs\zombie-lane.log`. Each log record is flushed to disk, and numbered prior logs are rotated on startup.

The wrapper checks GL calls made through the game's `GL11`/`GL11Ext` compatibility interface, after each call on the GL thread. A detected error is logged with its GL function and arguments. The first 20 errors include a Java stack trace; later errors still log their function/arguments. Recognized errors include `GL_INVALID_ENUM`, `GL_INVALID_VALUE`, `GL_INVALID_OPERATION`, stack overflow/underflow, and out-of-memory. The checker drains up to eight GL error flags after each wrapped call.

Scope matters: this does **not** wrap every OpenGL call in the process. In particular, the launcher's own framebuffer creation and presentation use direct LWJGL calls, so this mode is mainly useful for locating invalid operations in game rendering/resource code. It is not a GPU performance benchmark, does not explain driver/device loss, and may add substantial overhead because it queries GL after each wrapped call. Enable it only for a repro, note what action triggers the error, then rerun without it to compare normal behavior.

## Where to make changes

| Change | Source area |
|---|---|
| Game rules, menus, characters, progression | `src/game-ios/` |
| iOS-to-PC behavior, bundle reads, preferences, caches | `src/runtime-ios/` and `src/compat-ios/` |
| Window, input mapping, render loop, desktop startup | `src/launcher/` and `src/launcher-ios/` |
| J2ME and Android compatibility APIs | `src/j2me-desktop/` and `src/compat-android/` |
| Textures, sounds, packed resources, other game content | `game-assets-ios/` (supplied externally; see [game-assets](./game-assets.md)) |
| Save format and compatibility behavior | `src/platform-1001/`, `src/compat-android/`, `src/runtime-ios/` (trace the actual call path before editing) |

There is no Maven/Gradle/Ant build project in the supplied tree. Player-release changes are built with `tools\build-client.ps1`, which compiles the class allowlist against `src` and `src-release` and ships the compiled classes in `client-overrides/`; rebuilding or replacing JARs is not part of this tree's flow. Local logs confirm startup and frame rendering, and the maintainer's sessions report functioning gameplay in most areas; dated session notes are kept in the revival repository. [observed: port] Specific broken or missing items should be itemized there.
