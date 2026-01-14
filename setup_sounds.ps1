# ForgetLess - 사운드 파일 설정 스크립트 (PowerShell)

Write-Host "========================================"
Write-Host "ForgetLess - 사운드 파일 설정 스크립트"
Write-Host "========================================"
Write-Host ""

# Android res/raw 폴더 생성
$androidRawPath = "android\app\src\main\res\raw"
if (-not (Test-Path $androidRawPath)) {
    Write-Host "[1/3] Android raw 폴더 생성 중..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $androidRawPath -Force | Out-Null
} else {
    Write-Host "[1/3] Android raw 폴더 이미 존재" -ForegroundColor Green
}

# Android에 사운드 파일 복사
Write-Host "[2/3] Android에 사운드 파일 복사 중..." -ForegroundColor Yellow
$soundFiles = @("sound_01", "sound_02", "sound_03", "sound_04", "sound_05", "sound_06", "sound_07")
foreach ($file in $soundFiles) {
    Copy-Item -Path "sound\$file.mp3" -Destination "android\app\src\main\res\raw\$file.mp3" -Force
    Write-Host "  ✓ $file.mp3" -ForegroundColor Gray
}

# iOS에 사운드 파일 복사
Write-Host "[3/3] iOS에 사운드 파일 복사 중..." -ForegroundColor Yellow
foreach ($file in $soundFiles) {
    Copy-Item -Path "sound\$file.mp3" -Destination "ios\Runner\$file.mp3" -Force
    Write-Host "  ✓ $file.mp3" -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================"
Write-Host "완료! 사운드 파일 설정 완료" -ForegroundColor Green
Write-Host "========================================"
Write-Host ""
Write-Host "다음 단계:"
Write-Host "1. flutter pub get"
Write-Host "2. flutter run"
Write-Host ""
Write-Host "iOS의 경우 Xcode에서 수동으로 파일 추가 필요:" -ForegroundColor Yellow
Write-Host "- Xcode에서 ios/Runner.xcworkspace 열기"
Write-Host "- Runner 폴더에 sound_*.mp3 파일 추가"
Write-Host "- Target: Runner 선택"
Write-Host ""

