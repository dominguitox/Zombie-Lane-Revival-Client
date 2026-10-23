# Changelog

All notable changes to the public Zombie Lane client are documented here.
The format follows Keep a Changelog, and versions use the alpha `0.1.x` scheme.

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

- The loading-screen NPE in `DCiDead.drawLoadingScreen` remains under
  investigation.
- The launcher GUI has no automated visual test.
- Localized server-provided text is not included.

## [0.1.0] - 2026-10-22

### Added

- Initial sanitized public client source snapshot.
- Player-edition release pipeline with no-op development stubs.
- Explicit client class allowlist and release sanitization checks.
- Headless launcher smoke test.
