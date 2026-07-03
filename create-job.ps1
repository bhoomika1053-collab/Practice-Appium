param(
    [string]$JenkinsUrl = "http://localhost:8080",
    [string]$User = "",
    [string]$Pass = "",
    [string]$TestCase = "",
    [switch]$NonInteractive
)

$ErrorActionPreference = "Stop"

# Credential resolution order (so the gate can run unattended):
#   1. -User / -Pass parameters (Pass can be a Jenkins API token)
#   2. JENKINS_USER / JENKINS_TOKEN (or JENKINS_PASS) environment variables
#   3. Interactive Get-Credential prompt (only when nothing above is set and not -NonInteractive)
if (-not $User)  { $User = $env:JENKINS_USER }
if (-not $Pass)  { $Pass = $env:JENKINS_TOKEN; if (-not $Pass) { $Pass = $env:JENKINS_PASS } }

if (-not $User -or -not $Pass) {
    if ($NonInteractive) {
        Write-Host "No credentials supplied. Provide -User and -Pass (API token), or set JENKINS_USER and JENKINS_TOKEN env vars." -ForegroundColor Red
        exit 3
    }
    Write-Host "Enter the Jenkins credentials (username + password or API token)." -ForegroundColor Cyan
    $cred = Get-Credential -Message "Jenkins login (username + password/API token)"
    $User = $cred.UserName
    $Pass = $cred.GetNetworkCredential().Password
} else {
    Write-Host "Using supplied credentials (non-interactive)." -ForegroundColor DarkGray
}

Write-Host "Using user '$User' against $JenkinsUrl" -ForegroundColor Cyan

$pair = "$($User):$($Pass)"
$auth = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($pair))
$baseHeaders = @{ Authorization = "Basic $auth" }

# Get CSRF crumb within a session so the cookie is preserved
try {
    $crumbResp = Invoke-RestMethod -Uri "$JenkinsUrl/crumbIssuer/api/json" -Headers $baseHeaders -SessionVariable session
    $crumbHeader = @{ $crumbResp.crumbRequestField = $crumbResp.crumb }
    Write-Host "Got CSRF crumb." -ForegroundColor Green
} catch {
    Write-Host "Auth/crumb failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "If you created a different admin user, re-run with -User and -Pass." -ForegroundColor Yellow
    exit 2
}

$jobName = "mcp"

# Freestyle job (core Jenkins, no plugins needed). Runs Maven tests against the local project.
$projectDir = $PSScriptRoot
$jobConfig = @"
<?xml version="1.1" encoding="UTF-8"?>
<project>
  <actions/>
  <description>ASM Mobile Testing - runs local Maven/TestNG tests. Manual build: choose a TEST_CASE or leave blank for the full suite.</description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <hudson.model.ParametersDefinitionProperty>
      <parameterDefinitions>
        <hudson.model.StringParameterDefinition>
          <name>TEST_CASE</name>
          <description>Which test to run. Leave blank = full testng.xml suite. Examples: SmokeTest  |  SmokeTest#appLaunchesSuccessfully</description>
          <defaultValue></defaultValue>
          <trim>true</trim>
        </hudson.model.StringParameterDefinition>
      </parameterDefinitions>
    </hudson.model.ParametersDefinitionProperty>
  </properties>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <authToken>mcpbuildtoken</authToken>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders>
    <hudson.tasks.BatchFile>
      <command>cd /d "$projectDir"
powershell -ExecutionPolicy Bypass -File "$projectDir\ci-run.ps1"</command>
    </hudson.tasks.BatchFile>
  </builders>
  <publishers/>
  <buildWrappers/>
</project>
"@

# Check if job exists
$exists = $false
try {
    Invoke-RestMethod -Uri "$JenkinsUrl/job/$jobName/api/json" -Headers $baseHeaders -WebSession $session | Out-Null
    $exists = $true
} catch { $exists = $false }

$postHeaders = $baseHeaders + $crumbHeader + @{ "Content-Type" = "application/xml" }

if ($exists) {
    Write-Host "Job exists, updating config..." -ForegroundColor Cyan
    Invoke-RestMethod -Uri "$JenkinsUrl/job/$jobName/config.xml" -Method POST -Headers $postHeaders -Body $jobConfig -WebSession $session | Out-Null
} else {
    Write-Host "Creating job '$jobName'..." -ForegroundColor Cyan
    Invoke-RestMethod -Uri "$JenkinsUrl/createItem?name=$jobName" -Method POST -Headers $postHeaders -Body $jobConfig -WebSession $session | Out-Null
}
Write-Host "Job ready." -ForegroundColor Green

# Trigger a build manually with the chosen test case (blank = full suite).
Write-Host "Triggering build (TEST_CASE='$TestCase')..." -ForegroundColor Cyan
$triggerHeaders = $baseHeaders + $crumbHeader
$encoded = [System.Uri]::EscapeDataString($TestCase)
Invoke-RestMethod -Uri "$JenkinsUrl/job/$jobName/buildWithParameters?TEST_CASE=$encoded" -Method POST -Headers $triggerHeaders -WebSession $session | Out-Null
Write-Host "Build triggered!" -ForegroundColor Green

Write-Host "`nDashboard:        $JenkinsUrl/job/$jobName" -ForegroundColor Yellow
Write-Host "Build w/ params:  $JenkinsUrl/job/$jobName/build?delay=0sec  (or click 'Build with Parameters')" -ForegroundColor Yellow
Write-Host "Console:          $JenkinsUrl/job/$jobName/lastBuild/console" -ForegroundColor Yellow

if (-not $NonInteractive) {
    Start-Process "$JenkinsUrl/job/$jobName"
}
