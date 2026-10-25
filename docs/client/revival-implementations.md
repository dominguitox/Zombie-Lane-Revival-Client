# Revival implementation notes

**Applies to:** changes present in the `0.1.2` tree; server-side counterparts are marked "separate repository". Last updated: 2026-10-25.


This document records concrete changes made to the supplied PC port for the
revival. It describes this repository's source and the player build it
produces; it
does not claim the changes are part of an upstream Zombie Lane release.

## Pre-game launcher

### Goal

Before the game window opens, the player gets one Swing screen where they
choose:

- **Mode LOCAL** — no network connection is attempted at all, neither to a
  local server nor to any external host. No save review, no anticheat.
  Other local profiles keep being listed as neighbors exactly as
  `LocalProfiles` does today. [implemented]
- **Mode SERVER** — host, port, user name and password fields, a
  **Probar conexión** button that performs a single `GET /health` request
  (3 second connect/read timeout) against exactly the typed host and shows a
  clear outcome (ok / no answer / invalid host or port). No other call is
  made. [implemented]
- **Window and game options** — fullscreen, maximized, window size (the
  1136x640 logical resolution never changes; only the OS window), pause on
  focus loss, audio on/off (`--no-audio`), log level, and the zombie spawn
  multiplier field (`spawn.multiplicador`, 1.0–5.0, default 2.0). Two
  checkboxes replace the old per-value fields: "Infinite energy"
  (`energia.recarga=1`; an already-tuned value in the file is preserved when
  the box is unchecked) and "Infinite cash" (`trucos.billetes`, disabled in
  server mode — the cheat never reaches a server session). The cash helper
  keeps an honest book: the save always stores the real balance (earnings
  in, spends out, min 0), so unchecking the cheat reverses it instead of
  leaving a 9,999,999 wallet behind. `roda.passo`
  (wheel zoom step) and `vizinho.intervalo` (neighbour minutes) lost their
  launcher fields and remain file-only keys. Invalid or unmatched values
  still fall back to defaults with a warning in the log; the launcher
  validates what it owns. Diagnostics (`--gl-debug` equivalent, `FINE`
  logging) live in a collapsible "Avanzado" section. [implemented]
- **Dev options** — the dev shared-missions directory field is compiled out of
  the player edition. The real UI builder lives in the separate revival
  repository's dev tree (`src-dev`; see
  [editions.md](./editions.md)); this repository ships the
  no-op
  [`src-release`](../../src-release/zlport/dev/LauncherDevOptions.java)
  body that `tools/build-client.ps1` compiles, so the player binary contains
  no dev missions field at all, not a hidden one. [implemented]

### Files changed

- [`PreLauncherDialog.java`](../../src/launcher-ios/zlport/launcher/PreLauncherDialog.java)
  is the dialog itself. The validation of every field
  (`validateFields`, `optionValues`) and the connection semantics
  (`ServerConnection`) are plain logic without Swing, so smoke tests run
  headless. Player-visible strings are localized (see
  [the language notes](../language.md)). [implemented]
- [`PreLauncherState.java`](../../src/launcher-ios/zlport/launcher/PreLauncherState.java)
  stores the launcher's own state in `launcher.properties` at the profile
  root: `mode`, `server.host`, `server.port`, `server.user` (only when
  "Recordar usuario" is checked), `server.remember-user`, `profile.last` and,
  in the dev edition, `dev.missions-directory`. Keys are English; invalid saved
  values fall back to defaults. Game options are never duplicated here —
   they are written to each profile's `zombie-lane.properties`, which the game
   already reads (see `Settings.saveLauncherOptions` in
   [`Settings.java`](../../src/launcher/zlport/launcher/Settings.java)). The file
  is new, so there are no legacy keys to keep compatible; the Portuguese
  `zombie-lane.properties` keys stay as they are for post-launch
  compatibility. [implemented]
- [`ServerConnection.java`](../../src/launcher-ios/zlport/launcher/ServerConnection.java)
  composes the game URL (`http://HOST:PORT/dead/`), validates host/port/user
  and performs the only network operation of the launcher: the `/health`
  probe. `parseEndpoint` backs the `--server HOST:PORT` flag.
  [implemented]
- [`PendingCredentials.java`](../../src/launcher-ios/zlport/launcher/PendingCredentials.java)
  stages the typed password in memory for the current JVM run only.
  See the password decision below. [implemented]
