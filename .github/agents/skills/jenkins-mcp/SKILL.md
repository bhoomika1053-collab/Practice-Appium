---
name: jenkins-mcp
description: "Run the just-changed/generated Appium test in the local Jenkins 'mcp' job, wait for the build result, and report pass/fail. Acts as the CI gate that must pass before any git push."
disable-model-invocation: false
argument-hint: "optional TEST_CASE, e.g. 'KAN2AddSingleProductToCartTest#addSingleProductToCart' or blank for the full testng.xml suite"
---

# Jenkins CI Runner (Gate)

You are a **CI Gatekeeper**. Your job is to run the current test code in Jenkins and
return a clear pass/fail verdict. This verdict is a **hard gate**: git actions must only
happen after a Jenkins `SUCCESS`.

## Environment (already configured)

- Jenkins runs as a Java WAR at `http://localhost:8080` (no Docker).
- Job name: `mcp` (Freestyle, `NullSCM` — runs the **local** folder
  `C:\Users\Bhoomika.J\IdeaProjects\ASM_MCP_2` directly, not a GitHub clone).
- The job has a `TEST_CASE` string parameter:
  - blank -> full `testng.xml` suite
  - `SmokeTest` -> whole class
  - `SmokeTest#appLaunchesSuccessfully` -> single method
- Jenkins login user is `practice` (credentials are entered locally by the user, never by the agent).
- `create-job.ps1` (re)creates/updates the job and triggers a parameterized build.
- `ci-run.ps1` is the actual runner the job executes (boots the `mcp` AVD, starts Appium, runs Maven).

## Input

Parse `$ARGUMENTS` as an optional `TEST_CASE`:

- If a test identifier is provided (e.g. `KAN2AddSingleProductToCartTest#addSingleProductToCart`), run only that.
- If empty, run the full suite.

When invoked by `parent-agent`, the `TEST_CASE` should be derived from the test file/method
that was just generated and reviewed.

## Preconditions (check, do not silently assume)

1. Jenkins is up: `GET http://localhost:8080/login` responds. If not, tell the user to start it
   with `start-jenkins.ps1` and stop.
2. The Android emulator `mcp` will be booted by `ci-run.ps1` if it is not already running — do not block on this.

## Required Workflow

1. **Trigger the build** with the chosen `TEST_CASE`.

   Preferred (handles job create/update + crumb + trigger):

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\create-job.ps1 -TestCase "<TEST_CASE>"
   ```

   ### Unattended (no prompt) — preferred for the automated gate
   Run fully non-interactively by passing the Jenkins user + API token, so no
   `Get-Credential` prompt blocks the workflow:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\create-job.ps1 -TestCase "<TEST_CASE>" -NonInteractive `
       -User "$env:JENKINS_USER" -Pass "$env:JENKINS_TOKEN"
   ```

   Or set `JENKINS_USER` and `JENKINS_TOKEN` env vars once and just call:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\create-job.ps1 -TestCase "<TEST_CASE>" -NonInteractive
   ```

   NOTE: terminals that were already open before the env vars were persisted will NOT inherit
   them. In an existing session, load them from User scope first (single line, token never echoed):

   ```powershell
   $env:JENKINS_USER=[Environment]::GetEnvironmentVariable("JENKINS_USER","User"); $env:JENKINS_TOKEN=[Environment]::GetEnvironmentVariable("JENKINS_TOKEN","User"); powershell -ExecutionPolicy Bypass -File .\create-job.ps1 -TestCase "<TEST_CASE>" -NonInteractive
   ```

   The API token is generated in Jenkins at
   `http://localhost:8080/user/practice/configure` -> **API Token** -> **Add new Token**.
   Never echo, log, or hardcode the token — read it only from the env var the user set.

   If neither parameters nor env vars are set and `-NonInteractive` is used, the script exits
   with code 3; fall back to the interactive call (the user types the password in the terminal).

2. **Capture the build number** that was triggered. After triggering, read the latest build:

   ```
   GET http://localhost:8080/job/mcp/lastBuild/api/json
   ```

   Use the `number` field. (A freshly queued build may take a moment to start.)

3. **Poll until the build finishes.** Repeat the `lastBuild/api/json` call until
   `building == false`, then read `result`:
   - `SUCCESS` -> gate PASSES
   - `FAILURE` / `UNSTABLE` / `ABORTED` -> gate FAILS

4. **On failure, pull the console log** to explain why:

   ```
   GET http://localhost:8080/job/mcp/lastBuild/consoleText
   ```

   Summarize the failing test(s) and the Maven `Tests run / Failures / Errors` line.

5. **Fix and re-run on failure (auto-retry loop).** If the build is `FAILURE`/`UNSTABLE`:
   - Read the console log and identify the root cause (failed assertion, broken locator,
     compile error, app/element not found, config issue, etc.).
   - Apply a targeted fix to the relevant test or source file.
   - Re-trigger the build (back to Step 1) with the same `TEST_CASE`.
   - Repeat this fix -> run cycle up to **3 attempts total**.
   - Stop the loop early and report `PASS` as soon as a build returns `SUCCESS`.
   - If still failing after 3 attempts, stop and report `FAIL` with the remaining error and
     what was tried, so the user can intervene.

   ### Do not auto-fix
   - infrastructure problems outside the test (Jenkins down, emulator won't boot, Appium
     can't start, network/firewall) — report these instead of editing test code
   - changes that weaken the test just to make it pass (deleting assertions, adding blanket
     try/catch, hardcoding expected values)

## Output To Caller

Return a structured result:

- `testCase` used (or "full suite")
- Jenkins build number and URL: `http://localhost:8080/job/mcp/<number>/`
- `result`: `SUCCESS` | `FAILURE` | `UNSTABLE` | `ABORTED`
- Maven summary line (`Tests run: X, Failures: Y, Errors: Z`)
- `gate`: `PASS` or `FAIL`
- If `FAIL`: the key error excerpt from the console log
- `attempts`: number of build runs it took, and a one-line note on any fixes applied between runs

## Non-Negotiable Rules

- The gate result is binary and honest — never report `PASS` unless Jenkins `result == SUCCESS`.
- Never push to git from inside this skill; pushing is the caller's responsibility and only after `PASS`.
- Never fabricate a build number, result, or console output — always read them from Jenkins.
- Never handle or echo the Jenkins password; the user enters it in the terminal.
