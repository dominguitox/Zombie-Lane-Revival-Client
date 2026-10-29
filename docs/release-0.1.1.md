# Release 0.1.1

Released: 2026-10-23

This is the first public alpha assembled from the sanitized client tree. It
keeps the server separate and does not include server code, server data or
credentials.

## Changes

### Language selection

- Added a first-run language dialog for English, French, Italian, German and
  Spanish.
- Added a local `language` field to `launcher.properties`.
- Added `LauncherText`, an embedded catalogue for launcher and validation
  strings.
- Added `GameLanguage`, which updates only byte 0 of an existing
  `settings_data/1` record.
- Added `zlport.language` so a new profile can be created with the correct
  language by the game.
- Synchronized in-game language changes back to `launcher.properties` after
  the session, including server-mode sessions with a temporary save.
- Localized `PreLauncherDialog`, `AlphaConsent`, `LocalProfiles` and
  `ServerConnection` user-facing messages.

Language data is local only. The server protocol and the uploaded save format
are unchanged.

### Launcher fixes

- The Play button is always enabled. `play()` validates the form and shows an
  error instead of leaving the player with a disabled button.
- Added bounded validation for host, user, password, port, window size, zoom,
  energy recharge, zombie multiplier and neighbor interval.
- Preserved `FieldFilter` input limits and the no-password persistence rule.

### Build

- The release compiles an explicit allowlist of 44 Java sources, producing 66
  classes including inner classes.
- Release stubs remain the no-op `DevMode` and `LauncherDevOptions`.
- Release assembly rejects sources, tools, docs, tests, server data and other
  non-runtime artifacts.
- Added `docs/` and `CHANGELOG.md`; the release notes now describe the language
  feature.

## Verification

Verified on Windows with Temurin 25.0.4.1 and the bundled Java 21.0.8 runtime:

- Build compiles 66 classes.
- Headless `IosMain --help` smoke test exits 0 and has no `[dev]` banner.
- Language harness passes 38 checks.
- Validator harness passes 13 checks.
- Release allowlist and sanitization checks pass.
- The release folder and zip are generated from the committed source with
  `-Release`.

Build command:

```powershell
powershell -ExecutionPolicy Bypass -File tools\build-client.ps1 `
    -AssetsRoot "C:\path\to\Zombie-Lane-Assets" `
    -Version 0.1.1 `
    -Release
```

## Upgrade notes

- Existing `launcher.properties` files without `language` prompt once on the
  next graphical launch. Other saved launcher fields are retained.
- A profile without `settings_data/1` is not given a one-byte file. The game
  creates the complete record when it saves settings.
- In `--no-launcher` mode the system language is used and no dialog appears.
- Players can change the language later from the in-game options. The launcher
  picks up the change after the game exits.

## Known issues

- An inherited development note claimed a possible loading-screen NPE in
  `DCiDead.drawLoadingScreen` when `smLoadingScreen.getCollisionBox(1)` returns
  `null`. No reporter or reproduction was recorded; the maintainer reports
  never observing it. Treat this as unverified historical documentation, not
  a confirmed issue in the current release.
- There is no automated GUI test for the launcher. Verification covers
  compilation, headless smoke, validation and language logic.
- Localized server-provided text is outside this release.
- The three optional settings bytes after the language byte (crops, neighbors
  and recharge) are not player-editable in an official server context; this
  restriction remains pending documentation.
