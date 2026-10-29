# Changelog

All notable changes to the public Zombie Lane client are documented here.
The format follows Keep a Changelog, and versions use the alpha `0.1.x` scheme.

## [0.1.3] - 2026-10-28

### Added

- Opt-in diagnostic reports for LOCAL gameplay, sent independently of the game
  server to the HTTPS report receiver. A unique per-tester key is required.
- One ZIP per completed session, a 1 MiB upload limit, up to three pending
  retries per startup, and 30-day local report retention.
- Renewed consent for the separate report destination; revoking consent stops
  the current session's report. Tokens and saved games are not bundled in reports.
- LOCAL connection checks probe only the report receiver, never the game server
  or public status site. No game server is needed to play and send diagnostics.

### Fixed

- Official package and report metadata identify the build as `0.1.3`, without a
  development suffix. The separate receiver must be updated to accept it.
- The Windows launcher explains when runtime, libraries, assets, or launcher
  classes are missing instead of silently failing to start an incomplete package.

The next official version is `0.1.4`. The `0.1.3c` label below refers only to
the earlier development build, not the release package.

## [0.1.3c] - Previous development build

### Changed

- Report and health checks now use the receiver's direct PHP script URLs, so
  shared hosting does not need `.htaccess` rewrites.
- **Probar conexiones** is available in LOCAL mode and checks only the reports
  receiver with a read-only GET; SERVER mode retains its four independent GETs.
- Receiver accepts reports identified as `0.1.3c`; older development labels
  remain supported.

This remains an unofficial test build; the latest published launcher version
is `0.1.2`. A synthetic upload should be verified before wider distribution.

## [0.1.3b] - Previous development build

### Added

- Opt-in session report uploads to an independent HTTPS endpoint, including
  offline mode, with a 1 MiB cap, bounded startup retries and local 30-day ZIP
  expiry. Old consent must be renewed for the new destination; public uploads
  also require a unique per-tester key.
- Loopback upload/consent regression test and PHP/MySQL receiver deployment plan.

The web receiver has not been deployed or validated yet; this is not a public
release. The `b` suffix identifies this unofficial development build; the
latest published launcher version remains `0.1.2`.

## [0.1.2] - 2026-10-23

### Fixed

- Launcher option fields now fall back to the documented defaults when there
  is no local profile, or when the selected profile cannot be read. Previously
  the fields were left blank and produced a wall of validation errors.
- Removed duplicate `zombie-lane.properties` warnings from the launcher
  validator. Window width and height are now reported as independent errors.

### Changed

- The default release version is `0.1.2`.
- Launcher options now expose local infinite-energy and infinite-cash cheats;
  the cash cheat keeps the saved balance honest.
- Removed the wheel-zoom, energy-recharge and neighbor-interval fields from
  the launcher. The related file keys remain supported.
- Replaced the in-game music and sound-effect switches with independent
  0–100 volume sliders (0 mutes audio).
- Startup logs summarize expected resource misses and end-of-file probes;
  actual failures keep their stack traces.
- The generated Windows launcher hides the CMD window by default; an
  opt-in setting can show it on the next launch.
- The launcher can restore conservative game defaults, including a
  windowed 1136x640 display and disabled cheats.

## [0.1.1] - 2026-10-23

### Added

- First-run language selector for English, French, Italian, German and Spanish.
- Local language persistence in `launcher.properties` and synchronization from
  the in-game settings after each session.
- Embedded launcher translation catalogue and localized validation, profile,
  privacy and server-connection messages.
- `docs/` documentation for the language flow and this release.

### Fixed

- Play button remains enabled and validates the form before launch.
- Bounded validation for host, user, password, port, window size, zoom, energy
  recharge, zombie multiplier and neighbor interval.

### Changed

- The release class allowlist now compiles 44 sources and produces 66 classes.
- Release notes describe the language and launcher changes.

### Known issues

- An inherited note mentioned a possible loading-screen NPE, but no reporter
  or reproduction was recorded; the maintainer has never observed it. It is
  unverified historical documentation, not a confirmed issue.
- The launcher GUI has no automated visual test.
- Localized server-provided text is not included.

## [0.1.0] - 2026-10-22

### Added

- Initial sanitized public client source snapshot.
- Player-edition release pipeline with no-op development stubs.
- Explicit client class allowlist and release sanitization checks.
- Headless launcher smoke test.