- [`IosMain.java`](../../src/launcher-ios/zlport/launcher/IosMain.java) wires the
  dialog between argument parsing and runtime installation: parses the new
  flags, seeds the dialog (flags &gt; saved state &gt; `-Dzlport.server.url`
  &gt; defaults 127.0.0.1:6767), applies mode/connection, then continues with
  the existing profile/settings/runtime flow. [implemented]
- [`LocalProfiles.java`](../../src/launcher-ios/zlport/launcher/LocalProfiles.java)
  gained launcher helpers on top of its existing logic: `profiles` lists the
  valid profiles without any dialog, and `createNew(dataRoot, name)` creates a
  profile non-interactively from an explicit name (the launcher asks for the
  name in its own screen). Discovery, legacy migration and the standalone
  picker dialog are untouched. [implemented]
- [`LauncherDevOptions.java`](../../src-release/zlport/dev/LauncherDevOptions.java)
  compiles the no-op dev-options body into the player edition; the real UI
  builder lives in the dev tree of the separate revival repository, so the
  player binary has no dev field. See
  [editions.md](./editions.md). [implemented]
- The release launcher (`Run Zombie Lane Alpha.bat`, assembled by
  `tools/build-client.ps1`) starts `IosMain` with the compiled
  `client-overrides` classes ahead of the externally supplied `lib` JARs.
  `-Dzlport.server.url` seeds the connection fields before any
  `launcher.properties` exists (first run) and applies directly when running
  with `--no-launcher`; once the launcher saves the state, the state wins.
  Extra arguments are forwarded (`--no-launcher`, `--mode`,
  `--no-profile-picker` keep working). [implemented]

### LOCAL mode: no network

In LOCAL mode the launcher installs `zlport.launcher.OfflineNetwork` before
the runtime starts. This choice is deliberate: the guard is a
`URLStreamHandlerFactory` that answers every `http`/`https` attempt with an
immediate `ConnectException` without opening a socket. Covering all servlet,
asset and session requests (current and future) needs zero per-servlet
changes and cannot silently regress into partial connectivity; a per-servlet
fail-fast flag inside the game code would have to chase every request path.
The install therefore happens explicitly and only for LOCAL mode, so SERVER
mode still reaches the configured server. The revival repository's smoke
suite starts a fake HTTP server on the local machine, selects LOCAL mode, and
proves the server receives zero connections while every blocked attempt is
still recorded. [implemented], [smoke-tested]

### SERVER mode: connection and identity

`zlport.server.url` is composed as `http://HOST:PORT/dead/` (the shape the
client already expects). The account id decision is below; nothing besides
the typed host is contacted, and the probe is the only launcher request.
Before starting the game in SERVER mode the launcher runs that same probe
and **refuses to launch when the server does not answer**: without a
reachable server there is no online play (client-side validation of the
typed user and password happens first, without network). The same gate covers
`--no-launcher` scripted SERVER launches: they print the message and exit 1
instead of silently falling back to offline play. Verified live: a dead port
exits 1 in about 1 s, and a live server completes the handshake. [implemented],
[smoke-tested]

### Account identity rule

In SERVER mode the launcher authenticates against `/auth/register` or
`/auth/login` and uses the **server-issued numeric id** as
`zlport.account.id`; the local profile folder remains save storage. The game
handshake (`CreateNewSession`) re-sends that id as `username` with the password
staged in memory, and the server resolves it by numeric id (or by
case-insensitive user name). The typed user is only the fallback identity for
scripted launches without a dialog; an explicit `-Dzlport.account.id` wins.
Local profiles are not
injected as neighbors in SERVER mode — the roster comes from the server's
account list. In LOCAL mode nothing talks to a server, so the 15-digit numeric
profile folder id stays as today and sibling profiles are injected as local
neighbors. `IosMain.accountIdFor` implements the fallback. [implemented] A
packaged client completed a live `CreateNewSession` 200 against the revival
server. [smoke-tested]

### Password handling (client side)

The launcher enforces a 4–128 character password length, authenticates
against `/auth/register` and `/auth/login`, and sends the password in the form
body over plain HTTP: loopback or trusted LAN only, never a real password on
an untrusted network. The password is staged only in memory
(`PendingCredentials`), never in launcher properties, system properties, logs
or the command line; "Recordar usuario" remembers only the user name.
Storage, hashing policy and account limits are implemented in the revival
server (separate repository) and are not documented here.

### CLI flags and compatibility

- `--mode local|server` preselects the mode (`servidor` accepted as a value).
- `--server HOST:PORT` preselects the connection (validated; a pasted game
  URL is tolerated).
