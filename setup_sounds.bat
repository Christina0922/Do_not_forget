@echo off
echo ========================================
echo ForgetLess - 사운드 파일 설정 스크립트
echo ========================================
echo.

REM Android res/raw 폴더 생성
if not exist "android\app\src\main\res\raw" (
    echo [1/3] Android raw 폴더 생성 중...
    mkdir "android\app\src\main\res\raw"
) else (
    echo [1/3] Android raw 폴더 이미 존재
)

REM Android에 사운드 파일 복사
echo [2/3] Android에 사운드 파일 복사 중...
copy /Y "sound\sound_01.mp3" "android\app\src\main\res\raw\sound_01.mp3"
copy /Y "sound\sound_02.mp3" "android\app\src\main\res\raw\sound_02.mp3"
copy /Y "sound\sound_03.mp3" "android\app\src\main\res\raw\sound_03.mp3"
copy /Y "sound\sound_04.mp3" "android\app\src\main\res\raw\sound_04.mp3"
copy /Y "sound\sound_05.mp3" "android\app\src\main\res\raw\sound_05.mp3"
copy /Y "sound\sound_06.mp3" "android\app\src\main\res\raw\sound_06.mp3"
copy /Y "sound\sound_07.mp3" "android\app\src\main\res\raw\sound_07.mp3"

REM iOS에 사운드 파일 복사
echo [3/3] iOS에 사운드 파일 복사 중...
copy /Y "sound\sound_01.mp3" "ios\Runner\sound_01.mp3"
copy /Y "sound\sound_02.mp3" "ios\Runner\sound_02.mp3"
copy /Y "sound\sound_03.mp3" "ios\Runner\sound_03.mp3"
copy /Y "sound\sound_04.mp3" "ios\Runner\sound_04.mp3"
copy /Y "sound\sound_05.mp3" "ios\Runner\sound_05.mp3"
copy /Y "sound\sound_06.mp3" "ios\Runner\sound_06.mp3"
copy /Y "sound\sound_07.mp3" "ios\Runner\sound_07.mp3"

echo.
echo ========================================
echo 완료! 사운드 파일 설정 완료
echo ========================================
echo.
echo 다음 단계:
echo 1. flutter pub get
echo 2. flutter run
echo.
echo iOS의 경우 Xcode에서 수동으로 파일 추가 필요:
echo - Xcode에서 ios/Runner.xcworkspace 열기
echo - Runner 폴더에 sound_*.mp3 파일 추가
echo - Target: Runner 선택
echo.
pause

