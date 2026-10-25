# Java Port Architecture and Runtime

**Applies to:** the `0.1.2` source tree. Last reviewed: 2026-10-25.


This document records the essential architecture visible in the supplied Java
port sources. These are **[hypothesis: port]** findings: they describe this
port and its packaged 1.4.4 runtime, not a clean original APK or independently
observed gameplay. The intended trust labels and provenance are described in
[sources.md](./sources.md).

For the reproducible executable entrypoint, command-line options, and package
layout, see [pc-execution.md](./pc-execution.md). For resource files, atlases,
and sprite decoding, see [game-assets.md](./game-assets.md).

## Source layers

The source tree is split by responsibility rather than by one unified
application project:

| Source area | Responsibility in the PC port |
| --- | --- |
| `src/game-ios/` | Game code imported from the iOS build: `DCiDead`, game flow, `GameEngine`, areas, scenes, HUD, items, missions, sprites, and toolkit code |
| `src/launcher-ios/` | Active 1.4.4 entrypoint, launcher configuration, and installation of the PC-backed iOS platform |
| `src/launcher/` | Shared GLFW window, UI/event loop, OpenGL thread, framebuffer presentation, input mapping, settings, logging, and offline network handler |
| `src/runtime-ios/` | Implementations of selected iOS-facing platform services and the MIDP-compatible GL renderer for this port |
| `src/compat-ios/` | iOS API contracts and compatibility types used by the game |
| `src/j2me-desktop/` | J2ME/MIDP interfaces and shared desktop runtime types |
| `src/compat-android/` | Android API compatibility classes and PC app context |
| `src/platform-1001/` | Imported J2ME RMS and platform compatibility classes used by the game |
| `src/gles-desktop/`, `src/audio-desktop/` | Desktop OpenGL and OpenAL adapters |
| `lib/` (not in `src/`) | External LWJGL 3.4.3 Java APIs, native bindings, and port JARs supplied at release time |

The runnable package uses the iOS entrypoint, not the separate generic
`zlport.launcher.Main`. There is no Maven, Gradle, or Ant build descriptor in
the supplied source tree, so these directories are not by themselves a
reproducible source build.

## Startup and service installation

The effective startup sequence is:

1. `zlport.launcher.IosMain` parses launcher options and, unless a direct
   `--data-dir`/`-Dzlport.dataDir` override is supplied, prompts for a local
   profile. It loads settings from that profile, chooses the app bundle, and
   installs logging.
2. It installs `PcIosPlatform` into the static `Ios` service registry and
   installs `IosDriver` into the shared J2ME `Platform`.
3. The shared `Launcher` installs the offline URL handler, initializes GLFW,
   creates the window, and starts the GL thread.
4. The launcher reflectively constructs the configured MIDlet. The active
   entrypoint only accepts `com.dchoc.DCiDead`; the launcher invokes its
   create/resume lifecycle callbacks.
5. `DChocMIDlet.startApp()` makes the display visible and schedules its
   runnable. That runnable initializes the toolkit, emits the app-start event,
   and then continues to schedule itself through `Display.callSerially()`.
6. `DCiDead` initializes platform-independent game rendering and toolkit
   services, then constructs its `Game` flow processor. The flow processor
   creates `GameEngine`, which performs the ordered game subsystem and area
   initialization steps.

`Ios.platform()` and `Platform.surface()` deliberately fail if used before
installation. The launcher must therefore install both services before
constructing the MIDlet.

## Threads, update scheduling, and drawing

The main GLFW/UI thread waits for window events, dispatches Android-style
message-queue work, and delivers converted desktop input. The dedicated
`GLThread` owns the current OpenGL context, initializes the renderer, processes
render requests, and presents the game's logical framebuffer to the window.

The MIDlet does not own an independent fixed-step game thread. The shared
`Display` queues `Runnable`s submitted by `callSerially()`. On a requested GL
frame, `IosRenderer.onDrawFrame()` binds the current GL context, flushes queued
multitouch events, paints when repaint was requested, dispatches queued
single-pointer callbacks, and runs one serial callback. The MIDlet runnable
performs its game update and schedules itself again unless paused or exiting.
This couples normal logic updates to renderer work and repaint requests.

`DChocMIDlet` computes elapsed wall-clock time for logic updates and maintains
a rolling delta-time history. Game animation timing therefore receives
measured elapsed time, not a fixed simulation step. The renderer paces frames
near 60 Hz when the monitor refresh rate does not provide a usable rate at or
below 61 Hz; GLFW swap interval is also enabled.

