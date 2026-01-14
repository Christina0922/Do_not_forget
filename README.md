# ForgetLess - 중학생 숙제·준비물 관리 앱

## 프로젝트 개요
중학생이 숙제와 준비물을 까먹지 않도록 도와주는 안드로이드 앱입니다.

### 주요 기능
- ✅ 기본 4개 항목 (마스크, 응가역사/과학/사회 숙제) 자동 생성
- 🔔 정확한 시간에 알림 발송
- 📱 알림 탭하면 "봤음" 자동 기록
- ✔️ 알림 또는 앱에서 "완료" 처리
- ⏰ "10분 뒤" 스누즈 기능
- 🔄 재부팅 후에도 알림 자동 재등록
- 🌙 다크 테마 + 라임 포인트 색상

## 기술 스택
- **언어**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **데이터베이스**: Room
- **아키텍처**: MVVM + Repository 패턴
- **알림**: AlarmManager + BroadcastReceiver
- **네비게이션**: Navigation Compose

## 프로젝트 구조
```
app/src/main/java/com/geniusbrain/forgetless/
├── data/                          # 데이터 레이어
│   ├── TaskEntity.kt             # 할일 항목 엔티티
│   ├── TaskDailyStatusEntity.kt  # 일별 상태 엔티티
│   ├── TaskDao.kt                # 할일 DAO
│   ├── StatusDao.kt              # 상태 DAO
│   ├── AppDatabase.kt            # Room 데이터베이스
│   └── Repository.kt             # 리포지토리
├── alarm/                         # 알림/알람 시스템
│   ├── AlarmScheduler.kt         # 알람 스케줄링
│   ├── AlarmReceiver.kt          # 알람 수신
│   ├── CompleteActionReceiver.kt # 완료 액션
│   ├── SnoozeActionReceiver.kt   # 스누즈 액션
│   └── BootReceiver.kt           # 재부팅 수신
├── notification/                  # 알림 헬퍼
│   └── NotificationHelper.kt     # 알림 생성/표시
├── ui/                            # UI 레이어
│   ├── HomeScreen.kt             # 홈 화면
│   ├── HomeViewModel.kt          # 홈 뷰모델
│   ├── TaskListScreen.kt         # 전체 항목 화면
│   ├── TaskListViewModel.kt      # 전체 항목 뷰모델
│   ├── TaskEditScreen.kt         # 추가/편집 화면
│   ├── TaskEditViewModel.kt      # 추가/편집 뷰모델
│   ├── components/               # UI 컴포넌트
│   │   ├── TaskCard.kt          # 할일 카드
│   │   ├── DayChips.kt          # 요일 칩
│   │   └── StatusBadge.kt       # 상태 뱃지
│   └── theme/                    # 테마
│       ├── Color.kt             # 색상 정의
│       ├── Theme.kt             # 테마 정의
│       └── Typography.kt        # 타이포그래피
├── MainActivity.kt               # 메인 액티비티
└── ForgetLessApplication.kt      # 앱 클래스
```

## 데이터 구조

### TaskEntity (할일 항목)
- `id`: 고유 ID
- `title`: 제목
- `enabled`: 활성화 여부
- `hour`, `minute`: 알림 시간
- `repeatDays`: 반복 요일 (1=월 ~ 7=일)
- `note`: 메모

### TaskDailyStatusEntity (일별 상태)
- `taskId`: 할일 ID
- `dateKey`: 날짜 (yyyy-MM-dd)
- `seenAt`: 봤음 시각
- `completedAt`: 완료 시각
- `snoozedAt`: 스누즈 시각

## 알림 동작 방식

1. **알림 발생**: 설정된 시간에 정확 알람 발생
2. **알림 탭**: 앱이 열리며 "봤음" 자동 기록
3. **완료 버튼**: "오늘 완료" 처리
4. **10분 뒤 버튼**: 10분 후 1회 재알림
5. **다음 알람 재스케줄**: 알람 발생 후 즉시 다음 발생일 계산하여 재등록

## 권한
- `POST_NOTIFICATIONS`: 알림 표시 (Android 13+)
- `SCHEDULE_EXACT_ALARM`: 정확 알람 (Android 12+)
- `RECEIVE_BOOT_COMPLETED`: 재부팅 감지

## 빌드 방법
```bash
# Windows
gradlew.bat assembleDebug

# 설치
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 테스트 시나리오
1. ✅ 첫 실행 시 기본 4개 항목 자동 생성
2. ✅ 오늘 요일에 해당하는 항목만 홈 화면에 표시
3. ✅ 알림 탭하면 "봤음" 자동 기록
4. ✅ 알림의 "완료" 버튼으로 완료 처리
5. ✅ 앱의 "완료" 버튼으로 완료 처리
6. ✅ "10분 뒤" 버튼으로 스누즈
7. ✅ enabled 토글로 알람 켜기/끄기
8. ✅ 재부팅 후 알람 자동 재등록

## 디자인 특징
- 다크 네이비/차콜 배경
- 라임 그린 포인트 색상
- 큼직한 버튼과 텍스트
- 게임 UI 느낌의 직관적인 디자인
- 중2 남학생 타겟의 깔끔한 스타일

## 라이선스
개인 프로젝트 - 자유롭게 사용 가능

