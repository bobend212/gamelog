Write-Host "Note: JDK 17 is required."

# Start Spring Boot backend as a background job
$backendJob = Start-Job -ScriptBlock {
    cd "C:\Users\mkonopka\Desktop\code\gamelog\gamelog"
    ./mvnw.cmd spring-boot:run
}

Start-Sleep -Seconds 3 # Adjust this delay if backend startup takes longer

# Start React frontend as a background job
$frontendJob = Start-Job -ScriptBlock {
    cd "C:\Users\mkonopka\Desktop\code\gamelog\gamelog-frontend"
    npm start
}

Write-Host "Both backend and frontend started..."
Write-Host "Use Ctrl+C to stop"

# Keep the script running, waiting for jobs to finish (or Ctrl+C to interrupt)
try {
    Wait-Job -Job $backendJob, $frontendJob
} finally {
    # Cleanup if script interrupted
    Stop-Job -Job $backendJob -ErrorAction SilentlyContinue
    Stop-Job -Job $frontendJob -ErrorAction SilentlyContinue
    Remove-Job -Job $backendJob, $frontendJob -ErrorAction SilentlyContinue
}
