# Release 0.1.2

Released: 2026-10-23 (release notes regenerated on 2026-10-25)

The first `0.1.2` build fixed launcher validation when no local profile is
available and kept the `0.1.1` language support and server isolation
unchanged. The release notes below also cover the offline cheats, launcher
cleanup, audio controls and clearer startup diagnostics added on 2026-10-25.
The package must be rebuilt to include these later source changes.

## Fixes

- `PreLauncherDialog.reapplyOptions()` starts from `Settings.defaults()` and
  applies a saved profile only when one is selected and readable. Missing or
  unreadable profiles no longer leave the option fields empty.
- Removed the extra `Settings.fromProperties()` warning pass from
  `validateFields()`, which duplicated field errors with
  `zombie-lane.properties` messages.
- Window width and height are validated and reported independently.

## Offline cheats and launcher cleanup (added 2026-10-25)

- The "Zoom por paso de rueda", "Energía: segundos por punto" and "Vecinos:
  minutos" fields are gone from the launcher. `roda.passo` and
  `vizinho.intervalo` stay as file-only keys, and a tuned `energia.recarga`
  value survives launch-to-launch.
- "Energía infinita": a checkbox in the game options. When checked it writes
  `energia.recarga=1` (1 second per energy point); when unchecked the
  launcher preserves the value already in the file, or restores the default
  30 if the file still holds the checkbox's own `1`.
- "Billetes infinitos": a checkbox that writes `trucos.billetes=true`. While
  it binds, the wallet rides a 9,999,999 floor, but the profile save always
  stores the honest balance: every real earn is booked in and every purchase
  subtracts (min 0). Turning the cheat off brings the honest balance back,
  and a wallet that was already saved at the floor by an earlier cheat build
  is revoked once (the book starts at 0). Buying, missions and unlocks
  behave normally, the cheat is offered only for local profiles, stays
  disabled in server mode, and a server session ignores it even if the file
  says true.
- The checkboxes keep their per-profile state: they follow each selected
  profile's saved options.

## In-game audio and startup diagnostics (added 2026-10-25)

- Replaced the in-game music and sound-effect on/off switches with separate
  sliders from 0 to 100 (0 mutes audio). Dragging previews the OpenAL gain for
  new effects and the currently playing music; the selected value persists in
  the existing `settings` record after its original eight bytes.
- Existing settings records migrate from their two audio flags: enabled audio
  starts at 100; disabled audio starts at 0. The record's other fields and
  bytes remain in place.
- Expected end-of-file and optional-resource misses no longer print as errors.
  Startup diagnostics are summarized after game loading; real failures retain
  their stack trace.

## Launcher safety and console visibility (added 2026-10-25)

- The generated Windows launcher starts the game with `javaw.exe` by default,
  so no CMD window remains open while playing. The Advanced option `Show CMD
  on next launch` stores `console.visible=true` in the global
  `launcher.properties`; it takes effect the next time the generated batch
  launcher runs. With it enabled, the launcher uses `java.exe` and keeps the
  console available. Game logs continue to be written to the normal log file.
- `Restore safe defaults` resets game options for the selected profile without
  changing the selected profile or connection details: 1136x640 windowed (not
  maximized/fullscreen), audio on, normal energy recharge (30 seconds), normal
  zombie multiplier (2.0), normal wheel zoom and neighbor interval, cheats
  off, INFO logging and GL debug off. The reset is saved when Play is confirmed.

### Investigation log: reversibility report (2026-10-25, closed)

The first live test of the cash helper reported that a floor value kept
reappearing after the cheat was turned off. The full local-forensics method
from [pc-execution.md](./client/pc-execution.md) could reconstruct the
reporter's three-session sequence from records, log rotation, and session
reports, but not the exact writer of the polluted save. A diagnostic build
that logged every bind/spend/absolute-write/save decision under the
`android.CashCheat` tag (`zombie-lane.log`) was then handed over for a
repetition, which settled it:

- The four-session replication cleanly reversed everything. A fresh profile
  saved its honest tutorial balance; the cheat-on session bound the floor
  view onto that balance as the book, spent it down through purchases (min
  0), and still saved the honest book (0) rather than the floor; the next
  cheat-off session loaded 0 and saved 0; re-enabling restored the floor view
  with the book still from the honest record.
- Every observed value matched the design table: floor view on while the
  helper binds, wall wallet unchanged by spends, book = previous book +
  earns − spends, and `save()` always writing the book. No runtime path
  wrote the floor into a save.
- The earlier report is explained by pre-ledger compiled jars: some build
  folders in circulation still held classes that saved the raw wallet. Both
  remaining test builds now carry the final classes, so any local launcher
  behaves the same.

Design note that came out of the report: after turning the cheat off the
player gets back exactly the balance they had before enabling it, plus what
they honestly earned while it was on, minus what they spent, floored at 0.
The reporter's own replication is the boundary case: 2 saved billetes, a
cheat session whose purchases totalled 57 (35 + 3 + 3 + 3 + 3 + 5 + 5), so
the book clamped to 0 and the cheat-off session showed 0 — correct, not a
bug report. That will read as "the cheat stole my balance" whenever the
pre-cheat balance was smaller than the cheat-session spending; if it
bothers players we can revisit restoring at least the pre-enabled amount
as a loan. The floor grant, however, stays a loan, not a gift; the cure is
ticking the checkbox again, not a persistent grant.

The trace logging served its purpose and was removed from `PlayerProfile`
before packaging; the release classes are trace-free (the only remaining
`CashCheat` bytes are part of the `applyCashCheatOverride` method name).

## Verification

Verified on Windows with Temurin 25.0.4.1 and the bundled Java 21.0.8 runtime:

- Build compiles the expanded client class allowlist, including audio players,
  in-game volume controls and startup diagnostics.
- The current working tree compiles cleanly with Java 25 targeting Java 21;
  the release allowlist produces 93 class files. `IosMain --help` exits 0.
- Headless `IosMain --help` smoke test exits 0.
- Language harness passes 38 checks.
- Launcher-default harness passes 8 checks with an empty profile list.
- Validator harness passes 13 checks.
- Release allowlist and sanitization checks pass.
- Cheat harness on the compiled release classes: the platform reports the
  request, the floor view shows 9,999,999 while the honest book keeps every
  earn and spend (min 0), absolute wallet writes only adjust the book by
  their delta, a grant-polluted wallet revokes at bind, and with no request
  the wallet stays vanilla.
- Two full local launches with the modified classes ran for minutes and
  rendered with the render loop alive (frames counted in the exit summary),
  one of them with `trucos.billetes=true` in the profile properties; a
  headless harness checked the local and server write behaviour of the new
  keys. The launcher GUI itself has no automated visual test (unchanged
  known issue).
- The new audio sliders have compiled but still need visual and audible
  validation in a visible gameplay session.

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

- The inherited loading-screen NPE note from the 0.1.1 documentation has not
  been reproduced by the maintainer and is not considered a confirmed issue in
  this release.
- The launcher GUI has no automated visual test.
- Localized server-provided text is not included.
- The three optional settings bytes after the language byte (crops, neighbors
  and recharge) are not player-editable in an official server context; this
  restriction remains pending documentation.
