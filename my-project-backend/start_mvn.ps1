$mavenPath = "C:\Program Files\Apache\Maven\apache-maven-3.9.6\bin\mvn.cmd"

if (Test-Path $mavenPath) {
    Write-Host "Using Maven from: $mavenPath"
    & $mavenPath spring-boot:run
} else {
    Write-Host "Maven not found at $mavenPath"
    Write-Host "Searching for mvn in PATH..."
    $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mvnCmd) {
        Write-Host "Found mvn at: $($mvnCmd.Path)"
        & $mvnCmd.Path spring-boot:run
    } else {
        Write-Host "Maven not found!"
    }
}