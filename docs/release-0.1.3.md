# Alpha 0.1.3 — LOCAL gameplay reports

**Release identity:** `0.1.3` is the official full version. `0.1.3c` was a
development build; the next official version is `0.1.4`. The release is prepared
locally for the maintainer to upload; this document does not assert that a GitHub
release has been published. The [0.1.3c testing handoff](testing-0.1.3c.md) records
earlier tester observations, not an independent audit of production data.

The package, report metadata, and `X-ZL-Game-Version` all identify as `0.1.3`.
The separate report receiver must be deployed with `0.1.3` support and each
tester's individual token registered before distributing the client.

## Client changes

- With renewed explicit opt-in, one ZIP per completed session is posted to
  `https://reports.jorgelio.cl/reports/index.php`, independently of the game
  server. This also works in local/offline mode. Old consent for the game-server destination
  does not authorize uploads to the new domain. Headless launches without
  current consent do not silently opt in.
- ZIPs over 1 MiB are not uploaded. On startup, up to three pending ZIPs are
  retried. A successful HTTP 200 moves the ZIP out of the pending folder, so it
  is not resent; the server must accept repeated IDs idempotently when a reply
  is lost after storage.
- Public uploads require a unique per-tester token in the local
  `report-token.txt`; without it the ZIP stays pending. The token is never
  bundled in the public client or report ZIP.
- The client removes local pending and sent ZIPs older than 30 days on startup.
  If the receiver is unavailable beyond that time, unsent reports expire.
- The privacy notice names the receiver, the data sent and the host's access
  to the connecting IP address. Revoking consent during a session prevents
  that session's report from being sent at shutdown.
- In LOCAL mode, the manual **Probar conexiones** button is available and makes
  exactly one read-only GET to `https://reports.jorgelio.cl/health.php`; it does
  not contact the game server or public status site. In SERVER mode, the button
  makes read-only GET checks for the selected game server,
  `https://zl.jorgelio.cl/status`, its public snapshot, and
  `https://reports.jorgelio.cl/health.php`. It sends no credentials or report.
   These public checks can report unavailable without blocking LOCAL gameplay.

## Verification

`tests/SessionReportSmoke.java` uses a loopback-only fake receiver. It covers
legacy consent, local sessions, failed upload and retry, stable report IDs,
no re-upload after HTTP 200, missing public token, a three-pending-per-start limit, the 1 MiB cap,
revocation, local expiry, and rejection of unsafe endpoint URLs. The normal
client build and launcher help smoke also pass. These automated tests do **not**
  test the public receiver, database, TLS or real gameplay. The tester
subsequently reported one successful live send after key setup; that report is
recorded in the testing handoff, not independently verified here.

`tests/ConnectivityChecksSmoke.java` uses loopback fixtures to verify the
service result classifications, redirect handling, fresh/stale/offline
snapshots, GET-only behavior, and absence of credentials or report data.

From the repository root on Windows, run the tracked test against a fresh
development package (replace the paths as needed):

```powershell
.\tools\build-client.ps1 -Version 0.1.3 -Release -OutputRoot 'C:\Temp\Zombie Lane 0.1.3' -SkipZip
$classes = 'C:\Temp\Zombie Lane 0.1.3\client-overrides'
New-Item -ItemType Directory -Force 'C:\Temp\zl-report-tests' | Out-Null
javac -encoding UTF-8 -cp $classes -d 'C:\Temp\zl-report-tests' tests\SessionReportSmoke.java
java -Dzlport.client.version=0.1.3 -cp "C:\Temp\zl-report-tests;$classes" zlport.launcher.SessionReportSmoke
```

## Deployment gate

Follow [PHP/MySQL receiver plan](report-receiver.md). Confirm that the host
supports PHP 8.4, PDO MySQL, ZipArchive, a private directory outside webroot,
and a scheduled cleanup job. One live send has been reported by the tester;
run a controlled synthetic upload and continue the single-tester test before
wider distribution. The game server at `zl.jorgelio.cl:6767` is a separate
service and must not be used as the public report receiver.

## Tester setup (LOCAL)

1. Extract the entire release ZIP, keeping `runtime`, `lib`, `game-assets-ios`
   and `client-overrides` next to `Run Zombie Lane Alpha.bat`.
2. Put the unique privately supplied `report-token.txt` in the data root,
   normally `%APPDATA%\ZombieLane-1.4.4`, not in the extracted package or profile
   subdirectory. Do not share or reuse another tester's key.
3. Start the launcher, select LOCAL, and explicitly accept diagnostic uploads.
4. Play and close normally. With internet available the session ZIP is uploaded
   to the report website; no local game server needs to be running.
5. On success the ZIP moves from `logs/informes/pendientes` to
   `logs/informes/enviados` under the data root. Connection failures leave it
   pending for a later startup; ZIPs above 1 MiB are not uploaded.

The token is not the status publisher's token, a GitHub credential, or a game
account password. Reports can include action logs, configuration, usernames and
technical machine details; handle them as private diagnostics. Saved games,
passwords and the token file are not report entries. The host can see connecting
IP addresses. Consent can be revoked in the launcher's privacy dialog.
