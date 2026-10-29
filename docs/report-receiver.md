# Report receiver for alpha 0.1.3 (PHP 8.4 + MySQL)

This is the deployment contract and checklist for the independent report host.
The health check at `https://reports.jorgelio.cl/health.php` returned HTTP 200
with body `ok`. On 2026-10-27, the tester reported one successful live send
after registering the tester key; this has not been independently checked
against hosting logs/database. A controlled synthetic upload is still needed
before wider distribution. The client posts to
`https://reports.jorgelio.cl/reports/index.php`, not the game server.

The launcher’s manual connection check uses the separate read-only
`GET https://reports.jorgelio.cl/health.php` endpoint. It must answer `200 ok`
only when TLS, ZIP support, private configuration, database access, and private
storage are ready. This check never posts a ZIP and does not require a tester
token.

The separate private Revival repository contains the PHP receiver source,
schema, cron cleanup, synthetic tests and hosting instructions. A successful
health check does not by itself verify POST uploads.

## Host prerequisites

1. Map `reports.jorgelio.cl` to a TLS-enabled document root. Confirm HTTPS
    certificate validity and direct PHP script routes `POST /reports/index.php`
    and `GET /health.php`. PHP must have PDO MySQL and ZipArchive enabled; no
    URL rewrite is required.
2. Provision a private directory outside every public document root for ZIPs,
    writable by PHP and readable only by the site owner and PHP service account.
    Do not use a publicly reachable `uploads/` path. Keep DB credentials outside
    webroot as well. If the host cannot do this, **do not deploy** this design.
   Keep `private/app.php` and the actual `private/config.php` outside the
   document root, beside each other. The receiver loads `config.php` there by
   default, so shared hosting does not need a PHP environment-variable setting.
3. Create a dedicated MySQL user limited to this application's database.
   Enable a daily cron/hosting scheduled task that can run PHP CLI. Confirm
   that provider backups and access logs follow the same 30-day privacy policy
   or disclose and adjust them before inviting testers.

## Wire contract

The client sends one raw ZIP per session with `Content-Type: application/zip`,
`X-ZL-Consent: alpha`, `X-ZL-Report-Id` (stable across retries),
`X-ZL-Account` (optional; may be empty for retries), and `X-ZL-Game-Version`.
The official build sends `X-ZL-Game-Version: 0.1.3` and records `0.1.3` in
`sesion.json`. Deploy the updated receiver before distributing it: the receiver
accepts `0.1.3`, `0.1.2` and previous development builds `0.1.3a`, `0.1.3b`,
and `0.1.3c`. The next official version `0.1.4` is not yet allowed.
Public uploads also require `X-ZL-Report-Token`: a unique random token per
tester, stored in that tester's local `report-token.txt` under the Zombie Lane
data root (normally `%APPDATA%\ZombieLane-1.4.4`). Generate 10 independent
tokens using a cryptographically secure random source (e.g. 32 random bytes,
hex-encoded); send one privately to each tester. Do not ship keys inside the
public ZIP, reuse one key for everyone, or put them in URLs. A missing key
leaves the report pending locally; loopback development tests may omit it.
No multipart or JSON upload is used. Body size is at most 1 MiB. Reply **HTTP
200 only after durable storage**, including for an already stored report ID;
reject failures with non-200 responses. No redirect is accepted by the client.

## Suggested schema

```sql
CREATE TABLE reports (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  report_key BINARY(32) NOT NULL UNIQUE,
  tester_id TINYINT UNSIGNED NOT NULL,
  filename CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  bytes INT UNSIGNED NOT NULL,
  sha256 BINARY(32) NOT NULL,
  received_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  expires_at DATETIME NOT NULL,
  INDEX (expires_at)
) ENGINE=InnoDB;

CREATE TABLE testers (
  id TINYINT UNSIGNED PRIMARY KEY,
  token_sha256 BINARY(32) NOT NULL UNIQUE,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;
```

`report_key` is `SHA256(X-ZL-Report-Id)` (verify an ASCII identifier of bounded
length first). New clients use a timestamp and a full random UUID for that ID.
`filename` is server-generated random hex, not a client header.
The database need not store account names or IP addresses. This does not stop
the web server or hosting provider from logging source IPs; set their retention
separately. Never expose a list/download endpoint to the public Internet.

## Receiver implementation checklist

1. Require POST, HTTPS and the exact consent/content-type headers. Verify
   the tester token using its SHA-256 hash in `testers` and reject
   unknown/disabled tokens (403). Only store token hashes in MySQL and never
   log the raw token. Apply per-tester quotas even for valid tokens. A leaked
   key can be revoked without affecting the other nine testers.
2. Configure the web server to cap request bodies at 1 MiB (plus a small margin if the
   host requires it), and independently stream PHP input to a temporary private
   file with a hard 1 MiB limit. Reject oversized/empty requests (413/400).
   Disable request-body logging. `X-ZL-Consent` is only a client signal, **not
   authentication**.
3. Validate the ZIP container with ZipArchive, limit entry count, total
   uncompressed size and entry names (no absolute paths, `..`, or links). Never
   extract archives on the web server just to validate them. Reject malformed
   archives. Keep uploaded ZIPs quarantined outside the webroot.
4. Derive the unique report key from the bounded report ID. For a duplicate,
   compare body SHA-256 with the stored SHA-256: same ID+body returns 200 without
   storing again; same ID with different body is a conflict (409). Use a unique
   MySQL constraint for concurrency, not a SELECT-then-INSERT race.
5. Allocate a random private filename, atomically move the validated ZIP into
   place, insert its metadata with `expires_at = UTC_TIMESTAMP() + INTERVAL 30
   DAY`, and return 200 only when both file and row are committed. On a DB
   error remove the newly placed file. Reconcile orphan files after crashes;
   never return 200 if storage failed.
6. Apply server-level request-rate limits and storage quotas; public headers
   can be spoofed and client-side limits do not protect this endpoint. For a
   ten-person test, provision only the expected quota plus reasonable headroom.
   Return generic errors, no stack traces or file paths. Disable directory
   listing, public downloads and unnecessary CORS.
7. Schedule a daily cleanup that selects expired rows, deletes the matching
   files and metadata, and also removes stale temporary/orphan ZIPs. Test it
   against synthetic records older than 30 days. Ensure web access logs, DB
   backups and host backups do not silently retain identifiable data longer
   than the promised period.

## Safe validation before testers

Use a synthetic ZIP with no personal data. Verify first upload returns 200,
identical replay returns 200 and leaves one row/file, different bytes with the
 same ID return 409, and oversize/malformed bodies are rejected. Check requests
 without a valid tester token return 403, and that disabling one
tester's key only blocks that tester. Check per-tester rate and storage limits.
 Check that HTTP is redirected to HTTPS **before uploading**; the Java client posts to HTTPS
directly and does not follow redirects. Verify the saved file is unreachable by
URL, cleanup removes old rows/files, and the host can handle 10 simultaneous
small uploads. Only then run one consenting local game session and confirm a
single report is received. If any prerequisite is unavailable, pause rollout.