- `--user NAME` preselects the user name (validated).
- `--no-launcher` skips the pre-game launcher entirely: the previous flow
  (old profile picker dialog, unless `--no-profile-picker`) runs, and the
  mode/connection are resolved from flags, saved state and
  `-Dzlport.server.url` as the launcher would have done.
- `--no-profile-picker` and `--data-dir DIR` keep their previous behavior.
  Without `--no-launcher`, profile selection happens inside the launcher
  screen instead of the old dialog, and the legacy flat-save migration still
  runs. [implemented]

### Validation state

The launcher's validation, connection and profile behavior is covered by the
smoke tests of the separate revival repository, which drove a live server,
launched the packaged runtime in LOCAL and SERVER modes, and built the player
edition. Earlier claims in this section are historical implementation notes.
[implemented] [smoke-tested]

Launcher UI layout, in-game visible play and live custom-mission appearance
still require a real visible session. [not exercised in a live session]

## Local player profile picker

### Files changed

- [`IosMain.java`](../../src/launcher-ios/zlport/launcher/IosMain.java) is the
  active Java entry point used by the player release (`Run Zombie Lane
  Alpha.bat`).
- [`LocalProfiles.java`](../../src/launcher-ios/zlport/launcher/LocalProfiles.java)
  implements profile discovery, selection, creation, and one-time legacy-save
  copying. It is a new source file in the `launcher-ios` source tree.
- The player release ships these classes compiled from `src/launcher-ios` in
  the build's `client-overrides` directory, loaded ahead of the externally
  supplied `lib` JARs (see
  [editions.md](./editions.md)); the game runtime, `lib` and
  `game-assets-ios` trees are supplied externally and are not versioned in
  this repository.

No source changes were made to the game's `game-ios` classes or to the RMS
record-store implementation for this feature.

### Startup behavior in `IosMain`

`IosMain.main()` now:

1. Parses the original options after removing the profile-only
   `--no-profile-picker` switch, so that switch is not passed to the general
   launcher parser.
2. Uses the parsed data directory as the profile **root** and, unless
   `--no-profile-picker` was supplied, calls `LocalProfiles.choose(root)`
   before loading profile settings, installing the game runtime, or launching
   the MIDlet.
3. Stops startup if the dialog is cancelled. On selection, it uses the chosen
   profile directory as the effective data directory for settings, logs,
   `PcIosPlatform`, the shared launcher, and window-state persistence.
4. Removes the root-level `--data-dir DIR` pair before the final parse when a
   profile was selected. This prevents the root argument from overriding the
   chosen profile directory. When the picker is bypassed, `--data-dir DIR` is
   retained and continues to select the direct data directory.

The default root remains `%APPDATA%\ZombieLane-1.4.4` on Windows
(`~/.zombielane-1.4.4` when `APPDATA` is unavailable). `--data-dir DIR` and
`-Dzlport.dataDir=DIR` set the root for the picker; they do not by themselves
disable it. `--no-profile-picker` is the explicit compatibility path for
opening the selected root directly.

The old `java.awt.headless=true` setting was removed from `IosMain`, because a
native Swing profile dialog cannot be displayed in headless mode. The separate
desktop `zlport.launcher.Main` entry point is not used for the packaged game's
normal startup and was not changed for this feature.

### Profile discovery and dialog

`LocalProfiles` uses the `<data-root>\players` directory. It considers only
immediate child directories whose names are exactly 15 decimal digits.
`profile.properties` supplies the display name; profiles without metadata use
their directory ID as the name. The list is sorted by display name (case
insensitively), then by ID.

The dialog offers:

- **Iniciar**: runs the selected directory.
- **Crear perfil nuevo**: asks for a non-empty display name, creates a new
  random 15-digit directory and `profile.properties`, then returns to the
  selector. It does not copy save records from another profile.
- **Cancelar** or closing the dialog: returns without starting the game.

The numeric directory name is a locally generated key, not a game-account ID.
A newly created profile contains only metadata until the game initializes its
own settings and RMS records from defaults/resources.

### Existing flat-layout save migration

Before showing the selector, if `players` contains no recognized profile,
`LocalProfiles` checks `<data-root>\files` for at least one RMS record file
named `1` inside a directory ending in `_data`. If found, it creates a staging
directory under `players`, copies directories and regular files from the
legacy `files` tree into the staged profile's `files` directory, and writes
metadata with the display name `Perfil existente` and
`importedFrom=legacy-local-save`. The staging directory is then moved to its
generated numeric profile name (atomic move where supported).

