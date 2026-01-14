# ForgetLess - Flutter 버전 (Android/iOS)

## 📱 프로젝트 개요
중학생이 숙제와 준비물을 까먹지 않도록 도와주는 크로스플랫폼 앱입니다.

### 🎯 주요 기능
- ✅ **기본 4개 항목** 자동 생성 (마스크, 응가역사/과학/사회 숙제)
- 🔔 **실제 소리/진동 알림** (멜로디/진동/무음 3가지 모드)
- 🎵 **7가지 멜로디** 선택 가능 + 미리듣기 기능
- 📅 **날짜+시간 선택** (달력 UI 포함)
- 📱 **알림 탭** → "봤음" 자동 기록
- ✔️ **완료 처리** 기능
- 💾 **앱 재실행 후에도 설정 유지** (Hive 저장소)
- 🌙 **다크 테마** + 라임 그린 포인트 색상

## 🛠 기술 스택
- **프레임워크**: Flutter 3.x
- **언어**: Dart
- **로컬 알림**: flutter_local_notifications
- **로컬 저장소**: Hive
- **오디오**: audioplayers
- **상태 관리**: Provider
- **플랫폼**: Android (API 21+), iOS (13.0+)

## 📂 프로젝트 구조
```
lib/
├── main.dart                  # 앱 진입점
├── models/
│   ├── task.dart             # Task 모델
│   └── task.g.dart           # Hive 생성 코드
├── services/
│   ├── notification_service.dart  # 알림 서비스
│   ├── storage_service.dart       # 저장소 서비스
│   └── audio_service.dart         # 오디오 재생
├── providers/
│   ├── task_provider.dart         # Task 상태 관리
│   └── settings_provider.dart     # 설정 상태 관리
├── screens/
│   ├── home_screen.dart          # 홈 (오늘 할 일)
│   ├── task_list_screen.dart     # 전체 항목
│   ├── task_edit_screen.dart     # 추가/편집
│   └── settings_screen.dart      # 알림 설정
├── widgets/
│   └── task_card.dart            # Task 카드 위젯
└── utils/
    ├── theme.dart                # 앱 테마
    └── constants.dart            # 상수 정의

assets/sounds/                 # 알림 사운드 파일들
android/                       # Android 네이티브
ios/                          # iOS 네이티브
```

## 🚀 설치 및 실행

### 1. 환경 요구사항
- Flutter SDK 3.2.0 이상
- Dart SDK 3.0.0 이상
- Android Studio (Android) 또는 Xcode (iOS)

### 2. 의존성 설치
```bash
flutter pub get
```

### 3. 사운드 파일 준비 ⚠️ 중요!
다음 위치에 알림 사운드 파일을 추가해야 합니다:

#### Flutter Assets
```
assets/sounds/
├── alarm_01.wav (차임)
├── alarm_02.wav (딩동) ← 기본값
├── alarm_03.wav (비프)
├── alarm_04.wav (종소리)
├── alarm_05.wav (트럼펫)
├── alarm_06.wav (마림바)
└── alarm_07.wav (기타)
```

#### Android 커스텀 사운드
```bash
cp assets/sounds/*.wav android/app/src/main/res/raw/
```
**주의**: Android의 경우 파일 확장자를 제거하고 소문자로만 구성:
- `alarm_01.wav` → `res/raw/alarm_01.wav`

#### iOS 커스텀 사운드
```bash
cp assets/sounds/*.wav ios/Runner/
```
그 다음 Xcode에서 프로젝트를 열고:
1. Runner 폴더에 파일 추가 (Add Files to "Runner"...)
2. "Copy items if needed" 체크
3. Target: Runner 선택

### 4. 실행
```bash
# Android
flutter run -d android

# iOS
flutter run -d ios

# 빌드
flutter build apk          # Android APK
flutter build ios          # iOS (Xcode 필요)
```

## 📱 알림 모드 설명

### 🎵 멜로디 모드
- 선택한 사운드 파일 재생
- 진동도 함께 동작
- 7가지 멜로디 중 선택 가능
- 미리듣기 버튼으로 테스트 가능

### 📳 진동 모드
- 사운드 없이 진동만 동작
- 더 긴 진동 패턴 사용
- Android: 강한 진동
- iOS: 시스템 햅틱

