# Release 0.1.2

Released: 2026-10-23

This patch release fixes launcher validation when no local profile is
available. It keeps the `0.1.1` language support and server isolation
unchanged.

## Fixes

- `PreLauncherDialog.reapplyOptions()` starts from `Settings.defaults()` and
  applies a saved profile only when one is selected and readable. Missing or
  unreadable profiles no longer leave the option fields empty.
- Removed the extra `Settings.fromProperties()` warning pass from
  `validateFields()`, which duplicated field errors with
  `zombie-lane.properties` messages.
- Window width and height are validated and reported independently.

## Verification

Verified on Windows with Temurin 25.0.4.1 and the bundled Java 21.0.8 runtime:

- Build compiles 66 classes.
- Headless `IosMain --help` smoke test exits 0.
- Language harness passes 38 checks.
- Launcher-default harness passes 8 checks with an empty profile list.
- Validator harness passes 13 checks.
- Release allowlist and sanitization checks pass.

Build command:

```powershell
powershell -ExecutionPolicy Bypass -File tools\build-client.ps1 `
    -AssetsRoot "C:\path\to\Zombie-Lane-Assets" `
    -Version 0.1.2 `
    -Release
```

## Upgrade notes

- This replaces the `0.1.1` launcher. The old release folder can remain on
  disk; launch the `0.1.2` folder or its `Run Zombie Lane Alpha.bat`.
- Existing `launcher.properties` and profiles are reused. No migration is
  required.
- In server mode, an empty server user or password is still reported as an
  error. Those credentials are required only for the server connection.

## Known issues

- The loading-screen NPE in `DCiDead.drawLoadingScreen` remains under
  investigation and is not fixed in this release.
- The launcher GUI has no automated visual test.
- Localized server-provided text is not included.
- The three optional settings bytes after the language byte (crops, neighbors
  and recharge) are not player-editable in an official server context; this
  restriction remains pending documentation.