The source `files` tree is copied, not moved or deleted. On a copy/move error,
the temporary staging tree is cleaned up and startup reports the I/O error.
Only the legacy `files` subtree is copied; other settings, logs, preferences,
and cache files at the old root are not migrated. Migration is skipped if
`players` already contains a recognized numeric profile, or if no matching RMS
record file exists.

### Runtime storage effect

The port's `AppContext` resolves files beneath the configured data directory,
and its J2ME `RecordStore` writes each record at
`<data-dir>\files\<store>_data\1`. As a result, selecting
`<data-root>\players\<id>` isolates that profile's RMS records from all other
profiles. Settings, launcher logs, preferences, and cache use the same
effective profile data directory in this implementation.

## Listing other local profiles as neighbors

### Files changed

- [`LocalProfile.java`](../../src/compat-ios/zlport/ios/LocalProfile.java)
  defines the profile ID and display-name value passed through the PC
  platform API.
- [`IosPlatform.java`](../../src/compat-ios/zlport/ios/IosPlatform.java) and
  [`PcIosPlatform.java`](../../src/runtime-ios/zlport/ios/runtime/PcIosPlatform.java)
  expose an immutable list of local profiles to the game runtime.
- [`LocalProfiles.java`](../../src/launcher-ios/zlport/launcher/LocalProfiles.java)
  finds sibling profile directories under `players`, excluding the selected
  profile.
- [`IosMain.java`](../../src/launcher-ios/zlport/launcher/IosMain.java) passes
  that list to the platform when starting the game.
- [`WindowSocial.java`](../../src/game-ios/com/dchoc/windows/WindowSocial.java)
  adds the local profiles to the neighbor roster when the social window opens.

The ID comes from the profile directory name and the display name from
`profile.properties`. Only immediate sibling directories with a 15-digit
numeric ID are considered, using the same discovery rules as the profile
picker. If the selected data directory is not directly under a `players`
directory (for example, when using a flat legacy data directory), the local
neighbor list is empty. The selected profile is never listed as its own
neighbor.

These profiles are inserted as ordinary neighbor entries and marked accepted,
so they appear in the neighbor list without generating online invitation or
acceptance requests. Reopening the social window does not duplicate them.
This change only provides local neighbor discovery/listing; visits, remote
gamefield loading, gifts, help-action delivery, and timers still depend on
server-backed operations and are not made local by this change.

### Deployment status

The supported deployment is the **classpath override**:
`tools/build-client.ps1` compiles this repository's client classes from
`tools/client-classes.txt` into `client-overrides`, and the release launcher
prepends that directory to the externally supplied `lib` JARs when starting
`IosMain`. The shipped client is built from the tracked source, not from
patched JARs. The neighbor-discovery client change is delivered with the
regular build. [implemented]

Smoke tests in the revival repository verified that the active profile is
excluded, other valid profiles are returned in display-name order, and invalid
directory names and a non-profile data root are ignored. [smoke-tested]

## Custom missions (server-published, live in-game)

### Goal

Allow missions that were not part of the shipped game — holiday events, seasonal
content, admin-authored missions — to be authored with the revival repository's
mission editor tools and made to appear in a running game without restarting it.

### Files changed

- [`MissionManager.java`](../../src/game-ios/com/dchoc/idead/missions/MissionManager.java)
  adds `addCustomMission`, `showCustomMission`, and the objective-attachment
  helper. Custom missions are added to the normal mission list and, unless they
  are survival tips, stay hidden until shown.
- [`CustomMissions.java`](../../src/game-ios/com/dchoc/idead/missions/CustomMissions.java)
  is a new client helper that requests a refresh and shows a mission by id.
- [`GetSessionStatus.java`](../../src/game-ios/com/dchoc/idead/servlets/GetSessionStatus.java)
  is the live poll servlet: it reads Base64 mission/objective payloads, registers
  their custom texts, injects missions, applies shows, and reconciles gifts and
  help actions.
- [`GetCustomMissions.java`](../../src/game-ios/com/dchoc/idead/servlets/GetCustomMissions.java)
  remains the legacy mission-only parser used as the fallback path.
- [`Toolkit.java`](../../src/game-ios/com/dchoc/toolkit/Toolkit.java) adds
  `registerCustomText`, a registry that `getText` consults before the packed text
  table, so custom mission strings resolve without repacking `l0_0`.
- [`GameEngine.java`](../../src/game-ios/com/dchoc/idead/GameEngine.java) requests
  session status during the loading step and polls it every three seconds while
  in a play state.
