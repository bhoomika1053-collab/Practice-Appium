<#
CI test runner for Jenkins.
1. Ensures an Android emulator is connected (boots AVD 'mcp' if none).
2. Starts the Appium server and waits until it is ready.
3. Runs the Maven/TestNG suite (or a specific test case via -TestCase / TEST_CASE env var).
4. Cleans up the Appium server it started.
Exit code mirrors the Maven result so Jenkins reports pass/fail correctly.

TEST_CASE examples (set as a Jenkins parameter or pass -TestCase):
  (empty)                                 -> runs the full testng.xml suite
  SmokeTest                               -> runs all tests in SmokeTest
  SmokeTest#appLaunchesSuccessfully       -> runs a single test method
#>
param(
    [string]$TestCase = $env:TEST_CASE
)

$ErrorActionPreference = "Stop"
$projectDir = $PSScriptRoot
Set-Location $projectDir

if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:USERPROFILE\AppData\Local\Android\Sdk" }
$adb      = Join-Path $env:ANDROID_HOME "platform-tools\adb.exe"
$emulator = Join-Path $env:ANDROID_HOME "emulator\emulator.exe"
$avdName  = "mcp"
$appiumProc = $null

function Wait-ForUrl([string]$url, [int]$timeoutSec) {
    for ($i = 0; $i -lt $timeoutSec; $i++) {
        try {
            $r = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 3 -ErrorAction Stop
            if ($r.StatusCode -ge 200 -and $r.StatusCode -lt 500) { return $true }
        } catch {
            if ($_.Exception.Response) { return $true }  # server answered (e.g. 4xx) = up
        }
        Start-Sleep -Seconds 1
    }
    return $false
}

try {
    # 1. Ensure an emulator/device is connected
    Write-Host "=== Checking for connected device ===" -ForegroundColor Cyan
    $devices = & $adb devices | Select-String "device$" | Where-Object { $_ -notmatch "List of devices" }
    if (-not $devices) {
        Write-Host "No device connected. Booting AVD '$avdName'..." -ForegroundColor Yellow
        Start-Process -FilePath $emulator -ArgumentList "-avd $avdName -no-snapshot-load" -WindowStyle Minimized
        Write-Host "Waiting for emulator to come online..." -ForegroundColor Yellow
        & $adb wait-for-device
    } else {
        Write-Host "Device already connected: $devices" -ForegroundColor Green
    }

    # Wait for the Android framework to be fully ready (settings + package services).
    # boot_completed alone is not enough; UiAutomator2 needs the 'settings' service.
    Write-Host "Waiting for Android framework services..." -ForegroundColor Yellow
    $frameworkReady = $false
    for ($i = 0; $i -lt 120; $i++) {
        $boot = (& $adb shell getprop sys.boot_completed 2>$null) -replace '\s',''
        $set  = (& $adb shell service check settings 2>$null) | Out-String
        $pkg  = (& $adb shell service check package 2>$null) | Out-String
        if ($boot -eq "1" -and $set -notmatch "not found" -and $set -match "found" -and $pkg -notmatch "not found" -and $pkg -match "found") {
            $frameworkReady = $true
            break
        }
        Start-Sleep -Seconds 3
    }
    if (-not $frameworkReady) {
        throw "Android framework (settings/package services) did not come up. Try a cold boot: emulator -avd $avdName -no-snapshot-load -wipe-data"
    }
    Write-Host "Android framework is ready." -ForegroundColor Green

    # 2. Start Appium server (background)
    Write-Host "=== Starting Appium server ===" -ForegroundColor Cyan
    $appiumCmd = (Get-Command appium.cmd -ErrorAction SilentlyContinue).Source
    if (-not $appiumCmd) { $appiumCmd = Join-Path $env:APPDATA "npm\appium.cmd" }
    $appiumProc = Start-Process -FilePath $appiumCmd -ArgumentList "--address 127.0.0.1 --port 4723 --relaxed-security" -PassThru -WindowStyle Minimized
    if (-not (Wait-ForUrl "http://127.0.0.1:4723/status" 60)) {
        throw "Appium server did not become ready on port 4723."
    }
    Write-Host "Appium is ready." -ForegroundColor Green

    # 3. Run the tests
    Write-Host "=== Running Maven tests ===" -ForegroundColor Cyan
    if ([string]::IsNullOrWhiteSpace($TestCase)) {
        Write-Host "Running full suite (testng.xml)" -ForegroundColor Cyan
        & cmd /c "mvn clean test -Dsurefire.suiteXmlFiles=testng.xml"
    } else {
        Write-Host "Running specific test case: $TestCase" -ForegroundColor Cyan
        & cmd /c "mvn clean test -Dtest=`"$TestCase`""
    }
    $mvnExit = $LASTEXITCODE
    Write-Host "Maven exited with code $mvnExit" -ForegroundColor Cyan
}
finally {
    # 4. Clean up Appium
    if ($appiumProc -and -not $appiumProc.HasExited) {
        Write-Host "=== Stopping Appium server ===" -ForegroundColor Cyan
        Stop-Process -Id $appiumProc.Id -Force -ErrorAction SilentlyContinue
        Get-Process node -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*appium*" } | Stop-Process -Force -ErrorAction SilentlyContinue
    }
}

exit $mvnExit
