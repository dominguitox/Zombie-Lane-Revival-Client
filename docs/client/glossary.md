# Glossary

**Applies to:** the `0.1.2` client tree. Last reviewed: 2026-10-25.


- **dchoc** — Java package namespace used by game-port classes, including `com.dchoc`. [hypothesis: port]
- **zlport** — Java package namespace used by port launcher and runtime classes; further meaning is unknown. [hypothesis: port]
- **bundle_N** — Directory naming pattern for numbered asset bundles, where `N` is an identifier; the original game's mapping is unknown. [hypothesis: port]
- **r1** — Supplied file believed to be a packed resource archive; its format and contents remain hypotheses. [hypothesis: IPA]
- **ResourceIDs** — Port interface containing symbolic resource identifiers; whether its names reflect original-game usage is unknown. [hypothesis: port]
- **Tuner** — Port class containing gameplay and debug constants and resource-loading code; original-game correspondence is unknown. [hypothesis: port]
- **Toolkit** — Port class providing game toolkit and resource-loading functions; original-game behavior is unverified. [hypothesis: port]
- **IosMain** — Java entry point used by the supplied PC port; why it is named for iOS is unknown. [hypothesis: port]
- **apkimport** — Text in port metadata (`zlport.apkimport`); the importer and its inputs are unknown. [hypothesis: port]
- **energia.recarga** — Portuguese ("energy recharge"); the `zombie-lane.properties` key for energy regeneration, in seconds (default 30, valid 1-3600). [hypothesis: port]
- **spawn.multiplicador** — Portuguese ("spawn multiplier"); the `zombie-lane.properties` key for periodic zombie spawning (default 2.0, valid 1.0-5.0). [hypothesis: port]
- **vizinho.intervalo** — Portuguese ("neighbor interval"); the `zombie-lane.properties` key for the Rob/neighbour visit interval, in minutes (default 5, valid 1-1440). [hypothesis: port]
- **trucos.billetes** — Mixed ("tricks: cash"); the `zombie-lane.properties` key for the infinite-cash offline cheat, written by the launcher only for local sessions and ignored when launching against a server (`true`/`false`, default `false`). [implemented]
- **sem código na 1.4.4** — Portuguese ("without code in 1.4.4"); the message prefix of the port's stubbed methods that throw `UnsupportedOperationException`. [hypothesis: port]
- **OfflineNetwork** — A URL handler in the port that throws `ConnectException` for HTTP and HTTPS; the source `Launcher.run()` no longer installs it. [hypothesis: port]
- **LocalProfiles** — Revival launcher class (`zlport.launcher.LocalProfiles`) that discovers, selects, creates and lists local player profiles and their sibling profiles as neighbors. [implemented]
- **classpath override** — The revival's supported deployment for changed client code: classes compiled from `src/` are placed in `client-overrides/` and prepended to the classpath ahead of the `lib` JARs when the release launcher starts the game. [implemented]
- **custom mission** — A mission the revival server publishes (`PublishCustomMission`, separate repository) and injects into a running client, so it appears without restarting the game. [implemented]
