# Open questions

**Applies to:** the `0.1.2` client tree; server-side items are flagged separately. Last reviewed: 2026-10-25.


Priorities below indicate investigation order, not verified game facts. [hypothesis: port]

1. **Port inputs and provenance:** Which APK or IPA, and which version, fed the port? The maintainer says it was AI-generated from a patched IPA, unverified; `platform-1001.jar` also contains a `zlport.apkimport` marker, which suggests an Android APK may have been used. [hypothesis: port]
2. **Recompilation:** Can the decompiled source tree be rebuilt as a whole? A
   full-module compile of `src/game-ios` is not recorded in this repo. Selected
   classes have been compiled separately with the bundled `javac` against the
   `lib` JARs; whether the whole module still rebuilds is open.
   [hypothesis: port]
3. **Extensionless assets:** What are the formats and roles of the 437 extensionless files in `game-assets-ios`? Only `r1` has been identified so far. [hypothesis: IPA]
4. **Animation resource:** What is the structure of entry `829` (ID `66365`), a 146-byte resource identified as animation data [hypothesis: IPA] and used by `ZombieCharacter` in the port? [hypothesis: port]
5. **Map storage:** Where are maps stored, and which assets or records contain them? [hypothesis: IPA]
6. **Bundle mapping:** How do the `bundle_N` directories correspond to the port's `ServerBundle` objects and asset loading? The relationship is unknown. [hypothesis: port]
7. **`xp_limit` meaning:** In the level table, level 2 has `xp_limit` 25; the port compares total XP with the next level's limit. Verify at runtime that 25 XP is needed to reach level 2, and clarify whether the limit is cumulative XP. [hypothesis: port]
8. **Network protocol:** What messages, framing, and server behavior does the game use? The protocol is unknown; 15 `ServerSocketMessage` methods are stubbed in the port. [hypothesis: port]
9. **Uploaded payload format:** Partly answered from source: `avatar` is the `RMS_PROFILE` record (`GameEngine.savePlayerProfile()`, lines 889-898), and `Area.saveData()` sends `SceneLoader.saveCurrent()` and `MissionManager.save()` byte-for-byte as `gamefield` and `missions`, so they match the local area RMS record and `RMS_MISSIONS` (see [Area.java](../../src/game-ios/com/dchoc/idead/areas/Area.java), lines 140-157). Still open: whether captured traffic matches for every area, and how the server should decode them. [hypothesis: port]
10. **Launcher account-ID contract:** answered. The dialog authenticates and uses the server-issued numeric id as `zlport.account.id`; the typed user is the fallback identity only for scripted launches with no dialog, and the server resolves either key. [implemented] [smoke-tested]
11. **Save restoration:** resolved in the revival server (separate repository).
12. **Mission baseline count:** the review notes said the original set has 367 missions / 305 regular, but the port's bundle tables measure 366 / 304. Which record differs, and is one definition missing from this port's tables? [hypothesis: port]
13. **Custom mission lifecycle:** the custom-mission lifecycle (including id reservation) is handled in the revival server (separate repository).
