Write-Output "Stopping RideLink microservices..."

# Find and stop Java processes running Spring Boot
$javaProcs = Get-Process java -ErrorAction SilentlyContinue
if ($javaProcs) {
    foreach ($proc in $javaProcs) {
        try {
            Stop-Process -Id $proc.Id -Force
            Write-Output "Stopped Java process $($proc.Id)"
        } catch {
            Write-Warning "Could not stop process $($proc.Id)"
        }
    }
} else {
    Write-Output "No Java processes found."
}

# Stop MongoDB
& "$PSScriptRoot\stop-mongodb.ps1"
Write-Output "All RideLink services stopped successfully."
