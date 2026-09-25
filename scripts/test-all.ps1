$env:JAVA_HOME = "C:\Users\mkt.int1\tools\jdk-21.0.12.1+1"
$env:MAVEN_HOME = "C:\Users\mkt.int1\tools\apache-maven-3.9.9"
$env:PATH = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:PATH"

Write-Output "Running unit tests across all 4 microservices..."
$root = Resolve-Path "$PSScriptRoot\.."
Set-Location $root
mvn clean test
