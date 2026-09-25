$toolsDir = "C:\Users\mkt.int1\tools"
$mongoDir = Join-Path $toolsDir "mongodb"
$mongoZip = Join-Path $toolsDir "mongodb.zip"
$dataDir = Join-Path $toolsDir "mongodata"

if (!(Test-Path $mongoDir)) {
    New-Item -ItemType Directory -Path $mongoDir -Force | Out-Null
    $mongoDownloadUrl = "https://fastdl.mongodb.org/windows/mongodb-windows-x86_64-7.0.14.zip"
    Write-Output "Downloading Portable MongoDB 7.0.14..."
    $webClient = New-Object System.Net.WebClient
    $webClient.DownloadFile($mongoDownloadUrl, $mongoZip)
    Write-Output "Extracting MongoDB..."
    Expand-Archive -Path $mongoZip -DestinationPath $toolsDir -Force
    Remove-Item $mongoZip -Force
    
    $extractedFolder = Get-ChildItem -Path $toolsDir -Directory -Filter "mongodb-windows-x86_64-7.0.14*" | Select-Object -First 1
    if ($extractedFolder) {
        Get-ChildItem -Path $extractedFolder.FullName | Move-Item -Destination $mongoDir -Force
        Remove-Item $extractedFolder.FullName -Recurse -Force
    }
    Write-Output "MongoDB extracted to $mongoDir"
}

if (!(Test-Path $dataDir)) {
    New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
}

$userPath = [Environment]::GetEnvironmentVariable("Path", "User")
$mongoBin = Join-Path $mongoDir "bin"
if ($userPath -notlike "*$mongoBin*") {
    [Environment]::SetEnvironmentVariable("Path", "$mongoBin;$userPath", "User")
}
Write-Output "MongoDB setup ready in $mongoDir\bin"
