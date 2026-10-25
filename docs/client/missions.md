# Missions, objectives, and achievements

**Applies to:** the `0.1.2` client tree and the shipped packed resources. Last reviewed: 2026-10-25.


This documents the decompiled Java port and the supplied packed game-resource
tables. It is not independently verified behavior of the original game.
[hypothesis: port]

The mission system is the game's scripted-progression layer. It is split into
two parts:

- **Definitions** — static records packed into the game resource `r1` and
  decoded by `MissionManager` at startup. They describe what each mission is,
  but hold no player state.
- **State** — per-profile bytes serialized to the record store `RMS_MISSIONS`
  (with `RMS_MISSIONS_BACKUP` as fallback). They record which missions are
  active, objective counters, timestamps, and achievement tiers.

Editing definitions changes the campaign for every profile that loads them;
editing state changes one save only. See [Persistence and save
locations](#persistence-and-save-locations).

## Data inventory

The mission-system document is the shared `missions.json` export of the packed
tables. It started as a decoded inventory of the tables and is now also a
runtime definition source (see [JSON definitions
(runtime)](#json-definitions-runtime)). It is not a save file. The packed
tables and the `missions.json` export come from the supplied build; the
`game-assets-ios` bundle ships next to the installation and is not versioned
in this repository. The baseline contains:

| Records | Count | Packed resource |
| --- | ---: | --- |
| Mission / tip definitions | 366 | `80468` |
| Mission objectives | 576 | `80475` |
| Mission setups | 328 | `80470` |
| Outro events selected by mission type | 6 | `80472` |
| Outro events selected by mission ID | 48 | `80473` |
| Objective progress events | 2 | `80476` |
| Achievement definitions | 10 | `80435` |
| Achievement tier objectives | 40 | `80436` |

The 366 definitions comprise 304 regular missions, 29 hidden missions, 4
tutorials, and 29 survival tips. Six records have no objectives; an empty
objective list is represented as-is. In the runtime, the all-objectives
completion check starts true, so an active zero-objective mission is eligible
to complete immediately. Twenty-nine definitions have no title text. Counter
type `3287` occurs in an objective description (“Complete a x3 Combo”) but has
no named counter constant in the decompiled `Counter` class; the JSON preserves
the numeric ID and marks its type `unknown`. [hypothesis: port]

Mission and objective descriptions are decoded from the English `l0_0`
localization pack, retaining the text ID and text. A text ID of `-1` means
absent text; `-2` means an intentionally empty string. Item, currency, area,
spawn, location, and event IDs remain numeric where their meaning comes from
other tables. The JSON maps known counter and mission type IDs from the source
constants. See `counterTypes`, `missionTypes`, and `metadata.semantics` in the
JSON for code maps and runtime rules used to interpret the records.
[hypothesis: port]

## Resource tables and load order

`MissionManager.init()` allocates the collections and calls `loadData()`. When
the shared `missions.json` is present and valid it is the definition source
(see [JSON definitions (runtime)](#json-definitions-runtime)); otherwise
`loadDataFromBinary()` reads the eight packed tables in a fixed order. Each
table's records are read in a loop until the reader throws `EOFException`; a
non-EOF parse error prints a stack trace and still advances to the next table,
so a malformed table loads partially:

1. `80468` mission / tip definitions.
2. `80475` mission objectives, attached to their mission by matching
   `missionId` (the first mission whose ID matches receives the objective).
   Objectives whose reset flag is false are also added to the manager-wide
   `smNoResetObjectives` list.
3. `80470` mission setups.
4. `80472` outro events selected by mission **type**.
5. `80473` outro events selected by mission **ID** (appended to the same
   collection).
6. `80476` objective progress events, attached via
   `mission.getObjectiveByType(type, value)`.
7. `80435` achievement definitions.
8. `80436` achievement tier objectives, attached by `achievementId`.

Two later steps, `addCustomMission`/`showCustomMission`, inject
server-provided missions into the same collections at run time. Custom
definitions and their objectives are read from the custom payload instead of
the packed tables. The delivering server of those missions is a separate
repository not documented here. [hypothesis: port]

## JSON definitions (runtime)

`MissionManager.loadData()` looks for `missions.json` in the shared missions
directory (`<data-root>\missions`, overridable with
`-Dzlport.ios.missions`) before decoding `r1`. The shared directory is a LOCAL
authoring source only: SERVER mode disables it
(`IosMain.configureMissionSource`), so online play always uses the bundle plus
server-issued missions:

1. `Toolkit.loadSharedMissionsJson()` reads the file as UTF-8 and returns
   `null` when it is missing or unreadable.
2. `MissionManager.loadDataFromJson()` parses it with the dependency-free
   `com.dchoc.parser.Json` and fills the same collections as the binary
   loader: missions, objectives, setups, outcome events, objective progress
   events, and achievements. Text overrides are registered through
   [`MissionJsonText`](../../src/game-ios/com/dchoc/idead/missions/MissionJsonText.java).
3. If the file exists but cannot be parsed, or its root has no `missions`
   array, the loader prints a warning and falls back to the packed tables.

Root keys are `metadata`, `missionTypes`, `counterTypes`, `missions`,
`achievements`, `unassignedSetups`, `missionEventsById`,
`missionEventsByType`, and `objectiveEvents`. Per-mission keys are listed in
[JSON field reference](#json-field-reference). `type`,
`condition.counterType`, and `objectiveEvents[].counterType` are caches; the
loader uses the numeric IDs.

Text handling: each text is `{"textId": N, "text": "..."}`. The loader
registers `text` as a runtime override through `Toolkit.registerText`, so the
JSON string is shown even when `l0_0` holds different text at `N`. A `textId`
of `-1` with a literal string receives a synthetic negative ID starting at
`-100000`; `-2` keeps the empty-string sentinel; a missing `text` leaves the
ID unchanged.

The per-mission `progressEvents` and `outroEvents` arrays are export views
derived from the root event tables; the runtime reads the root tables
directly.

## The mission record

### Fields

`Mission(DChocByteArray)` reads one record. Defaults before reading:
`new = true`, `iconAnimationID = -1`, `introIconAnimationID = -1`,
`introEventType = -1`, `expirationEventType = -1`, and every text ID `-1`.
Types below are the reader calls; the field name is the port's.

| Order | Field | Type | Meaning |
| ---: | --- | --- | --- |
| 1 | `id` | short | Mission ID; the key used by prerequisites, setups, and saves. |
| 2 | `titleTextID` | short | HUD/list/window title. |
| 3 | `hintTextID` | short | Hint line shown in the mission window (`-1` hides it). |
| 4 | `descriptionTextID` | short | Main description text. |
| 5 | `completedTextID` | short | Text shown when completed. |
| 6 | `unusedShort` | short | Read and discarded by the port. |
| 7 | `rewardXp` | short | XP granted at reward collection. |
| 8 | `rewardCoins` | short | Coins granted at reward collection. |
| 9 | `rewardItems` | int-value list | Pairs of `{itemId, amount}`. |
| 10 | `requiredMissions` | int array | Prerequisite mission IDs (`null` if count ≤ 0). |
| 11 | `requiredLevel` | byte | Minimum player level. |
| 12 | `type` | short | Mission type ID (see below). |
| 13 | `facebookPost` | int array | Facebook post text IDs; non-null enables the FB button. |
| 14 | `twitterPost` | short | Twitter post text ID (`-1` hides it). |
| 15 | `duration` | int | Source minutes; `> 0` becomes `minutes * 60000` ms, otherwise stored as-is. |
| 16 | `locked` | boolean | Explicit lock gate. |
| 17 | `canRetry` | boolean | Whether expiry returns it to inactive instead of failing. |
| 18 | `saveAfterCompleted` | boolean | Queue a delayed save after completion. |
| 19 | `iconAnimationID` | int | HUD icon animation. |
| 20 | `unusedInt` | int | Read and discarded by the port. |
| 21 | `introEventType` | short | Event played on activation before acceptance (`-1` = none). |
| 22 | `introEventParameters` | int array | Parameters for the intro event. |
| 23 | `introIconAnimationID` | int | Icon animation for the intro. |
| 24 | `expirationEventType` | short | Event played on timeout (`-1` = finish immediately). |
| 25 | `notificationID` | short | Notification definition ID used for reminders. |

`readIntArray` reads an `int` count (`≤ 0` → `null`) followed by that many
32-bit ints. `readIntValueList` reads an `int` count followed by that many
`{int, int}` pairs. All multi-byte values are big-endian.

### Mission types

| ID | Constant | Runtime note |
| ---: | --- | --- |
| `2286` | Hidden | Data record; not added as a regular HUD mission. |
| `2288` | Tutorial | Activation/completion is also sent to `TutorialFlow`. |
| `2292` | Regular | Added to the mission HUD and mission list. |
| `2399` | Survival tip | Kept in the tips collection; completion increments the tips counter. Its tracking event uses type `18`. |
| `3008` | Coming mission | Recognized by the class; none occur in this extracted table. |
| `-1` | Invalid | Not a valid type. |

Unrecognized types still load and can be activated, but only regular missions
are added to the HUD.

## States and the mission lifecycle

### States

| State | Constant | Meaning |
| ---: | --- | --- |
| `0` | `STATE_INACTIVE` | Loaded, not started; may be gated. |
| `1` | `STATE_WAITING_TO_BE_ACCEPTED` | Activated with an intro event that has not finished. |
| `2` | `STATE_ACCEPTED` | Active and counting; registered with the manager. |
| `3` | `STATE_REWARDS_COLLECTED` | Done; rewards granted, removed from the active list. |
| `4` | (unnamed) | Failed/abandoned non-retryable expiry; sets the completed flag. |

Serially the record saves state `3` compactly as a single byte, while states
`1`, `2`, and `4` store full state (see [Persistence](#persistence-and-save-locations)).
State `4` reports `isCompleted() == true` but is **not** state `3`, so it never
satisfies a prerequisite gate.

### Activation gating

`MissionManager.updateNewMissions()` scans every mission each time
`smUpdateNewMissions` is set. A mission in state `0` activates when all of:

- `isLocked()` is false (the explicit `locked` flag),
- `!isLockedByLevel()` — `PlayerProfile.getLevel() >= requiredLevel`, and
- `!isLockedByMission()` — every ID in `requiredMissions` resolves to a mission
  whose state is exactly `3`.

A missing prerequisite ID is ignored (`getMission` returns null, so it does not
block); a found prerequisite in any state other than `3` blocks activation. If
the type is regular, activation adds it to the HUD and the mission list and,
for the single mission ID `4318`, opens its window when new.

`Tuner` also defines mission IDs that unlock systems when they become active
(`visitRob`, `fences`, `buildingParts`, `crafting`, `tutorHelping`,
`dailyBonus`, `buildingUpgrade`, `spouse`, `dog`). Only the visit-Rob unlock is
wired directly into `Mission.changeState`; the others are consulted by their
feature code. [hypothesis: port]

### Intro event and acceptance

`Mission.activate()` only works from state `0`. If `introEventType == -1` the
mission goes straight to state `2`; otherwise it enters state `1`.
`executeIntroEvent()` queues a `TimedEvent(0, introEventType, params, mission)`,
and when the event finishes `introEventFinished()` calls `accept()`, which
promotes state `1` to `2`. Entering state `2` is the single place that:

- adds the mission to the active list and notifies `TutorialFlow` (tutorials),
- unlocks Rob when `id == Tuner.getMissionToUnlockVisitRob()`,
- records the acceptance timestamp,
- emits `updateCounter(2704, id, 1)` and `updateCounter(2704, 80, 1)`
  (the “accept mission” counters),
- resets reset-able objective counters and runs `logicUpdate(0)` on each
  objective,
- executes the mission's setups (queuing failures for retry), and
- starts the mission's tracking event.

### Objectives and progress

Each objective is evaluated by `Mission.checkForCompletedObjectives`. A mission
completes when **every** objective reports `isCompleted()`. An objective is
complete when it is purchased or `isGoalReached()`. On the transition to
complete, `mObjectiveCompleted` is set, sound FX `34` plays, and the objective
is tracked. In the non-forced form an empty objective list yields
`completed == true` immediately.

`Mission.logicUpdate` polled objectives and re-checks completion. The manager
also routes incoming game events to active missions and no-reset objectives.

### Completion, rewards, and outro events

When `MissionManager.logicUpdate` sees a completed active mission, it:

1. calls `mission.collectRewards()` (state `2 → 3`),
2. runs `executeOutroEvents()`, and
3. if `saveAfterCompleted`, queues `TimedEvent(1500, 4053, null)` (delayed save).

Entering state `3` runs, in order: `consumeObjectives()`, `resetSetups(false)`,
credits `rewardCoins` and `rewardXp`, adds each `rewardItems` pair to the
inventory, and tracks completion. `executeOutroEvents()` fires every mission
event whose selector matches the mission's type (events loaded from `80472`)
or its ID (events from `80473`). Tips increment `HUD.increateTipsCounter()`
instead of touching the HUD mission list. The manager handles at most one
completion or expiry per logic update and returns immediately.

### Duration, expiration, and retry

`hasDuration()` is `mDuration >= 0`, so a negative duration never times out
while a zero duration still expires as soon as `getTimeLeft()` goes negative.
`getTimeLeft()` is `duration - elapsed since acceptance`. On expiry,
`executeExpirationEvent()` runs `TimedEvent(0, expirationEventType, null, mission)`
(or finishes immediately when the type is `-1`) and tracks the expiry. The
finish handler then, for a state-`2` mission, resets objectives and setups and:

- if `canRetry`: returns to state `0`, marks it new, and requests a rescan;
- otherwise: marks it completed and moves to state `4`.

### Deactivation

`deactivate()` from any non-zero state resets setups (forced) and returns to
state `0`; it reports whether the mission was previously active. During
`updateNewMissions`, a mission found in state `2` whose explicit lock became
true is deactivated, and leaving state `2` removes it from the active list
(also notifying `TutorialFlow` for tutorials).

## Objectives, counters, and progress

### Objective record

`MissionObjective` extends `Counter`. `loadData` reads:

| Order | Field | Type | Meaning |
| ---: | --- | --- | --- |
| 1 | `id` | short | Objective ID (referenced by saves and progress events). |
| 2 | `missionID` | short | Owning mission. |
| 3 | `descriptionTextID` | short | Objective description text. |
| 4 | `type` | short | Counter type. |
| 5 | `subType` | short | Counter subtype. |
| 6 | `value` | short | Counter value (target entity/ID). |
| 7 | `goal` | int | Target count. |
| 8 | `resetCounter` | boolean | Whether the counter resets on accept/retry. |
| 9 | `consumed` | boolean | Whether the goal amount is consumed at reward collection. |
| 10 | `unusedBoolean` | boolean | Read and discarded by the port. |
| 11 | `unlockPrice` | int | Price to buy the objective (`-1`/`0` = not buyable). |
| 12 | `unlockCurrency` | short | Currency: `9` = coins, `7` = cash (per the UI). |
| 13 | `relativePrice` | boolean | Price decreases with progress. |
| 14 | `iconAnimationID` | int | Objective icon. |
| 15 | `connectedMissionSetupIDs` | int array | Setups that supply this objective. |

`getUnlockPrice()`: when the price is positive and relative, returns
`max(1, price - counter * price / goal)`; otherwise the raw price.
`MissionObjective.save` appends a purchased boolean only when `unlockPrice > 0`;
`Counter.save` writes the counter as an `int` and, for type `2571` (wait), the
start timestamp as a long.

### Counter matching

A `Counter` matches an incoming event when:

```text
type == eventType  OR  (type == 2582 AND eventType in {2480, 2518})
    AND subType == eventSubType AND value == eventValue
```

`2582` (`buyOrPickup`) therefore accepts pick-up (`2480`) and buy (`2518`)
events with the objective's configured subtype and value. Value `80` is used as
an “any” value at many call sites, but it is not a universal wildcard here; the
objective must be configured with `80` to match it. The single wildcard is
value `-1` when an objective and an objective-progress event are linked by
`getObjectiveByType`, not for ordinary matching.

`isGoalReached()` is `max(goal, 1) <= counter` when `goal > 0`, and
`counter > 0` otherwise. `updateCounter` adds the amount; `getCountLeft()` is
`max(0, goal - counter)`.

### Polled counters

`isManuallyUpdated()` returns false for five types, which the 500 ms
`logicUpdate` timer polls instead of relying on events:

| Type | Name | Polled value |
| ---: | --- | --- |
| `2341` | customize | `1`/`0` whether the active avatar wears the configured item group. |
| `2516` | have | Currency/energy/inventory count by value ID (see below). |
| `2642` | have subtype | Inventory count of a subtype by value ID. |
| `2571` | wait | Whole minutes elapsed since the counter start time. |
| `6048` | complete mission | `1`/`0` whether the mission with the configured value ID is complete; unknown ID returns true. |

For `2516`/`2642`, value IDs `7` cash, `9` coins, `11` stone, `13` food, and
`107` energy read profile resources; other values are inventory item or subtype
counts. When a polled value changes, `logicUpdate` returns `11` (used to refresh
mission update time); reaching the goal returns `7`.

### Buying objectives

The mission window offers a buy button when the objective is incomplete, not
purchased, and its price is positive. `WindowMission.buyObjective` checks the
currency: coins (`9`) or cash (`7`); insufficient funds opens the
out-of-currency window instead. On success it deducts the price and calls
`setPurchased(true)`, which sets the counter to the goal and fires any progress
event for the gain. A purchased objective counts as complete and is exempt from
consumption. A `BuyMissionStepEvent` tracks the spend before the purchase.

### Consumed objectives

`Mission.consumeObjectives()` runs at reward collection. For each objective
flagged `consumed` and not purchased, it removes `goal` units of the value:
IDs `7` cash, `9` coins, `11` stone, `13` food; any other value is treated as an
item ID and removed from inventory. This is separate from the reward list.

### Objective progress events

An `ObjectiveEvent` fires when an objective crosses its configured target.
`updateProgress(amount)` triggers it when `target <= counter` and
`counter - amount < target`, i.e. on the update that reaches or passes the
target. The event queues a `TimedEvent(delay, eventType, parameters)`.

### Counter catalog

The `Counter` constants and additional IDs observed at call sites. Names are
labels from the port, not proof of upstream semantics. [hypothesis: port]

| Type | Name | Type | Name |
| ---: | --- | ---: | --- |
| `-1` | invalid | `2516` | have |
| `78` | killZombieWith | `2518` | buy |
| `90` | killZombieWithCrafted | `2520` | complete |
| `95` | killZombie | `2526` | craft |
| `100` | consume | `2528` | upgrade |
| `112` | clear | `2533` | repairWith |
| `118` | hire | `2541` | completeAchievement |
| `123` | visit | `2546` | tradeCollection |
| `128` | farm | `2553` | clearWith |
| `2291` | cleanUp | `2561` | harvestWith |
| `2341` | customize | `2571` | wait |
| `2476` | outOf | `2582` | buyOrPickup |
| `2480` | pickUp | `2593` | killZombieAt |
| `2482` | pickUpCombo | `2597` | repairAt |
| `2489` | hitZombie | `2642` | haveSubtype |
| `2491` | collect | `2658` | killZombieAtNeighborWith |
| `2493` | construct | `2704` | acceptMission |
| `2495` | repair | `2714` | hitByZombie |
| `2498` | harvest | `2733` | centerCamera |
| `2500` | move | `2885` | connectivity |
| `2504` | sellAdult | `2887` | connectivityReminder |
| `2507` | harvestAt | `2895` | openMission |
| `2513` | clearAt | `2963` | energyFromItem |
| `2971` | alertZombie | `6048` | completeMission (observed) |
| `3287` | combo (observed) | `3860` | set (observed) |
| `3864` | observed | | |

Subtypes (used as the `subType` field, not counter types): `-1` invalid,
`118` hire, `41` spouse, `1926` neighbor, `2514` tutor, `2661` crafted.
Sentinels: `VALUE_ANY = 80`, `VALUE_NONE = -1`, `UPDATE_TIME = 500` ms. `-1`
subtype is the default when a call site passes no subtype.

## Mission setups (spawning prerequisites)

A `MissionSetup` binds a mission to a spawning. `MissionSetup` reads: `id`,
`missionID`, `type`, `amount` (int), an unused boolean, `spawningID`, and
`locationID`.

| Type | Constant | Behavior |
| ---: | --- | --- |
| `2432` | spawn zombie | `spawning.execute(amount, locationID)`. |
| `2436` | spawn item | Same as spawn zombie. |
| `3056` | reset spawning | `spawning.reset()`. |

- `canExecute()` requires one of those types, an existing spawning, and
  `spawning.canExecuteAt(locationID)`.
- `execute()` spawns `amount` (or resets for type `3056`).
- `reset(force)` resets the spawning for `2432`/`2436` unless it is stored
  permanently and `force` is false; for `3056` it re-executes (respawning).

On acceptance, `Mission.executeSetups()` runs every setup for the mission and
queues each failure via `MissionManager.addDelayedMissionSetup`. The delayed
queue is deduplicated by setup ID and retried every logic update. Neighbor
visits call `updateNeighborMissionSetups()`, which queues setups whose
spawning resolves to the current area (with an `1926`→`2514` tutorial-area
special case).

The `Fixes.fixMissionSetups(objective)` repair path uses an objective's
`connectedMissionSetupIDs`: it sums `spawning.calculateAmount()` across the
setups and, if the available spawns are fewer than `goal - counter`, executes
one extra spawn at a time across the connected setups until the objective can
be met. Spawn definitions and location meaning live in the spawning/location
tables; this document keeps their IDs. [hypothesis: port]

## Mission events

Three event roles exist:

- **Intro** — stored on the mission (`introEventType`/`introEventParameters`);
  a delay-0 `TimedEvent` whose completion promotes state `1` to `2`.
- **Outro** — `MissionEvent` records (from `80472` by type and `80473` by ID),
  fired when the mission completes.
- **Expiration** — stored on the mission (`expirationEventType`); a delay-0
  `TimedEvent` run on timeout.

`MissionEvent` layout: `selector` (short), `eventType` (short), `parameters`
(int array), `delay` (int). Its `selector` populates `missionType` when the
record came from the by-type table, or `missionID` when from the by-ID table.
`execute(mission)` queues `TimedEvent(delay, eventType, parameters, mission)`.

`ObjectiveEvent` layout: `missionID` (short), `type` (short), `value` (short),
`target` (int), `eventType` (short), `parameters` (int array), `delay` (int).
It queues a `TimedEvent(delay, eventType, parameters)` with no owner.

Unknown event types are queued but only act when their handler is registered in
`EventManager`.

## HUD and mission windows

- `HUDMissions` owns a vertical stack of mission icons (count derived from
  screen height). `addMission` inserts an icon at the top, recalculates
  positions, and appends the mission to `WindowMissionList` (window `26`);
  see [`WindowMissionList`](../../src/game-ios/com/dchoc/windows/WindowMissionList.java).
  `removeMission` frees its images and removes it from both. An overflow icon
  expands the list.
- `WindowMission` renders a single mission: title, description,
  optional hint, timer, up to three objectives (icon, description, target or
  wait time, completion check, and a buy button), OK/`3`, and Facebook/`4` and
  Twitter/`5` buttons when the mission defines posts. The OK callback closes
  the window and, for mission `2343`, triggers a connection dialog.
- Opening a mission emits `MissionManager.updateCounter(2895, id, 1)`, the
  `openMission` counter.
- `HUD.increateTipsCounter()` advances the tips UI when a tip completes.

## Tracking and notifications

Mission types `2292` and `2399` create a `MissionTrackingEvent` in the
constructor (`2399` sets a type `18`). Tracking actions are started (`2`),
completed (`8`), objective-completed (`17`, with the objective ID), and expired
(`11`). “Accept mission” and “open mission” are counted through
`MissionManager.updateCounter(2704, …)` and `(2895, …)`.

Killing zombies (`type 95`, subtype `-1`, value `80`) also accumulates
`smKilledZombies` and reports it to the `killedzombies` leaderboard. Several
counter types record side timestamps in `MissionManager` used by other
systems: `106` spend, `112` clear, `2495` repair, `2498` harvest, `2518` buy
with value `1911` customize, `2526` craft, `2528` upgrade.

A mission with a `notificationID` can produce a reminder: `Fixes.fixMission`
clamps the mission's objective-update time relative to the notification delay
so the reminder is not immediately overdue after a load.

## Achievements

There are 10 achievements, each with four tier objectives (`bronze` `63`,
`silver` `64`, `gold` `65`, `platinum` `66`) loaded from `80435` and `80436`.

- `Achievement.loadData`: `id`, `titleTextID`, `descriptionTextID`,
  `completedDescriptionTextID`, `iconAnimationID`.
- `AchievementObjective.loadData`: `id`, `achievementID`, `level`, `type`,
  `subType`, `value`, `goal` (int), `rewards` (int-value list). It extends
  `Counter`.
- `getLevel()` reports platinum if the third objective's goal is reached, gold
  if the second, silver if the first, otherwise bronze.
- Counters only advance while `PlayerProfile.getLevel() >=
  Tuner.getLevelToUnlockAchievements()`.
- When a tier goal is reached: `addRewards()` grants the pairs, a
  `TimedEvent(100, 2468)` carrying the achievement is queued, and
  `updateCounter(2541, objectiveId, 1)` plus `(2541, 80, 1)` advance the
  achievement counters.
- Reward IDs: `2095` XP, `9` coins, anything else an item; if an item's type
  is `1911` (avatar) the customization window (`25`) rebuilds its touch areas.
- Progress is reported to Game Center via `reportAchievementCompleted` /
  `reportAchievemenProgress`.
- Achievements are saved and loaded with the mission record but have their own
  count and entries.

## Persistence and save locations

The primary save key is `RMS_MISSIONS`; the normal backup key is
`RMS_MISSIONS_BACKUP`. `Area.saveData()` serializes the current scene and
mission-manager state and writes the current mission record. On a successful
load of the scene plus mission record, `Area.loadData()` copies those bytes to
the scene backup and mission backup keys. If the primary scene/mission load
fails, it attempts the scene backup and mission backup. The manager declares
older/debug backup-name constants (`RMS_MISSION_BACKUP_V1..V3`,
`RMS_MISSIONS_DBG_BACKUP`), but the inspected load path uses
`RMS_MISSIONS_BACKUP`. The `rms.sqlite` shipped with the build
(`game-assets-ios`) is not the PC player's save record. [hypothesis: port]

`MissionManager.save()` writes, in order:

1. UTF data-version string and a short mission count.
2. Each mission: short ID, then `Mission.save` (state byte; for non-inactive
   missions the new/completed/objective-completed flags, an acceptance
   timestamp when the mission has a duration, each objective's short ID and
   `save`, and the last-update timestamp). State `3` stores only the
   survival-tip new flag when the record is a tip; ordinary completed missions
   need only the state byte.
3. Achievement count (byte); each achievement's short ID plus its objective
   tier/counter state.
4. Killed-zombie total (int).
5. Delayed mission-setup short IDs.
6. Active-spawning short ID and long timestamp pairs.
7. Non-reset objective short ID and counter entries.
8. Craft, harvest, upgrade, clear, spend, customize, and repair timestamps
   (longs).

`MissionManager.load()` validates every saved mission, achievement, and
non-reset objective ID against the current tables; a missing ID fails the load
and returns false so the area loader can try its backup. Timestamps and mission
fields are versioned: timestamps are read only after record version `125`,
repair time after `128`, and the compact completed-state form from version
`133`. `Fixes.fixTimestamp` clamps a future timestamp down to the current time.
Timestamps are written as two 32-bit ints (high word then low word). This
format is binary and version-dependent; the JSON definitions are not a save
migration format. [hypothesis: port]

In the PC port, each J2ME record store is one file at
`<data-dir>\files\<store>_data\1`; the default data directory is
`%APPDATA%\ZombieLane-1.4.4\players\<id>` after selecting a profile and can be
rooted elsewhere with `--data-dir` or `-Dzlport.dataDir`. Use
`--no-profile-picker` to open a data directory directly. Writing a record
deletes/recreates its store before writing record `1`. Back up live user data
before experimenting with save logic. [hypothesis: port]

Mission definitions and localized text, by contrast, are shared rather than
saved per profile. The runtime reads the packed mission tables (`r1`) and
language packs (`l0_0`, `l1_0`, ...) from the shared missions directory
`<data-root>\missions` (`%APPDATA%\ZombieLane-1.4.4\missions` on the default
root; relocate with `-Dzlport.ios.missions`) before falling back to the game
bundle, so every profile loads identical missions.

## Custom missions (server-delivered)

Custom missions are fetched by the client and injected **hidden**, so a
holiday/event mission can ship with the delivering server — a separate
repository not documented here — without appearing in the HUD until it is
shown. In SERVER mode the launcher fetches the catalog with `catalog=1` and
writes a per-session cache before `GameEngine` step 80 loads
`AreaHome`/`RMS_MISSIONS`; `MissionManager.init()` installs it from that cache
at step 60 via `CustomMissionsBootstrap.installFromCache()`, and the step-90
`GetSessionStatus` poll refreshes it by calling
`MissionManager.addCustomMission` again. [implemented]

- `MissionManager.addCustomMission(data, objectivesData)` decodes a `Mission`
  and its objectives, replaces any existing mission with the same ID in the
  mission, tips, and active collections, adds it, and requests a rescan.
- `showCustomMission(mission/id)` activates the mission if inactive, and for
  regular missions adds and opens its HUD window.
- `CustomMissions.requestRefresh()` asks the engine (`setGetSessionStatus`) to
  re-fetch the server's status; `CustomMissions.show(id)` makes one visible
  without restarting.

If a save still contains a custom mission that neither the server nor the
last-known-good cache provides, `MissionManager.load()` fails and marks the
load incomplete, and `Area` keeps the downloaded gamefield and disables saving
instead of restoring the default scene.

Custom missions reuse existing counter, item, spawn, and event IDs. Live
publishing injects only the definition, objectives, and text IDs; a mission
using mission setups, progress events, completion events, or an intro/dialogue
event is not supported and must be installed as packed resources instead
(the delivering revival server enforces this at publish time; separate
repository). [hypothesis: port]

The delivering server reserves every original mission, mission-objective,
achievement, and achievement-tier objective ID, so a custom mission must avoid
all of those tables. The original objective IDs `6066` and `6067` exist even
though the original mission maximum is `6065`; neither ID is free. The agreed
convention that the client and the delivering server share is that **all
custom missions start at `10000`** (proposed range `10000..19999`).
[observed in this tree: reservation applied by the revival server, separate repository]

## Worked example: the opening campaign chain

Decoded from the shipped `r1`, the first records form a short tutorial chain.
Configurations shown are the observations used to sanity-check the runtime
rules above. [hypothesis: port]

| ID | Type | Title | Requires | Objectives (type/value/target) |
| ---: | --- | --- | --- | --- |
| `2287` | tutorial | GEARING UP | — | clear / 839 / 1 |
| `2289` | tutorial | SAVE ROB! | 2287 | killZombie / 55 / 3 |
| `2290` | tutorial | PICK UP REWARDS | 2287 | pickUp / 80 / 14; pickUpCombo / 80 / 0 |
| `1` | tutorial | Talk to Rob! | 2290 | clear / 51 / 1 |
| `2291` | mission | GETTING EVEN | 1 | killZombie / 80 / 3 |
| `2293` | mission | RESTORE COMMUNICATION | 2291 | clear / 54 / 1 |

This shows prerequisite gating (state `3` required), tutorial routing to
`TutorialFlow`, and the reuse of value `80` as “any”. Localized text is read
from `l0_0`; editing the definitions changes the strings and IDs but not these
rules. A mission installed into the shared definitions document applies to all
profiles.

## JSON field reference

The root groups are `metadata`, `missionTypes`, `counterTypes`, `missions`,
`unassignedSetups`, `missionEventsById`, `missionEventsByType`,
`objectiveEvents`, and `achievements`. `metadata.resourceIds` records the
source table IDs; `recordCounts` is a consistency check for the extracted
tables; `unmappedCounterTypeIds` lists objective counter types for which the
port has no named counter constant. The exported counter-name map includes
every `Counter.java` type and the additional observed IDs `3860`, `3864`, and
`6048`. These names are labels, not translations or proof of upstream
semantics. [hypothesis: port]

Each mission has `id`, `typeId`/`type`, `texts`, `rewards`, `requirements`,
`duration`, retry/save flags, icon and notification IDs, `intro`,
`expirationEventTypeId`, social-post IDs, linked `objectives` and `setups`,
and its `progressEvents`/`outroEvents`. Text values preserve both `textId`
and resolved `text`. Requirements preserve the required level, prerequisite
mission IDs, and explicit locked flag. Reward item pairs are represented as
`{id, amount}`. Durations preserve the source value and the converted
milliseconds. Unused fields that the port reader consumes are explicitly
named `unused...ReadByPort`; they are retained rather than assigned invented
meanings. [hypothesis: port]

Each objective exposes its `condition` tuple (`counterTypeId`,
`counterType`, `subtypeId`, `valueId`, and target), reset and consumed flags,
purchase `unlock` fields, icon ID, connected setup IDs, and progress events.
Setups expose their IDs, owner mission, type, amount, spawning, and location.
Events preserve their selector/owner, event type, parameters, and delay in
milliseconds. Achievement objects expose localized title/description text
and their tier objectives; each tier contains a counter condition and
reward-pair list. [hypothesis: port]

## Provenance and references

The extraction follows the supplied Java port and iOS packed resources. Keep
numeric IDs and the caveat in the JSON when reusing it; a code name or text
description does not independently establish original-game behavior.
[hypothesis: port]

Relevant implementation in this tree:
[`MissionManager.java`](../../src/game-ios/com/dchoc/idead/missions/MissionManager.java),
[`Mission.java`](../../src/game-ios/com/dchoc/idead/missions/Mission.java),
[`MissionObjective.java`](../../src/game-ios/com/dchoc/idead/missions/MissionObjective.java),
[`MissionSetup.java`](../../src/game-ios/com/dchoc/idead/missions/MissionSetup.java),
[`MissionEvent.java`](../../src/game-ios/com/dchoc/idead/missions/MissionEvent.java),
[`ObjectiveEvent.java`](../../src/game-ios/com/dchoc/idead/missions/ObjectiveEvent.java),
[`Achievement.java`](../../src/game-ios/com/dchoc/idead/missions/Achievement.java),
[`AchievementObjective.java`](../../src/game-ios/com/dchoc/idead/missions/AchievementObjective.java),
[`Counter.java`](../../src/game-ios/com/dchoc/idead/missions/Counter.java),
[`MissionJsonText.java`](../../src/game-ios/com/dchoc/idead/missions/MissionJsonText.java),
[`CustomMissions.java`](../../src/game-ios/com/dchoc/idead/missions/CustomMissions.java),
[`CustomMissionsBootstrap.java`](../../src/game-ios/com/dchoc/idead/missions/CustomMissionsBootstrap.java),
[`GetCustomMissions.java`](../../src/game-ios/com/dchoc/idead/servlets/GetCustomMissions.java),
[`GetSessionStatus.java`](../../src/game-ios/com/dchoc/idead/servlets/GetSessionStatus.java),
[`Fixes.java`](../../src/game-ios/com/dchoc/idead/fixes/Fixes.java),
[`WindowMission.java`](../../src/game-ios/com/dchoc/windows/WindowMission.java),
[`WindowMissionList.java`](../../src/game-ios/com/dchoc/windows/WindowMissionList.java),
[`HUDMissions.java`](../../src/game-ios/com/dchoc/hud/HUDMissions.java),
[`MissionTrackingEvent.java`](../../src/game-ios/com/dchoc/idead/tracking/MissionTrackingEvent.java),
[`ToolkitHelpers.java`](../../src/game-ios/com/dchoc/toolkit/ToolkitHelpers.java),
[`Tuner.java`](../../src/game-ios/com/dchoc/idead/tuner/Tuner.java),
[`Area.java`](../../src/game-ios/com/dchoc/idead/areas/Area.java),
[`GameEngine.java`](../../src/game-ios/com/dchoc/idead/GameEngine.java),
[`Toolkit.java`](../../src/game-ios/com/dchoc/toolkit/Toolkit.java),
[`ServerConnection.java`](../../src/launcher-ios/zlport/launcher/ServerConnection.java),
[`IosMain.java`](../../src/launcher-ios/zlport/launcher/IosMain.java), and
[`RecordStore.java`](../../src/platform-1001/javax/microedition/rms/RecordStore.java).
