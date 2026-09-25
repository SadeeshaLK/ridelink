$toolsDir = "C:\Users\mkt.int1\tools"
$mongoBin = Join-Path $toolsDir "mongodb\bin\mongod.exe"
$dataDir = Join-Path $toolsDir "mongodata"

if (!(Test-Path $mongoBin)) {
    Write-Output "MongoDB not found. Running setup..."
    & "$PSScriptRoot\setup-mongodb.ps1"
}

if (!(Test-Path $dataDir)) {
    New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
}

$running = Get-Process mongod -ErrorAction SilentlyContinue
if ($running) {
    Write-Output "MongoDB is already running (PID: $($running.Id))."
} else {
    Write-Output "Starting MongoDB on port 27017..."
    Start-Process -FilePath $mongoBin -ArgumentList "--dbpath `"$dataDir`" --logpath `"$dataDir\mongod.log`"" -WindowStyle Hidden
    Start-Sleep -Seconds 2
    Write-Output "MongoDB process started on port 27017."
}
