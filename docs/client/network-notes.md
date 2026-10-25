# Network notes

**Applies to:** the `0.1.2` client tree; server-side roles are flagged separately. Last reviewed: 2026-10-25.


These notes describe the Java port only; they do not establish original-game network behavior. [hypothesis: port]

## Tuner constants

The values below are present in the port source and in `game-ios.jar` bytecode. [hypothesis: port]

| Constant | Type | Value |
|---|---|---:|
| `DEBUG_SERVLET_PARAMETERS` | `boolean` | `false` |
| `DEBUG_SERVLET_SIGNATURES` | `boolean` | `false` |
| `CONNECTION_RETRY_TIME` | `int` | `45000` |
| `INVITE_RETRY_TIME` | `int` | `30000` |

The meaning and units of the retry-time constants are not established here. [hypothesis: port]

## Stubbed methods and references

`ServerSocketMessage` has 15 methods that throw `UnsupportedOperationException`: `getBytes(): byte[]`, `getSize(): int`, `readBoolean(): boolean`, `readByte(): byte`, `readInt(): int`, `readShort(): short`, `readUTF(): String`, `readUnsignedByte(): int`, `readUnsignedShort(): int`, `skip(int): void`, `writeBoolean(boolean): void`, `writeByte(int): void`, `writeInt(int): void`, `writeShort(int): void`, and `writeUTF(String): void`. [hypothesis: port]

`IServerSocketConnection` declares `getMessage(): ServerSocketMessage` and `sendMessage(ServerSocketMessage): boolean`; no in-tree calls to the message read/write methods or implementation of that interface were found in `src/`. [hypothesis: port]

`ServerBundle` has two stubbed callbacks: `percentageDataReceived(int): void` and `responseCodeReceived(int): void`. It also implements `responseReceived(HTTPResponse)`. [hypothesis: port]

`ServerAssetLoader` constructs `ServerBundle` instances and calls `loadBundle()`; `Animation` requests bundles through `ServerAssetLoader.requestBundle()`. These are port asset-loading references, not evidence of a working server protocol. [hypothesis: port]

`ServletRequest` also defines `percentageDataReceived(int)` and `responseCodeReceived(int)`; the source search did not identify their dispatch call sites. [hypothesis: port]

## Observed behavior without a server

The source tree still contains
[`OfflineNetwork.java`](../../src/launcher/zlport/launcher/OfflineNetwork.java),
a URL handler in `zlport.launcher` that throws `ConnectException` for HTTP and
HTTPS requests, but `Launcher.run()` no longer installs it, so normal runs use
the source implementations of the network classes. [hypothesis: port]

## HTTP service used by the port

The game service is HTTP, not the socket-message interface documented above.
`ServletRequest.execute()` builds a request to
`SettingsWrapper.getServerURL() + serviceName`, puts common parameters in the
request body, and appends `sig`. The transport sends requests with a body as
`POST application/x-www-form-urlencoded`; asset requests have no body and use
`GET`. [hypothesis: port]

The `1.4.4` PC assets' `Info.plist.properties` select `ServerType=PROD`,
`ServerAssetVersion=1_4_5`, and `ServerAssetsTestEnvironment=false`. In this
configuration, the source resolves the game-service base to
`http://idead.dchoc.com/dead/` and the asset base to
`http://dcassets2-a.cdngc.net/idead/1_4_5/`. These are historical URLs from the
port's bundled configuration, not a recommendation to rely on those hosts.
[hypothesis: port]

Every servlet request adds `ver` (the configured asset version) and
`platform=3`. Most also add `dcgid` when a user ID is available. The parameter
pairs are sorted by key before serialization; an empty value is sent as a
bare key. The client appends an MD5 `sig`: when a session ID exists it hashes
the serialized parameters, session ID, and a hard-coded suffix; without a
session ID it hashes only that suffix. Some servlet constructors enable
percent-escaping of the whole body; the default is disabled. [hypothesis: port]
The MD5 `sig` is legacy protocol compatibility, not a security control.

### Startup sequence

During game load, `GameEngine` initializes the player profile and sends
`POST /dead/CreateNewSession` (using the configured base URL). It sends:

| Parameter | Source / purpose |
|---|---|
| `pid=3` | Platform identifier in this servlet |
| `puid`, `token` | Both populated from the port's UID, or empty |
| `dcgid` | Existing user ID, if present |
| `tz` | Time-zone offset in whole hours |
| `lang` | Selected language code |
| `country` | Country code, if available |
| `ver`, `platform`, `sig` | Common servlet fields |

The XML response parser looks for `dcgId` and `sessionId`. A returned
`dcgId` is saved as the player ID and schedules the account request; the
`sessionId` is retained for later signatures. [hypothesis: port]

