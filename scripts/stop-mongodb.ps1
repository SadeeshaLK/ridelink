$running = Get-Process mongod -ErrorAction SilentlyContinue
if ($running) {
    Stop-Process -Name mongod -Force
    Write-Output "MongoDB stopped."
} else {
    Write-Output "MongoDB is not running."
}
