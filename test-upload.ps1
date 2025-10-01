# Test script to upload a document to DocFlow API

$url = "http://localhost:8081/api/documents/upload"
$filePath = "test-document.txt"

# Create multipart form data
$boundary = [System.Guid]::NewGuid().ToString()
$LF = "`r`n"

$fileBytes = [System.IO.File]::ReadAllBytes($filePath)
$fileEnc = [System.Text.Encoding]::GetEncoding('iso-8859-1').GetString($fileBytes)

$bodyLines = (
    "--$boundary",
    "Content-Disposition: form-data; name=`"file`"; filename=`"test-document.txt`"",
    "Content-Type: text/plain$LF",
    $fileEnc,
    "--$boundary--$LF"
) -join $LF

try {
    $response = Invoke-RestMethod -Uri $url -Method Post -ContentType "multipart/form-data; boundary=$boundary" -Body $bodyLines
    Write-Host "Upload successful!" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Yellow
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Upload failed!" -ForegroundColor Red
    Write-Host $_.Exception.Message
}
