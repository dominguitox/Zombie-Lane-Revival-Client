# Editions: dev, server and player

**Status:** the dev/player split is implemented; the client-side no-op release
stubs and the sanitizing release build are implemented in this repository. The
authoritative revival server is a separate project. **Date:** 2026-10-25.

This repository is the **public sanitized client tree**: client sources, the
release build script and the no-op `src-release` stubs. The **dev edition**
lives in a separate private revival repository, which additionally contains the
dev hooks (`src-dev/`), the server implementation, tests and authoring tools.
A player edition is assembled from this tree with `tools/build-client.ps1`,
which compiles an explicit client class allowlist without the dev hooks. The
server never ships with either edition.

## The four groups

| Group | Shipped to players? | Lives in | Notes |
|---|---|---|---|
| **Client (local)** | Yes | `game-assets-ios/`, `src/` client modules | Runs on the player's machine; a player can read and modify everything here, so it is never trusted. |
| **Server** | No | Separate revival repository | Authoritative rules, accounts, global missions, custom-mission delivery. |
| **Dev-only** | No | Separate revival repository | Authoring tools, tests, dev hooks (`src-dev/`), server code and data, packaging. |
| **Shared** | Partly | `src/` shared code, mission catalog JSON, formats | Definitions and formats both sides can use; the catalog is delivered by the server. |

### Client-local files

These stay on the player's machine and must be treated as untrusted input:

- `game-assets-ios/` — the shipped asset bundle (supplied externally at build
  time; not versioned in this repository).
- `client-overrides/` — the compiled client changes placed into the release
  (the shipped client is built from `src/`, not patched JARs).
- Per-profile saves under `%APPDATA%\ZombieLane-1.4.4\players\<id>\`, and the
  shared missions directory `%APPDATA%\ZombieLane-1.4.4\missions\`.
- `actions.jsonl` and the local player-action log. `[implemented]`

### Server-only files

These never appear in the player package. Accounts, mailbox records, custom
missions, save uploads and append-only request/save/mailbox logs belong to the
separate revival repository's server and data directories.

## The dev/release hook: `DevMode`

Client code that must differ between editions calls `zlport.dev.DevMode`. The
dev edition compiles the real class from the dev tree's `src-dev`; this
repository compiles the no-op from `src-release`, so the dev behavior is
**not present** in the player binary (not merely hidden):

```java
if (DevMode.enabled()) {
   // dev-only behavior
}
```

Both `DevMode` classes share the same public methods; only the bodies differ.
The same split applies to `zlport.dev.LauncherDevOptions`.

## Building the player edition

This repository builds only the player edition:

```powershell
powershell -ExecutionPolicy Bypass -File tools\build-client.ps1 `
    -AssetsRoot "C:\path\to\Zombie-Lane-Assets" `
    -Version 0.1.1 `
    -Release
```

`-Release` requires clean, committed sources. The build compiles the class
allowlist `tools/client-classes.txt` against `src` and `src-release`, verifies
the no-op stubs with `javap`, copies the external `runtime`, `lib` and
`game-assets-ios` trees into the output, and rejects any server-side,
dev-only or unexpected artifact before zipping and hashing the result. See
[the repository README](../../README.md#build) for prerequisites, options and
the full build steps.

## Rule of thumb

**Anything the player can read or modify cannot be authoritative.** Coins, XP,
inventory and mission progress must move to the server before they can be
trusted. The client keeps a local copy only for display and prediction. The
staged client-server plan and the save-integrity groundwork live in the
separate revival repository.