`DCiDead.doDraw()` delegates to the current `FlowProcessor`. The game flow
draws through `Toolkit`/`Graphics` abstractions, whose iOS implementation
eventually issues OpenGL calls. `GlThread` renders into a fixed-size
off-screen framebuffer and blits it into an aspect-fit rectangle in the
resizable window. For the active entrypoint, the logical dimensions are
1136x640; resizing changes the presentation rectangle, not the logical game
layout.

Rendering and texture upload require the GL context on the GL thread.
`IosRenderer.currentGl()` is thread-local and throws if code requests a
graphics context on a thread that has not bound it.

## Desktop input mapping

`InputQueue` translates GLFW input into logical game coordinates, excluding
letterbox bars:

- Left mouse press, move, and release become a single-pointer touch sequence.
- Cursor movement while not pressed becomes hover; leaving the game frame
  ends hover.
- Mouse-wheel movement becomes zoom at the cursor position.
- Escape is translated to legacy Android key code 4.
- F11 and Alt+Enter are handled by the launcher for fullscreen and are not
  sent as ordinary game input.

The queue forwards events through `zlport.j2me.Input` to `IosDriver`. The
driver's keyboard down/up methods return `false`, so the mapped Escape event
does not become functional game keyboard input through this path. The renderer
accepts one pointer for the ordinary press/drag/release queue; it separately
passes touch samples to the MIDlet's gesture callback. Whether that path
produces useful gameplay gestures depends on the game handlers and must not be
assumed from the adapter alone.

`IosGameScene.touchable()` adds a further guard for a desktop-specific
scene-interaction path: the flow must be active, the game engine must be in an
eligible state, the activity must not be loading, the scene must be active,
and no HUD window may be open. UI controls and the isometric scene therefore
do not receive identical touch routing.

## Game initialization and state machine

`DCiDead` performs initialization incrementally across update calls. It first
initializes OpenGL helpers, toolkit wrappers, settings, activity/loading UI,
graphics metadata, and tracking. Subsequent loading steps initialize fonts,
the game flow, and payment-related setup. Graphics initialization calls
`DavinciUtilities.textureInit()` and `ServerAssetLoader.init()`. Several
loading progress values are placeholders rather than measurements of every
subsystem's actual work.

`Game.init()` constructs `GameEngine`; `GameEngine.load(step)` initializes
subsystems in ordered steps:

- device tuning, social wrappers, and shared animation state;
- items and player profile, AB-test data, and the initial session request;
- scene, spawning, mission, notification, event, and action managers;
- HUD and persistent area objects;
- the home area and its data, followed by tutorial and dialog data.

The engine's principal states are:

| State | Role observed in the code |
| --- | --- |
| 0 | Ordered initialization/loading |
| 1 | Loading or switching to another area |
| 2 | Home-area gameplay |
| 4 | Neighbor-area gameplay |
| 6 | Adventure-area gameplay |
| 7 | Intro movie presentation |
| 8 | Intro/loading-screen transition |
| 9 | Save/travel checkpoint that coordinates activity UI and area readiness |

States 3 and 5 are also present in update/state code, but their independent
user-facing meanings are not established here. Area transitions load and enter
the next area, exit the current area, then select the active state from the
area type. A failed non-home area load schedules a return to home.

During active gameplay, `GameEngine.logicUpdate()` advances touch, network
servlet, profile, notification, animation, mission, event, spawn, scene,
tutorial, and HUD managers according to the current state and activity
overlay. Drawing is separate: the engine draws the isometric scene, HUD, and
post-draw overlays for eligible states. This conditional ordering is
important when changing game systems; not every manager runs in every state.

## Areas, world objects, and save/load

`GameEngine` owns the known area instances (home, circus, downtown, cure,
crash site, robbery, and neighbor) and an `IsometricScene`. Area classes own
their persistence identifiers and load/enter/exit behavior. `SceneLoader`
serializes the current isometric scene as a versioned payload containing
object count, crash-related metadata, typed object records, and a scene
timestamp. The reader supports legacy/uncompressed data as well as versioned
compressed records and dispatches object decoding to `ObjectLoader` or the
legacy loader.

The game persists named records through the J2ME `RecordStore` API. In this
port, `RecordStore` maps each store name to one file at
`<app-data>/files/<store>_data/1`. It is a compatibility shim, not a full J2ME
record-store implementation: it returns only one record, `getNumRecords()`
returns zero, `listRecordStores()` returns `null`, and `setRecord()` is empty.
`addRecord()` overwrites the backing file rather than implementing a general
multi-record store.

The game-level save path stores the player profile separately from area data.
On profile load it tries `RMS_PROFILE`, then `RMS_PROFILE_BACKUP`; a successful
primary load refreshes the backup. When pausing a loaded game outside an
active transaction, `GameEngine.pauseGame()` requests a save. Save processing
is gated by tutorial completion and current area/state; area data is not
unconditionally written for every state. See [pc-execution.md](./pc-execution.md)
for inspected package-specific save-record details.

