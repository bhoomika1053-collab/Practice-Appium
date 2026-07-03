Write-Host "`n=== Starting Jenkins (no Docker required) ===" -ForegroundColor Cyan

$jenkinsDir = "$env:USERPROFILE\.jenkins-local"
$warPath = "$jenkinsDir\jenkins.war"

if (-not (Test-Path $jenkinsDir)) {
    New-Item -ItemType Directory -Path $jenkinsDir -Force | Out-Null
}

# Jenkins 2.426.3 is the last LTS that supports Java 11.
# Downloaded from repo.jenkins-ci.org (get.jenkins.io is blocked on this network).
$warUrl = "https://repo.jenkins-ci.org/public/org/jenkins-ci/main/jenkins-war/2.426.3/jenkins-war-2.426.3.war"

if ((-not (Test-Path $warPath)) -or ((Get-Item $warPath).Length -lt 1MB)) {
    Write-Host "Downloading Jenkins (~70 MB, one-time)..." -ForegroundColor Green
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $warUrl -OutFile $warPath -UseBasicParsing
    Write-Host "Downloaded Jenkins ($([Math]::Round((Get-Item $warPath).Length/1MB,1)) MB)." -ForegroundColor Green
} else {
    Write-Host "Jenkins already downloaded." -ForegroundColor Green
}

Write-Host "`nStarting Jenkins on http://localhost:8080 ..." -ForegroundColor Yellow
Write-Host "Keep this window open. Jenkins is starting (30-60 sec)...`n" -ForegroundColor Yellow

$env:JENKINS_HOME = "$jenkinsDir\home"
java -jar $warPath --httpPort=8080