### 🔕 무음 모드
- 소리와 진동 없음
- 알림만 조용히 표시
- 상태바에 알림 아이콘만 표시

## 🎨 알림 설정 화면

설정 화면에서 다음을 조정할 수 있습니다:
1. **알림 모드** 선택 (멜로디/진동/무음)
2. **멜로디 선택** (멜로디 모드인 경우)
3. **미리듣기** 버튼으로 사운드 테스트

설정 변경 시 모든 예정된 알림이 자동으로 재스케줄됩니다.

## 📅 날짜+시간 선택

할 일 추가/편집 시:
- **날짜 선택**: 달력 UI로 날짜 선택
- **시간 선택**: 시간 피커로 시간 선택
- **기본값**: 내일 오전 8시

## ⚠️ 권한 설정

### Android
자동으로 요청되는 권한:
- `POST_NOTIFICATIONS` (Android 13+)
- `SCHEDULE_EXACT_ALARM` (Android 12+)
- `VIBRATE`
- `RECEIVE_BOOT_COMPLETED`

첫 실행 시 권한을 허용해주세요.

### iOS
첫 실행 시 알림 권한 요청:
- 알림 표시
- 사운드
- 배지

"허용" 을 선택해주세요.

## 🔧 트러블슈팅

### 알림이 울리지 않음
1. **권한 확인**
   - Android: 설정 → 앱 → ForgetLess → 알림 허용
   - iOS: 설정 → ForgetLess → 알림 허용

2. **배터리 최적화 해제** (Android)
   - 설정 → 배터리 → ForgetLess → 최적화하지 않음

3. **사운드 파일 확인**
   - `assets/sounds/` 폴더에 모든 WAV 파일 존재 확인
   - Android: `res/raw/` 폴더 확인
   - iOS: Xcode 프로젝트에 추가 확인

### 사운드가 재생되지 않음
1. **Android**
   - `res/raw/` 폴더에 파일 복사 확인
   - 파일명이 소문자+언더스코어만 포함 확인
   - 확장자 제거 확인

2. **iOS**
   - Xcode에서 Runner 타겟에 파일 추가 확인
   - Build Phases → Copy Bundle Resources 확인

### 앱이 빌드되지 않음
```bash
# 클린 빌드
flutter clean
flutter pub get
flutter run
```

## 📊 데이터 저장

### Hive 저장소
- **Task 데이터**: `tasks_box`
  - 할 일 정보 (제목, 날짜/시간, 메모 등)
  - 상태 정보 (봤음, 완료)
  
- **설정 데이터**: `settings_box`
  - 알림 모드 (멜로디/진동/무음)
  - 선택한 멜로디

### 데이터 위치
- Android: `/data/data/com.geniusbrain.forgetless/`
- iOS: `Library/Application Support/`

## 🎯 테스트 체크리스트

- [ ] 첫 실행 시 기본 4개 항목 생성
- [ ] 오늘 할 일만 홈 화면에 표시
- [ ] 알림이 정확한 시간에 울림
- [ ] 멜로디 모드: 사운드 재생 확인
- [ ] 진동 모드: 진동만 확인
- [ ] 무음 모드: 조용히 알림만 확인
- [ ] 알림 탭 → "봤음" 기록
- [ ] 완료 버튼 → "완료" 상태 변경
- [ ] 설정 변경 → 앱 재시작 후에도 유지
- [ ] 미리듣기 버튼 → 사운드 재생
- [ ] 날짜/시간 선택 → 정확히 예약
- [ ] 항목 추가/편집/삭제 동작
- [ ] enabled 토글 → 알림 켜기/끄기

## 🌐 지원 플랫폼

| 플랫폼 | 최소 버전 | 알림 | 사운드 | 진동 |
|--------|----------|------|--------|------|
| Android | API 21 (5.0) | ✅ | ✅ | ✅ |
| iOS | 13.0 | ✅ | ✅ | ✅ |

## 📝 라이선스
개인 프로젝트 - 자유롭게 사용 가능

## 🙏 무료 사운드 리소스
- [Freesound](https://freesound.org/)
- [Pixabay Sound Effects](https://pixabay.com/sound-effects/)
- [Mixkit](https://mixkit.co/free-sound-effects/)

