# Language Support

Applies to: **0.1.1**

## Scope

The launcher and the game share one language choice. Everything is local: the
language is never sent to, stored on, or synchronized with the server.

Supported languages:

| Code | Game index | Name |
| --- | --- | --- |
| `en` | 0 | English |
| `fr` | 1 | French |
| `it` | 2 | Italian |
| `de` | 3 | German |
| `es` | 4 | Spanish |

The indexes match the language value already used by the game's
`settings_data/1` record and its `l0_0` through `l4_0` assets.

## Where the preference lives

### Launcher preference

`launcher.properties` in the data root stores the launcher choice:

```properties
language=es
```

The data root is `%APPDATA%\ZombieLane-1.4.4` by default, or the directory
passed with `--data-dir` / `-Dzlport.dataDir`.

Missing values are valid:

- If `launcher.properties` does not exist, the launcher asks for a language on
  the first graphical run.
- If the file exists but has no `language` key (an older installation), the
  launcher asks once and keeps the other saved fields.
- If the value is unknown or malformed, it is treated as missing.
- Regional values such as `es-MX` or `fr_FR` are normalized to their base
  language.

`--no-launcher` does not open the language dialog. It uses the operating-system
language, falling back to English. If the game later writes a supported language
to the profile, the launcher preference is updated after the session.

### Game preference

The game reads the language from byte 0 of the local settings record:

```text
players/<profile-id>/files/settings_data/1
```

The record is 256 bytes. Only byte 0 is changed; all other bytes are preserved.
The launcher does not create a partial one-byte record. If the record does not
exist yet, it sets the `zlport.language` system property and the game creates a
complete valid record through `Game.loadSettings()`.

For server-mode sessions, the downloaded save lives in a temporary session
directory. The launcher applies the language there before the game starts and
reads it back before that temporary directory is deleted. The uploaded save is
therefore unchanged by the language flow, and the server is not involved.

## Startup flow

1. `IosMain` loads `launcher.properties`.
2. In graphical mode, a missing `language` opens the first-run dialog before
   the launcher window. Cancelling the dialog aborts the launch.
3. The choice is saved to `launcher.properties`.
4. `LauncherText` is switched to the chosen language, and
   `zlport.language` is exposed to the game process.
5. `GameLanguage.apply()` updates byte 0 of an existing settings record. A
   missing record is left missing for the game to create.
6. The game reads the language through `Game.getDeviceLangauge()`, which gives
   `zlport.language` priority over the operating-system locale.
7. After the game exits, `IosMain.syncLanguage()` reads byte 0 from the profile
   or temporary server save and updates `launcher.properties` if the player
   changed the language in the in-game options.

## UI coverage

The embedded catalogue `LauncherText` covers:

- the first-run language dialog;
- the main launcher, profile picker, profile creation and advanced options;
- validation and connection messages;
- the privacy consent dialog.

Brand names, log-level identifiers (`SEVERE`, `INFO`, and so on), HTTP status
text and server-provided detail strings are intentionally not translated.

There is no external properties file and no `ResourceBundle` lookup. This keeps
the release class list and packaging unchanged: the translations are compiled
into the client.

## Extension points

To add a language:

1. Add its code, index and native name to `LauncherLanguage`.
2. Add a complete map to `LauncherText` and select it in `LauncherText.table`.
3. Confirm that the game has assets for the corresponding index.
4. Add the language to the manual checks in `docs/release-0.1.1.md`.

Indexes are part of the settings format. Do not renumber existing languages;
append new indexes instead.

## Verification

The `0.1.1` language harness checks 38 cases:

- all five catalogue languages resolve;
- unknown languages fall back to English;
- `launcher.properties` round-trips and migrates files without `language`;
- regional language codes normalize;
- byte 0 changes while bytes 1-255 remain identical;
- missing settings records are not created by the launcher;
- `zlport.language` is exposed and cleared;
- validator messages follow the active language.

## Relevant files

- `src/launcher-ios/zlport/launcher/LauncherLanguage.java`
- `src/launcher-ios/zlport/launcher/LauncherText.java`
- `src/launcher-ios/zlport/launcher/LanguageDialog.java`
- `src/launcher-ios/zlport/launcher/GameLanguage.java`
- `src/launcher-ios/zlport/launcher/PreLauncherState.java`
- `src/launcher-ios/zlport/launcher/IosMain.java`
- `src/game-ios/com/dchoc/idead/Game.java`