- The server half — the `GetSessionStatus`, `GetCustomMissions`,
  `ReserveCustomTextIds`, `PublishCustomMission` and `ShowCustomMission`
  endpoints plus the custom-mission store — is implemented in the revival
  server (separate repository), as are the editor and payload build tools.

### How it works

The revival repository's authoring tool encodes the selected mission and its
objectives with the same binary
layouts the packed tables use, Base64-encodes them, and posts them to the
revival server. Before building, it asks the server to reserve unique short
text IDs
(`ReserveCustomTextIds`) and writes them into the mission/objective strings, so
the payload also carries a `texts` map of reserved id to Base64 text. The client
fetches custom missions, registers their texts in `Toolkit`, and injects them
hidden. For SERVER starts the launcher now fetches the catalog with
`catalog=1` and writes a per-session `custom-missions-cache.xml` before
launching the game, with a last-known-good copy per server endpoint in the data
root. `MissionManager.init()` installs that cache at step 60, so definitions
exist before step 80 loads `RMS_MISSIONS`; `CustomMissionsBootstrap` also
preserves an already-installed mission when step 90 refreshes. The catalog
request does not consume pending shows. While playing the client polls
`GetSessionStatus`, and a `ShowCustomMission` request makes a mission appear
immediately through the normal mission HUD. Because the payload is byte-identical to the packed format, the
existing `Mission` and `MissionObjective` readers accept it unchanged.

The poll is revision-gated for missions and shows. The server bumps
`mission_revision` on every publish and `show_revision` for every show, keeps a
bounded persistent show log, and returns only payloads above the revisions the
client sends. An unchanged poll therefore carries only revisions and server
status, and a show stays queued until the client acknowledges it. The revision
and show state persists server-side across restarts (revival server,
separate repository). The client falls
back to the legacy `GetCustomMissions` poll if an older server returns HTTP 404
for `GetSessionStatus`.

Live publishing injects only the mission definition, its objectives, and text
IDs. The revival repository's build tool therefore rejects a mission that uses
setups, progress events, completion events, or an intro/dialogue event, telling
the author to use the packaged form instead.

### Validation state

The client sources compile against the public build allowlist
(`tools/client-classes.txt`), and the publish/serve/show/persistence,
session-status revision gating, catalog show-safety and cache behavior are
covered by the revival server's smoke suite. [smoke-tested] The end-to-end
in-game appearance of a custom mission and the three-second
`GetSessionStatus` poll have not been visually exercised in a live session,
and the accept/save/restart path still needs live validation. [not exercised
in a live session]

### Id and save guards

Early custom-mission experiments edited the packed bundle and the mission
tables in place, which broke saves that still referenced original mission ids.
The revival therefore ended up with guards on both the publish side and the
client side.

- Publish-side id guard (revival server, separate repository): a custom
  mission whose mission, objective or achievement id collides with the
  original set, or whose text ids fall inside the bundle text range, is
  rejected. Ids above the mission-id maximum are not a free range: the
  original objective and achievement ids also count, so the server allocates
  from the reserved range instead of `max(id) + 1`. The original id set is
  loaded from the original mission table, which the server requires at
  startup.
- The shared mission directory stays a LOCAL-only source: SERVER mode clears
  `zlport.ios.missions` and disables `Toolkit`'s shared-pack lookup, so online
  play uses the bundle plus server-issued missions only.
  [implemented] [smoke-tested]
- The game's Base64 codec
  ([`Base64.java`](../../src/game-ios/com/dchoc/idead/network/Base64.java))
  uses the reverse of the standard URL-safe alphabet
  (`_`=62, `-`=63): custom-mission payloads must be encoded by mapping `+` to
  `_` and `/` to `-`, or they will not decode.
  [implemented]
- Client save guard: as a last resort, a failed mission load now keeps the
  downloaded gamefield, marks the load incomplete, writes
  `RMS_GAMEFIELD_BACKUP`/`RMS_MISSIONS_BACKUP`, and blocks saving instead of
  replacing the area with the default scene; `Area.saveData()` returns false
  rather than overwriting the RMS records with a partial mission list, so the
  final upload re-sends the originals. [implemented]

The target architecture — immutable bundle baseline, reserved `10000..19999`
id range with a server-side allocator, catalog load before save load, and a
custom-only authoring mode — is the design direction of the revival
repository. [design]

### Catalog cache and boot order