The per-user app-data directory is separate from `game-assets-ios`. The PC
platform stores iOS preferences and its cache under that data directory;
Android-style files, databases, and J2ME record stores use compatibility
paths rooted there as well. Moving or backing up saves therefore requires
preserving user data, not just the installed asset bundle.

## Data-driven gameplay systems

The game combines compiled behavior with binary definition tables read
through `Toolkit`. The resource IDs below are the ones requested by these
readers; they are source-level evidence, not an independently verified
original-game schema.

| System | Data sources observed | Runtime role |
| --- | --- | --- |
| Tuning and progression | `Tuner` reads resources including `80410`, `80432`, `80425`, `80422`, `80433`, `80431`, and `80413`-`80415` | Configures global gameplay values, area setups, level definitions, and combo definitions |
| Item catalog | `ItemManager` reads item-family resources `80459`, `80457`, `80461`, `80462`, `80467`, `80455`, `80465`, `80453`, `80463`, `80456`, `80460`, `80458`, `80464`, `80454`, and collections from `80441` | Groups catalog definitions by item type; inventories store item IDs and quantities and resolve definitions through this manager |
| Spawning and locations | `SpawningManager` reads spawn definitions from `80485` and location definitions from `80486` | Activates eligible spawn definitions and places their configured objects into the current scene |
| Missions and objectives | `MissionManager` reads mission definitions (`80468`), objectives (`80475`), setups (`80470`), two mission-event tables (`80472`, `80473`), objective events (`80476`), achievement definitions (`80435`), and achievement objectives (`80436`) | Connects game counters/events to objective progress, mission activation, rewards, expiration, and HUD state |

`Tuner` is a central lookup layer for values such as area setup, level
requirements, combo rewards, and gameplay tuning constants. `PlayerProfile`
uses it for level definitions, while area objects obtain their setup data
from it. This makes the binary tables and their ID mapping part of the game's
behavior; changing only a Java constant may not change a value loaded from a
resource.

The scene is a tile-based isometric world. `IsometricScene.init()` allocates a
50-by-50 tile grid and nine typed object collections, plus a camera,
pathfinder, spatial quadtree, object pool, floor, and transient feedback and
loot-reward state. Runtime code uses different collections for object
classes; spawn logic, for example, inspects the zombie and loot collections
by their internal collection indices. Those indices are implementation
details, not stable serialized type IDs.

Area records bridge persistent scene data and that live world. A common
`Area.loadData()` path tries its primary stored record. If the scene record
fails to load, it attempts the area's backup and then falls back to that
area's bundled default scene resource. Areas loaded with missions also validate
the mission record. A failed mission load no longer discards a scene that
loaded successfully: the area keeps the server gamefield, writes both raw
records to the RMS backups, and disables saving until the mission definitions
are available again. Saving serializes the current scene and mission data into
named records. `SceneLoader` reconstructs the world by decoding each object's
type and payload, then rebuilds walkable-tile state. This is distinct from the
sprite/texture resource path described in
[game-assets.md](./game-assets.md).


Spawn definitions include subtype, spawn ID, interval, amount range, health,
maximum population percentage, location, reward type/list, level range,
mission prerequisites, and persistence/guarding flags. The manager
reevaluates whether each definition is active; eligibility depends on level
and mission state. A spawn executes only when its timer is due and a matching
location exists in the current area. The manager stops after the first
successful due spawn in one update and skips its normal update while at a
neighbor's place. The optional PC spawn multiplier affects periodic zombie
definitions only and is applied when the configured factor is greater than
1.

Mission definitions are linked at load time to objective definitions,
mission setups, event mappings, and achievement objectives. During updates,
active missions advance timers and evaluate completion or expiration; a
completed mission may collect rewards and run follow-up events. The manager
then activates eligible missions based on their locked, level, and
prerequisite state and updates HUD mission entries. Mission progress is
therefore driven by emitted game events and counters, not solely by the HUD.

The update gate matters: mission, event, spawn, and scene simulation run only
when the scene is active and the application is not in its loading activity
state. The top-level area/game state imposes additional conditions. When
investigating a timer, spawn, mission, or object that appears frozen, trace
both its local update method and the caller's state/HUD guards.

## Platform adapters and deliberate limitations

`IosPlatform` is the game-facing boundary for iOS behavior. `PcIosPlatform`
implements the PC behavior for resource access, preferences, device/version
queries, cache operations, and texture upload. Other methods represent
services that existed on iOS—ads, notifications, Game Center, movie playback,
social integrations, and related UI—and are ignored, stubbed, or return
unavailable values where a PC implementation is not supplied. Individual
methods must be checked before relying on them; a method signature does not
mean the original iOS feature is operational on PC.

