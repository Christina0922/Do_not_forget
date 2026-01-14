class Constants {
  // 알림 모드
  static const String notificationModeMelody = 'melody';
  static const String notificationModeVibrate = 'vibrate';
  static const String notificationModeSilent = 'silent';
  
  // 알림 사운드
  static const List<AlarmSound> alarmSounds = [
    AlarmSound('sound_01.mp3', '차임', '🔔'),
    AlarmSound('sound_02.mp3', '딩동', '🎵'),
    AlarmSound('sound_03.mp3', '비프', '📢'),
    AlarmSound('sound_04.mp3', '종소리', '🛎️'),
    AlarmSound('sound_05.mp3', '트럼펫', '🎺'),
    AlarmSound('sound_06.mp3', '마림바', '🎹'),
    AlarmSound('sound_07.mp3', '기타', '🎸'),
  ];
  
  // 기본값
  static const String defaultNotificationMode = notificationModeMelody;
  static const String defaultAlarmSound = 'sound_02.mp3';
  
  // Hive Box 이름
  static const String tasksBox = 'tasks_box';
  static const String settingsBox = 'settings_box';
  
  // 설정 키
  static const String keyNotificationMode = 'notification_mode';
  static const String keyAlarmSound = 'alarm_sound';
}

class AlarmSound {
  final String fileName;
  final String displayName;
  final String emoji;
  
  const AlarmSound(this.fileName, this.displayName, this.emoji);
}