For SERVER starts the launcher requests `/dead/GetCustomMissions` with
`catalog=1`, which returns all definitions without consuming pending shows,
writes a per-session `custom-missions-cache.xml`, keeps a last-known-good copy
per server endpoint in the data root, and passes the session cache path to the
game via `zlport.custom.missions.cache`. `MissionManager.init()` installs
definitions at step 60, before step 80 loads `RMS_MISSIONS`, and
`addCustomMission()` preserves an existing mission during the step-90
refresh. `Event.setParameterCSV()` now accepts custom ids outside the
original CSV table; `Event.java` and `CustomMissionsBootstrap.java` are part
of `tools/client-classes.txt`, so the overrides actually compile.
[implemented] [smoke-tested]

## Global shared missions

### Goal

Every local player must see the same mission definitions, matching the original
game's shared content. [hypothesis: port] The shipped mission definitions and
English text live in the resource bundle (`game-assets-ios\r1` and `l0_0`) and
the per-profile data directory stores only the mission *state* record
(`RMS_MISSIONS`). [hypothesis: port] This change gives edited mission content a
single shared home that the game reads before the bundle, so all profiles load
identical missions. [implemented]

### Files changed

- [`Toolkit.java`](../../src/game-ios/com/dchoc/toolkit/Toolkit.java) opens pack
  resources (`r1`, `l0_0`, `l1_0`, and so on) from the shared missions
  directory first, then falls back to the bundle. `loadFile` gained
  `openSharedPack` and `sharedMissionsDirectory`. The same file also replaces
  the fixed 4324-entry text cache with a growable map so appended text IDs
  resolve.
- [`IosMain.java`](../../src/launcher-ios/zlport/launcher/IosMain.java) derives
  the shared directory from the profile **root** and publishes it as the
  `zlport.ios.missions` system property before installing the runtime.
- Installing `r1`/`l0_0` into the shared directory is done by the revival
  repository's authoring tools (it stages a package, seeds the directory from
  the selected source on first use, and installs the files with a dated backup
  instead of into the game bundle).

### Shared directory

The shared missions directory defaults to `<data-root>\missions`
(`%APPDATA%\ZombieLane-1.4.4\missions` for the default profile root) and can be
relocated with `-Dzlport.ios.missions`. It holds full `r1` and `l0_0` files;
because the game checks it first, placing them there overrides the bundle for
every profile while leaving the game installation untouched. Mission *state*
stays per profile under `<profile>\files\RMS_MISSIONS_data`. The override is
a LOCAL authoring path only: in SERVER mode the launcher clears the property
and `Toolkit.setSharedMissionsEnabled(false)` makes the shared lookup return
null. [implemented]

### Validation state

The client sources compile against the public build allowlist
(`tools/client-classes.txt`), and the shared directory resolution is shared by
`IosMain` and `Toolkit`. The associated authoring tool's package/install path
and the shared directory behavior are covered by the revival repository's
smoke tests. [smoke-tested] The in-game load of a shared `r1`/`l0_0` has not
been visually exercised in a live session. [not exercised in a live session]

## JSON mission definitions (runtime)

### Goal

Mission definitions should be authored and stored as JSON instead of being
rebuilt into the packed `r1`/`l0_0` tables for every edit. In LOCAL mode the
runtime reads `<shared-missions>\missions.json` first and keeps the packed
tables as a fallback; SERVER mode disables the shared directory so the bundle
and the server-issued missions are the only sources. This keeps an edited
mission catalog from ever clobbering the bundle the online client depends on.
Player saves are not part of this change. [implemented]

### Files changed

- [`Json.java`](../../src/game-ios/com/dchoc/parser/Json.java) is a small
  dependency-free JSON parser used by the runtime (the game ships no JSON
  library). It returns maps, lists, strings, longs, doubles, booleans, and null.
- [`MissionJsonText.java`](../../src/game-ios/com/dchoc/idead/missions/MissionJsonText.java)
  resolves `{"textId": N, "text": "..."}` entries and registers the literal
  string through `Toolkit.registerText`, so JSON text wins over `l0_0`.
- [`Mission.java`](../../src/game-ios/com/dchoc/idead/missions/Mission.java),
  [`MissionObjective.java`](../../src/game-ios/com/dchoc/idead/missions/MissionObjective.java),
  [`MissionSetup.java`](../../src/game-ios/com/dchoc/idead/missions/MissionSetup.java),
  [`MissionEvent.java`](../../src/game-ios/com/dchoc/idead/missions/MissionEvent.java),
  [`ObjectiveEvent.java`](../../src/game-ios/com/dchoc/idead/missions/ObjectiveEvent.java),
  [`Achievement.java`](../../src/game-ios/com/dchoc/idead/missions/Achievement.java), and
  [`AchievementObjective.java`](../../src/game-ios/com/dchoc/idead/missions/AchievementObjective.java)
  gained no-argument constructors and `fromJson` factories next to the binary
  readers.