After session creation, the game can request `GetAccountInformation`, followed
by `GetTimers`. `GetAccountInformation` parses neighbor, gift, action, platform,
health, friend-code, option, and daily-reward data from XML; on completion it
schedules `GetTimers`, which reads countdowns and hire/gift state. A successful
account response can also trigger `CollectDailyRewards`. A failed connection
does not produce a usable XML response. [hypothesis: port]

The game retries session creation after approximately 45 seconds while
disconnected. This comes from `GameEngine`'s update loop; the units are
milliseconds there. [hypothesis: port]

### Other observed service operations

These service names and client-sent fields are present in the port. This list
is an inventory of calls, not a complete server contract; server-side
validation, persistence rules, and response schemas are not established by the
client sources alone.

| Service | Additional client-sent fields observed |
|---|---|
| `GetAccountInformation` | None |
| `GetSessionStatus` | `mission_revision`, `show_revision`, `mailbox_revision`; the live poll for this revision cycle. |
| `GetTimers` | None |
| `SetOption` | `options` |
| `GetAccountSave` | `dcgid`. Restore request: the parser reads the requested account's `avatar`, `gamefield`, `missions` and `area` as XML with the game's Base64. |
| `UpdateAccountInformation` | Optional Base64 `avatar`, `gamefield`, `missions`; `area` (`home`, `circus`, `downtown`, `crashsite`, `cure`) whenever `gamefield` is sent |
| `AddNeighborRequest` | `neighbors` |
| `VisitNeighbor` | `neighbor_dcgid` |
| `AddNeighborActions` | `neighbor_dcgid`, `actions` |
| `AcceptNeighborActions` | `neighbor_dcgid`; optional `cancel` |
| `HireNeighbor` | `neighbors` |
| `SendGift` | `neighbors`, `gift` |
| `AcceptGift` | `gifts`; optional `cancel` |
| `CollectDailyRewards` | No additional field; the reward day is tracked client-side |
| `BindPlatformAccount` | `pid`, `puid`, `name` |
| `UpdateDeviceToken` | `pid`, `device_token`, `udid` |
| `GetCustomMissions` | Optional `revision`. Legacy mission-only poll: returns `<revision>N</revision>` plus every published custom mission as `<mission><definition>…</definition><objectives>…</objectives></mission>` (Base64 binary), and one-time `<show>ID</show>` entries. When the sent revision matches and no show is pending, only the revision is returned. |
| `PublishCustomMission` | `mission` (numeric id), `definition` (Base64 mission record), `objectives` (Base64 objective records). |
| `ShowCustomMission` | `mission_id`. Queues a one-time show request delivered on the next `GetSessionStatus` or legacy `GetCustomMissions` poll. |

The `GetSessionStatus`, `GetAccountSave` and custom-mission operations are
implemented in the revival server (separate repository).

The `avatar`, `gamefield` and `missions` payloads are the
same byte arrays the client writes to its local stores: `Area.saveData()`/`loadData()`
pass `SceneLoader.saveCurrent()` (the area RMS record) and
`MissionManager.save()` (`RMS_MISSIONS`) both to disk and to
`UpdateAccountInformation` ([Area.java](../../src/game-ios/com/dchoc/idead/areas/Area.java),
lines 140-157 and 85-107), and `avatar` is the `RMS_PROFILE` record
(`GameEngine.savePlayerProfile()`, lines 889-898). [hypothesis: port]

**Encoding warning:** these fields do not use RFC 4648 Base64. The game codec
at [`Base64.java`](../../src/game-ios/com/dchoc/idead/network/Base64.java) uses the
alphabet `...0123456789_-`, `*` for `AA`, `|` for `AAA`/padding, and spaces as
`AA` on decode. Standard Base64 decoders reject the `*` character.

### Mailbox gifts (SendGift / AcceptGift): derived wire format

The responses below are derived only from the client servlet parsers
([`SendGift.java`](../../src/game-ios/com/dchoc/idead/servlets/SendGift.java),
[`AcceptGift.java`](../../src/game-ios/com/dchoc/idead/servlets/AcceptGift.java)
and the `gift` handling in
[`GetAccountInformation.java`](../../src/game-ios/com/dchoc/idead/servlets/GetAccountInformation.java)
and
[`GetTimers.java`](../../src/game-ios/com/dchoc/idead/servlets/GetTimers.java)).
Claims about the wire format are therefore [hypothesis: port]; a matching
mailbox backend is implemented in the revival server (separate repository).

Requests. The client-sent fields are those in the service table above:

- `SendGift`: `neighbors` holds exactly one recipient account id (the parser
  requires the `success` response text to equal it) and `gift` a numeric gift
  item id. The item id comes from the sending slot in the client
  (`NeighborProfile` gift slots).
