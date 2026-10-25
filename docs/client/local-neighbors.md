# Local-profile neighbors

**Applies to:** the `0.1.2` client tree; the flow sections below describe planned local behavior. Last reviewed: 2026-10-25.


This document records neighbor behavior visible in the supplied Java port,
what the revival implements today, and proposed local-only behavior. The
revival targets the Java port and the local server; port observations are
**[hypothesis: port]**, not verified facts about the original game. Local-profile
**listing** is implemented (see [revival-implementations.md](./revival-implementations.md));
the visit, gift, hire and help flows below remain a design note in this
repository's scope.

## Goal for the current development stage

Treat every other local player profile as an available neighbor automatically.
There should be no external account, Facebook/Game Center connection, friend
code, invitation, or custom server requirement for the local experience.
Classic neighbor activities should work between profiles on the same
installation, using the same gameplay rules and UI where practical.

The active profile must never be its own neighbor. A roster should be derived
from all other valid local profiles, so creating another profile makes it
available as a neighbor without a separate add/accept flow. Keep each profile's
home and progression isolated; the social system connects those saves rather
than merging them.

## What the Java port does

### Profile and neighbor model

The PC launcher stores profiles below `players/<15-digit-id>/`, with
`profile.properties` containing an ID and display name. It selects one profile
directory as the game's data root. This provides local identities, but the
launcher does not turn the other profile directories into neighbors.

In the game code, `NeighborProfile` holds a neighbor's user ID, name, platform,
level/avatar data, action allowance, pending commands, gifts, and hired state.
`WindowSocial` maintains the neighbor list and the mailbox. In the normal
account flow, `GetAccountInformation` parses server-provided neighbors, avatar
data, actions, and gifts and adds them to that UI. The source also has a
special tutorial/robbery neighbor (type `2514`); it is separate from ordinary
player neighbors and should not be confused with the local-profile roster.

### Visiting and helping

Selecting a neighbor travels to a neighbor area. `VisitNeighbor` sends the
neighbor ID and expects avatar and gamefield data, remaining actions, and
optional visit rewards. `AreaNeighbor` loads that data, sets the current
neighbor, updates neighbor missions, and awards visit rewards when the response
includes them.

While visiting, player actions spend the neighbor's action allowance instead
of the visitor's home energy. Farming, looting, and combat can create help
actions for the visited profile. The port sends those actions with
`AddNeighborActions`; at home, the owner can accept or decline them. Accepted
commands are executed by the neighbor character in the owner's scene. Thus
help is an inbox/approval workflow, not an immediate direct edit to the
owner's save.

Action counts, replenishment, and visit rewards are partly server/timer-driven.
The code has a five-action allowance for the special robbery profile and a
24-hour interval in that profile's local save logic, but those values must not
be assumed to define every ordinary neighbor's online rules.

### Gifts and hiring

Neighbors expose a set of gift items. `SendGift` sends a gift to a neighbor;
received gifts appear in the mailbox and can be accepted or declined with
`AcceptGift`. Gift status and cooldowns are also refreshed by `GetTimers`.

The neighbor UI can hire a neighbor to help at home. Hire availability,
active hired status, and countdowns are read from server timers; the port
tracks the hire action and updates the hired state after success.

### Why this is not already local

The invite, visit, action, gift, hire, account, and timer flows are servlet
requests tied to server responses. The local profile picker is a separate
launcher feature. Merely populating `WindowSocial` from profile metadata would
not make visits or the mailbox work: those paths also expect remote gamefield
data, action acknowledgements, gift records, and timers.

The port's network documentation describes unresolved protocol/runtime gaps:
see [network notes](./network-notes.md). The neighboring code references below
are useful implementation evidence, not a working local social backend.

## Proposed local behavior

### Roster and identity

- Build the ordinary-neighbor roster from all valid local profile metadata,
  excluding the active profile.
- Use each profile's stable ID as its social identity and its metadata name
  as the display name. Show an ID or other disambiguator when names collide.
- Make these neighbors available immediately. Do not show server-oriented
  invite, friend-code, or acceptance steps for local profiles.
- Refresh the roster when profiles are created or renamed. Missing, malformed,
  or inaccessible profile data should be reported rather than silently
  becoming an empty neighbor.
- Keep the special tutorial/robbery character separate from this roster.

### Activities to support

| Activity | Local equivalent |
| --- | --- |
| Visit | Load the selected profile's saved home into a neighbor visit without switching or overwriting the active profile's save. Return to the visitor's home normally. |
| Visit rewards and action allowance | Apply the game's configured limits and cooldowns consistently; persist timestamps so restarting the app does not reset them. Keep these values configurable during development rather than hard-coding server assumptions from the Java port. |
| Help actions | Record actions in the visited profile's mailbox. Let its owner accept or decline them; accepted actions should use the existing neighbor-character execution path. |
| Gifts | Record outgoing gifts as incoming mailbox items for the recipient profile. Support accept/decline, status, and cooldowns, with each gift applied at most once. |
| Hiring | Preserve the existing timed hire behavior and track the active hire and available hires in the profile that owns the home being played. |
| Neighbor list and mailbox | Reuse the social screens and item states where possible, but replace remote loading and servlet success responses with local operations. |

### Persistence and implementation boundary

Keep each profile's game save in its own profile directory. Store social state
with the profile it belongs to: received help and gifts belong to the recipient;
visit cooldowns and outgoing-operation state belong to the acting profile.
When a social action changes two profiles, apply it as a durable, idempotent
operation so a restart cannot duplicate or lose the transfer. Never load one
profile simultaneously as both the active player and a neighbor.

Put the local implementation behind a social-operation boundary that covers
roster lookup, neighbor visits, help actions, gifts, hire status, and timers.
The current port's servlet classes are evidence of those operations, but local
behavior should not depend on fake HTTP responses. Keeping the UI-facing
operations independent of storage will also leave room to replace the local
backend with a real server later.

All local profiles on one installation can be connected by this design.
Cross-device neighbors, cloud synchronization, and conflict resolution require
a server and are out of scope for this stage.

## Source map

The following paths describe the Java port only **[hypothesis: port]**:

- Profile discovery: `src/launcher-ios/zlport/launcher/LocalProfiles.java`
- Neighbor list, mailbox, and invitation UI: `src/game-ios/com/dchoc/windows/WindowSocial.java`
- Neighbor state and gifts: `src/game-ios/com/dchoc/idead/neighbors/NeighborProfile.java`,
  `src/game-ios/com/dchoc/idead/neighbors/NeighborGift.java`
- Visits: `src/game-ios/com/dchoc/idead/areas/AreaNeighbor.java`,
  `src/game-ios/com/dchoc/idead/servlets/VisitNeighbor.java`
- Help actions: `src/game-ios/com/dchoc/idead/servlets/AddNeighborActions.java`,
  `src/game-ios/com/dchoc/idead/servlets/AcceptNeighborActionsRequest.java`,
  `src/game-ios/com/dchoc/idead/characters/NeighborCharacter.java`
- Gifts and hire: `src/game-ios/com/dchoc/idead/servlets/SendGift.java`,
  `src/game-ios/com/dchoc/idead/servlets/AcceptGift.java`,
  `src/game-ios/com/dchoc/idead/servlets/HireNeighbor.java`,
  `src/game-ios/com/dchoc/idead/servlets/GetTimers.java`