- [`MissionManager.java`](../../src/game-ios/com/dchoc/idead/missions/MissionManager.java)
  splits the old loader into `loadDataFromBinary()` and adds
  `loadDataFromJson(String)`; `loadData()` tries the shared JSON first.
- [`Toolkit.java`](../../src/game-ios/com/dchoc/toolkit/Toolkit.java) adds
  `loadSharedMissionsJson()` and `registerText(int, String)`.
- Writing the export to the shared directory and repacking the binary tables
  are done by the revival repository's authoring tools.

### How it works

`Toolkit.loadSharedMissionsJson()` reads UTF-8 `missions.json` from the shared
missions directory. `MissionManager.loadDataFromJson` fills the same vectors as
the binary loader: missions (plus survival-tip indexing), objectives (with the
no-reset list), setups, outcome events, objective progress events, and
achievements. A malformed or non-mission document logs a warning and falls
back to `r1`. The authoring tools write the export to
`<data-root>\missions\missions.json`; the game restarts to re-read it.

### Validation state

The runtime JSON load path is covered by the revival repository's smoke
suite, which loads the mission catalog through `loadDataFromJson` and
checks the editor round trip. [smoke-tested] The in-game load has not been
visually exercised in a live session. [not exercised in a live session]

## Packaging and validation state

The supported packaging is `tools/build-client.ps1`: it compiles the client
class allowlist `tools/client-classes.txt` from `src` and the `src-release`
stubs into `client-overrides`, assembles the release tree from the externally
supplied `runtime`, `lib` and `game-assets-ios` directories, and verifies
before zipping that no server, dev or build artifacts leak into the output
(see [editions.md](./editions.md)). The build also runs a headless launcher
smoke check of `IosMain`. In-place JAR patching is not used anywhere in
this tree. [implemented]

The profile chooser has not been visually exercised in a live game session.
[not exercised in a live session]

## Save integrity groundwork (S0)

### Goal

Give the server server-owned history about uploaded client state, so the state
can be validated later without a re-encoding step. The design, the threat
model and the staged plan live in the separate revival repository; the parts
described here are server behavior, not client changes.

### Server changes

All of the following was implemented in the revival server (separate
repository):

- `UpdateAccountInformation` stores each uploaded payload as received and, when
  anything changed, increments the account's monotonic sequence, stamps
  `meta.updatedAt` with server time, records the save schema version and stores
  a SHA-256 of each payload. An identical re-upload is a no-op.
- Every accepted payload change appends one JSON line to the server's save
  history with time, account, sequence, field, size and
  hash, so the change sequence survives payload overwrites.
- Every `/dead/` request appends one JSON line to the server's request log
  with server time, service, account, remote address, response status and
  duration. The status is read back from the HTTP
  exchange, so rejected requests are recorded too.
- `GetServerTime` returns server UTC time as
  `<response><time>epochMillis</time><timezone>UTC</timezone></response>`; no
  client calls it yet.

### Validation state

The metadata behavior, the save history (including identical no-op re-uploads
and continuation across a restart), the request log and the time endpoint,
including persistence across a restart, are covered by the revival server's
smoke suite. [smoke-tested]
The behavior under a real game session has not been exercised.
[not exercised in a live session]

## Server mailbox gifts (S0)

### Goal

One account can send a gift to another, and the recipient can see it in the
mailbox and accept or decline it. Each record applies at most once. This is the
first cross-account server feature of the stage plan (the server trusts the
client but logs everything); the stage plan and its security posture live in
the separate revival repository.

### Server changes

The `SendGift` and `AcceptGift` endpoints on `/dead/`, the mailbox store, the
append-only transition log, pending-gift listing in `GetAccountInformation`,
and resend-cooldown entries in `GetTimers` were all implemented in the revival
server (separate repository). No client change was needed: the request/response
shapes were derived from the existing servlet parsers
([`SendGift`](../../src/game-ios/com/dchoc/idead/servlets/SendGift.java),
[`AcceptGift`](../../src/game-ios/com/dchoc/idead/servlets/AcceptGift.java),
[`GetAccountInformation`](../../src/game-ios/com/dchoc/idead/servlets/GetAccountInformation.java),
[`GetTimers`](../../src/game-ios/com/dchoc/idead/servlets/GetTimers.java));
the derived schema and its provenance labels are recorded in
[network-notes.md](./network-notes.md).