`IosDriver` adapts MIDP renderer, touch, zoom, hover, and media-player
interfaces. `IosAudio` loads WAV data from the resource platform, caches PCM
buffers for the installed OpenAL output, and creates effect/music voices.
Music locators ending in `.mp3` are mapped to `.wav` by the port. Missing or
unreadable audio is logged and the sound is skipped. Disabling audio or
failing to open OpenAL does not prevent the launcher from attempting to run
the game.

The source `Launcher.run()` does not install `OfflineNetwork`; that class remains
dormant in the source tree. The packaged `launcher.jar` may contain the earlier
offline implementation. In LOCAL mode, `IosMain` installs `OfflineNetwork`
explicitly; this does not establish that historical external services are
available or safe to use, and in local mode the client reaches only a
 configured server if one runs.

The port has compatibility gaps beyond whole platform features. Some methods
are explicit `UnsupportedOperationException` stubs, some are tracked by
`NotPorted`, and some return a neutral value. Inspect the exact call path when
a feature appears inert; do not treat a placeholder return as proof that the
original behavior was reproduced. The launcher logs tracked not-ported calls
on shutdown, while an early crash may require inspecting the log and crash
report instead.

## Evidence and validation boundaries

This architecture guide is based on source inspection, including
`IosMain`, `Launcher`, `GlThread`, `InputQueue`, `IosRenderer`, `DChocMIDlet`,
`DCiDead`, `GameEngine`, `SceneLoader`, and the platform adapters. It does not
claim that all game screens, controls, transitions, or persistence scenarios
were exercised visually. Gameplay data paths were traced into the tuning,
item, spawning, mission, profile, area, and scene loaders. Package-specific
successful startup/rendering evidence and known validation gaps are listed in
[pc-execution.md](./pc-execution.md).

Before changing or reimplementing behavior, verify it against the actual
callers and the packaged runtime where possible. In particular:

- A source-level callback path establishes that code can receive an event,
  not that the event produces the expected user experience.
- A serializer reader establishes its accepted field layout, not the
  complete authoring schema or validity constraints.
- iOS-API-shaped methods establish an adapter contract, not parity with iOS.
- Data observed in this port remains a port hypothesis unless independently
  checked against a clean APK or observed in the original game.

## Key source files

- [IosMain.java](../../src/launcher-ios/zlport/launcher/IosMain.java),
  [Launcher.java](../../src/launcher/zlport/launcher/Launcher.java),
  [GlThread.java](../../src/launcher/zlport/launcher/GlThread.java), and
  [InputQueue.java](../../src/launcher/zlport/launcher/InputQueue.java): startup,
  window, render thread, and desktop input.
- [DChocMIDlet.java](../../src/game-ios/com/dchoc/toolkit/DChocMIDlet.java),
  [IosRenderer.java](../../src/runtime-ios/javax/microedition/lcdui/IosRenderer.java),
  and [IosDriver.java](../../src/runtime-ios/zlport/ios/runtime/IosDriver.java):
  MIDlet scheduling, display callbacks, and platform driver.
- [DCiDead.java](../../src/game-ios/com/dchoc/DCiDead.java),
  [Game.java](../../src/game-ios/com/dchoc/idead/Game.java), and
  [GameEngine.java](../../src/game-ios/com/dchoc/idead/GameEngine.java):
  application boot, flow, game state, and subsystem updates.
- [IsometricScene.java](../../src/game-ios/com/dchoc/idead/isometric/IsometricScene.java),
  [Area.java](../../src/game-ios/com/dchoc/idead/areas/Area.java), and
  [SceneLoader.java](../../src/game-ios/com/dchoc/idead/isometric/SceneLoader.java):
  world model, area lifecycle, and scene persistence.
- [Tuner.java](../../src/game-ios/com/dchoc/idead/tuner/Tuner.java),
  [ItemManager.java](../../src/game-ios/com/dchoc/idead/items/ItemManager.java),
  [SpawningManager.java](../../src/game-ios/com/dchoc/idead/spawning/SpawningManager.java),
  and [MissionManager.java](../../src/game-ios/com/dchoc/idead/missions/MissionManager.java):
  definition tables and data-driven game systems.
- [PlayerProfile.java](../../src/game-ios/com/dchoc/idead/player/PlayerProfile.java),
  [RecordStore.java](../../src/platform-1001/javax/microedition/rms/RecordStore.java),
  and [PcIosPlatform.java](../../src/runtime-ios/zlport/ios/runtime/PcIosPlatform.java):
  profile state, record storage, per-user data, and platform services.
