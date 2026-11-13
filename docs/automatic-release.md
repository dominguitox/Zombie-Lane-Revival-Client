# Automated Windows releases

The release workflow runs when a `vMAJOR.MINOR.PATCH` tag is pushed (an
optional `-suffix` marks the release as a prerelease). It builds the tagged
commit, verifies the package and runs the existing loopback Java smoke tests.
Only after those steps pass does it create a GitHub release with the ZIP,
`SHA256SUMS.txt` and automatically generated release notes. Review the generated
notes and add any version-specific installation or privacy instructions.

## One-time setup

1. Register a dedicated, trusted Windows x64 self-hosted GitHub Actions runner
   for this repository with the custom label `zl-client-release`. Keep it online
   for releases. Do not run untrusted pull request code on this machine.
2. Install PowerShell 7 (`pwsh`), Git, GitHub CLI (`gh`), and a JDK 21 or newer
   with `javac`, `javap`, and `java` on the runner service account's PATH.
3. Place the separately supplied asset tree on the runner, including
   `runtime/bin/java.exe`, `lib/` and `game-assets-ios/` as described in the
   main README. Grant the runner service account read access. The runner's temp
   directory needs enough space for the assembled package and ZIP. Its contents
   are not automatically removed by this workflow.
4. In GitHub repository settings, set the Actions variable `CLIENT_ASSETS_ROOT`
   to the absolute path of that tree. Allow Actions to create releases using
   the workflow's `contents: write` permission. No personal token is needed.
   Protect release tags and restrict who can push them: the tagged code runs on
   the self-hosted runner with release permissions.

## Releasing

Update `SessionReport.RELEASE_VERSION` and any version-specific documentation,
merge the changes into `main`, then create and push a new tag such as `v0.1.4`.
The workflow checks that the tag matches the source version and points to a
commit reachable from `main`. It will not create a release when the build or
tests fail. Existing releases are not overwritten. If publishing fails, inspect
the release and its assets before retrying: GitHub CLI can create a release
before a later asset upload fails. The build script performs package allowlist,
stub and headless launcher checks; it does not test interactive gameplay.

## Test roadmap

Java supports automated tests through libraries such as JUnit 5. The current
project uses standalone `main`-based smoke tests, which already return a failing
exit code on assertions and require no test framework. The release job runs both
with the fixed `0.1.3` test version expected by their current assertions; this
does not validate the next release's report version at runtime.

1. Add a repeatable test command and run the existing headless tests in CI on
   trusted branches, then adapt version assertions to test the actual version.
2. Introduce JUnit 5 with a pinned build tool and dependencies. Migrate the
   existing fixtures incrementally and publish JUnit XML results; keep loopback
   networking and temporary directories isolated.
3. Add unit tests for version selection, launcher validation, profile settings,
   consent and report retention, followed by integration tests for local saves
   and client/server contracts with fake services.
4. Add a Windows package smoke test and a documented manual gameplay checklist
   covering startup, rendering, audio, save/reload and both game modes. Run
   automated checks for pull requests on a separate hosted or disposable runner;
   never expose the dedicated release runner or private assets to untrusted code.