### How it works

- Each record follows the `mailbox` shape:
  `id`, sender, recipient, `kind` (`gift` or
  `help`), `payload` (the item id string, or `itemId,action,expiresAt` for
  help), `status`
  (`pending`/`accepted`/`declined`/`expired`), `createdAt` and `appliedAt` in
  server time. The client sends no gift identity, so ids are generated by the
  server and re-created ids continue after a restart; they
  are also the dedupe key `WindowSocial` uses for incoming gifts.
- Idempotency without client keys: only a `pending` record transitions.
  `AcceptGift` matches (recipient, item id, sender) against the oldest pending
  record; matching accepted/declined records make the request an idempotent
  success with no state change, and the transition log gains no new line.
  Stale pending records become `expired` and stop being listed.
- Every cross-account request writes the state file exactly once inside the
  store's synchronization, so a restart cannot duplicate or lose a gift.
- Cooldown and limits are S0 tuning constants
  (60 s resend cooldown, 7 day pending expiry, 20 pending gifts per
  recipient), not recovered rules of the original service.
- Rejections (unknown recipient, self-sends, recipient lists, non-numeric
  item, duplicate send inside the cooldown, full mailbox, unknown gift,
  malformed `gifts`) answer HTTP 400 with an error element and appear in the
  request log.
- S0 limitation: the server keeps saves as opaque blobs and does not modify
  the recipient's inventory. Accepting a gift transitions the record only; the
  client applies the reward locally after the success response
  (`HUDItemAcceptGift.addGift`). Moving the application server-side is
  reserved for later stages of the revival plan, once saves can be decoded.

### Validation state

The happy send plus recipient/sender listings, the double-accept no-op, the
decline path with `cancel`, persistence across a server restart, every
rejection case in the request log, the help add/accept and pending-help
startup delivery, and mailbox revision persistence are all covered by the
revival server's smoke suite. [smoke-tested]

Two flows remain unverified locally: the XML against the real in-game parsers
with two profiles in the same live session, and whether the recipient client
interprets the derived `gift` elements exactly as expected on screen.
[not exercised in a live session] The schema claims about the original server
stay hypotheses. [hypothesis: port]

## Live session status and neighbor help

### Goal

Keep the running client aware of server reachability and apply custom missions,
mission shows, pending gifts and neighbor-help actions without restarting. This
replaces the mission-only three-second poll with one revision-gated request.

### Files changed

- [`GetSessionStatus.java`](../../src/game-ios/com/dchoc/idead/servlets/GetSessionStatus.java)
  parses the status response, installs changed missions/shows, adds new gifts,
  creates missing neighbors and action commands, and reconciles pending gifts
  and unaccepted help commands against the mailbox snapshot.
- [`GameEngine.java`](../../src/game-ios/com/dchoc/idead/GameEngine.java) polls the
  new servlet every three seconds in play states, tracks server reachability,
  and falls back to `GetCustomMissions` when the server answers 404.
- [`WindowSocial.java`](../../src/game-ios/com/dchoc/windows/WindowSocial.java) adds
  `syncPendingGifts` and `syncPendingActions`; mailbox items are removed directly
  without triggering a full `GetAccountInformation` refresh.
- [`CustomMissions.java`](../../src/game-ios/com/dchoc/idead/missions/CustomMissions.java)
  refreshes through `setGetSessionStatus()`.
- The server half — `GetSessionStatus` with its revision store, help mailbox
  records, pending help in `GetAccountInformation`, and
  `AddNeighborActions` / `AcceptNeighborActions` — is implemented in the
  revival server (separate repository).

### How it works

The client sends `mission_revision`, `show_revision` and `mailbox_revision`.
The response always includes `online`, `server_time`, `protocol` and the three
current revisions. Mission payloads are sent only when the mission revision is
stale; `<show>` entries are sent until acknowledged; when the mailbox revision
is stale the response carries the complete pending gift/action snapshot. The
client uses that snapshot to add new records and remove gifts or unaccepted help
commands that are no longer pending. Accepted help commands remain until
`NeighborCharacter` executes them. `AcceptNeighborActions` returns only the
pending records it resolved, so an expired or already-resolved id is not marked
accepted on the client.

`GetSessionStatus` is in the server's quiet-service set. The revision gating,
show acknowledgement, gift delivery, help add/accept, mailbox kind logging and
restart persistence are covered by the revival server's smoke suite.
[implemented] [smoke-tested] The live
two-client flow and the 404 fallback against an older server have not been
exercised in a visible session. [not exercised in a live session]
