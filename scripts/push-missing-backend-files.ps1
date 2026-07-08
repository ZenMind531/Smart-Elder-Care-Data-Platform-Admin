$ErrorActionPreference = "Stop"

param(
    [string]$Message = "Add backend bootstrap and test support files",
    [switch]$SkipTests
)

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

$branch = (git branch --show-current).Trim()
if ([string]::IsNullOrWhiteSpace($branch)) {
    throw "Cannot detect current Git branch."
}

Write-Host "Current branch: $branch"
Write-Host "Checking files required for backend testing..."

$pathsToAdd = @(
    ".mvn/maven.config",
    ".mvn/settings.xml",
    "pom.xml",
    "scripts/run-dev.bat",
    "scripts/run-dev.ps1",
    "scripts/push-missing-backend-files.ps1",
    "src/main/java/com/smarteldercare/SmartElderCareApplication.java",
    "src/main/java/com/smarteldercare/common",
    "src/main/java/com/smarteldercare/modules/dashboard",
    "src/main/java/com/smarteldercare/modules/device",
    "src/main/java/com/smarteldercare/modules/report",
    "src/main/java/com/smarteldercare/modules/system",
    "src/main/resources",
    "src/test",
    "协同开发目录结构规范.md"
)

$missing = @()
foreach ($path in $pathsToAdd) {
    if (-not (Test-Path -LiteralPath $path)) {
        $missing += $path
    }
}

if ($missing.Count -gt 0) {
    Write-Warning "These paths were not found and will be skipped:"
    $missing | ForEach-Object { Write-Warning "  $_" }
}

if (-not $SkipTests) {
    Write-Host "Running Maven tests before commit..."
    mvn test
} else {
    Write-Warning "Skipping tests because -SkipTests was provided."
}

Write-Host "Staging backend bootstrap/test files..."
foreach ($path in $pathsToAdd) {
    if (Test-Path -LiteralPath $path) {
        git add -- $path
    }
}

Write-Host "Staged changes:"
git status --short

$staged = git diff --cached --name-only
if ([string]::IsNullOrWhiteSpace(($staged -join ""))) {
    Write-Host "No staged changes to commit."
} else {
    git commit -m $Message
}

Write-Host "Pushing to origin/$branch..."
git push -u origin $branch

Write-Host "Done."
