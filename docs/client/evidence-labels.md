# Evidence labels

**Applies to:** all `0.1.x` documentation; the label set is release-independent. Last reviewed: 2026-10-25.


This file defines the labels used for claims about the original game, the
supplied builds, and this revival. It replaces the provenance-label list that
was previously kept in [sources.md](./sources.md). Use the most specific label
the evidence supports.

## Labels

- `[verified: play]` - observed by the maintainer in a running game and
  recorded in a dated session log kept in the revival repository. The dated log
  entry is part of the evidence. Do not use this label for code reads, smoke
  tests, or second-hand reports.
- `[verified: docs]` - supported by official documentation only: first-party
  game, platform, or publisher documentation. Repository notes, fan pages, and
  community wikis are not official documentation.
- `[verified: clean-apk]` - reserved for a claim checked against a clean APK
  baseline. No clean APK baseline has been obtained, so this label is not in
  use yet.
- `[code-read: <source>]` - read from decompiled code or bytecode, or from a
  supplied build's files; not run. Name the source, for example
  `[code-read: flash-decompile]`, `[code-read: port-decompile]`, or
  `[code-read: ipa-decompile]`. A code read can establish what the code says,
  not what the game did at runtime.
- `[hypothesis: <source>]` - an inference. Sources are `IPA`, `port`, `Flash`,
  and `Android`; a combination may be written with ` + `. Port and IPA material
  stays `[hypothesis]` unless it fits one of the verified labels.
- `[implemented]` - present in this repository's own client or launcher code.
  For the revival server, write "implemented in the revival server (separate
  repository)" instead. Do not use for claims about the original game or the
  supplied builds.
- `[smoke-tested]` - covered by a recorded test or smoke-test run. The test
  harnesses and full runs are kept in the revival repository; record the
  checks performed. Do not use for claims about the original game or the
  supplied builds.
- `[not exercised in a live session]` - legacy qualifier for revival code that
  is implemented but was not confirmed by a live gameplay session. It is not a
  provenance or evidence label.

## Cross-source matches

A value or design that appears in two unverified sources is evidence of design
similarity, not verification. Each claim keeps its own label and status; do not
promote it because another unverified source agrees. This replaces the former
`[corroborated: A + B]` label, which is withdrawn.

## Legacy labels

| Legacy label | Current handling |
| --- | --- |
| `[observed: Flash]` | Replaced by `[code-read: flash-decompile]` in the files migrated on 2026-10-22. It meant a static read of the supplied Flash build, never a play observation. |
| `[observed: port]` | Not migrated mechanically. It was used for both runtime observations and source reads, so a single replacement would change the meaning of some claims. |
| `[observed: docs]` | Not migrated. It referred to repository documentation, not official documentation, so `[verified: docs]` would change its meaning. |
| `[observed: Wayback]` and dated variants | Not migrated. Web-archive captures are not covered by the current labels. |
| `[observed: Web]` | Not migrated. Same reason as the Wayback labels. |
| `[corroborated: A + B]` | Withdrawn. Two unverified sources agreeing do not corroborate a claim; describe the match as design similarity and keep each claim's own label. |

## Rules for use

- Label claims about the original game or the supplied builds with an evidence
  label; label this repository's own client and launcher work with
  `[implemented]` or `[smoke-tested]`.
- Do not use a stronger label than the evidence allows. A decompiled build, a
  port, or an archive capture stays a code read or a hypothesis until it is
  checked against a verified source.
- Do not record credentials, tokens, keys, account identifiers, or session data
  in evidence notes.