- `AcceptGift`: `gifts` holds `itemId,senderId` and a bare `cancel` parameter
  marks a decline. The client builds the request from the mailbox gift shown in
  the social window; it is guarded client-side against repeating an acceptance.

Responses:

- `SendGift` → `<response><success>RECIPIENT_ID</success></response>`. The
  parser compares the `success` text to the sent neighbor id, so the service
  must return the normalized recipient id.
- `AcceptGift` → `<response><success>ITEM_ID,SENDER_ID</success></response>`.
  The parser requires the text to start with the gift item id and end with the
  sender id.
- `GetAccountInformation` lists the request account's pending gifts as
  `<gift><id>RECORD</id><sender_dcgId>SENDER</sender_dcgId><item_id>ITEM</item_id><receiver_dcgId>RECIPIENT</receiver_dcgId><amount>1</amount></gift>`
  elements after the `<neighbor>` blocks. The client resolves the item id
  through `ItemManager.getGift` and deduplicates by the record `id` (a
  server-assigned long parsed with `ToolkitHelpers.parseLong`), so record ids
  must stay stable across restarts; accepted/declined/expired gifts stop being
  listed, otherwise the client would show them again as pending.
- `GetTimers` lists the request account's outgoing gifts still in the resend
  cooldown as
  `<gift><receiver_dcgId>RECIPIENT</receiver_dcgId><item_id>ITEM</item_id><countdown>SECONDS</countdown></gift>`
  elements after `<general>`. The parser resets every outgoing gift slot before
  parsing and re-applies status 4 (`STATUS_SENT`) plus a future timestamp only
  for listed entries, so omitting an entry re-opens that send slot. `countdown`
  is whole seconds. [hypothesis: port]

The client applies the gift reward itself after the `AcceptGift` success
response: `HUDItemAcceptGift.addGift` adds the item to the local profile; the
server response only confirms the record transition. [hypothesis: port]

### Remote asset requests

For HD graphics, `ServerAssetLoader` reads bundle IDs and versions from an
embedded resource and downloads the missing files individually. In the
production configuration above, the URL shape is
`http://dcassets2-a.cdngc.net/idead/1_4_5/bundle_<id>/<bundle-version>/<file>`.
The requested files are numbered PNGs followed by a file named `0`; successful
responses are cached locally. The bundles are separate from the player/account
API, so replacing the API alone does not satisfy asset loads. [hypothesis: port]

### Session status poll and live mailbox updates

The client polls `GetSessionStatus` every three seconds while a game session is
active. It sends the three revisions it already holds and expects
`<online>1</online>`, `<server_time>`, `<protocol>1</protocol>`, the current
`mission_revision`, `show_revision` and `mailbox_revision`, plus payloads only
for sections whose revision changed:

- Custom missions are sent as the same `<mission>` payloads as
  `GetCustomMissions` when `mission_revision` is stale.
- Mission shows are sent as `<show>ID</show>` entries above
  `show_revision`; shows already acknowledged by this client are not re-sent.
- When `mailbox_revision` is stale, the response includes the complete pending
  mailbox snapshot for that account: `<gift>` entries and neighbor-help
  `<action><id>…</id><neighbor_dcgid>…</neighbor_dcgid><item_id>…</item_id><updated_health>…</updated_health></action>`
  entries. The client reconciles gifts and unaccepted help commands against
  this snapshot.

The legacy `GetCustomMissions` endpoint remains available for older clients
and for the fallback path. If `GetSessionStatus` returns HTTP 404, the client
marks it unsupported and uses the legacy mission-only poll; gifts and help
updates then require a current server. [hypothesis: port]

Neighbor help uses a record kind `help` with payload
`itemId,action,expiresAt`. `AddNeighborActions` sends it, the recipient sees
it as an `<action>` in `GetSessionStatus` (and in startup
`GetAccountInformation`), and `AcceptNeighborActions` resolves the pending
records and returns only the ids it actually resolved. Pending help expires at
the client-supplied timestamp and is capped per recipient. Hiring remains
unimplemented client-side. [hypothesis: port]

## What the client establishes, and what it does not

The source shows per-account HTTP operations and shared neighbor/gift features;
it does not expose a documented multiplayer match protocol. The declared
`IServerSocketConnection` and `ServerSocketMessage` API is stubbed in this port
and has no in-tree implementation, so it is not a usable transport.
[hypothesis: port]

The servlet inventory above is an inventory of calls the client makes; it does
not define complete XML response schemas or backend semantics. Those must be
discovered from runtime traffic or additional authoritative material before a
replacement service can be considered fully compatible with this client. The
client source treats HTTP `200` as success in the servlet response handlers,
matching the transport and `HTTPResponse.RESPONSE_CODE_OK`; previously several
handlers checked for `1`. [hypothesis: port]

The revival server is a separate component that is not part of this
repository; its implementation and deployment details are not documented here.
