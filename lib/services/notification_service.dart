import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/timezone.dart' as tz;
import 'package:permission_handler/permission_handler.dart';
import 'dart:io';

import '../models/task.dart';
import '../utils/constants.dart';
import 'storage_service.dart';

class NotificationService {
  static final NotificationService _instance = NotificationService._internal();
  factory NotificationService() => _instance;
  NotificationService._internal();

  final FlutterLocalNotificationsPlugin _notifications = FlutterLocalNotificationsPlugin();
  bool _initialized = false;

  Future<void> initialize() async {
    if (_initialized) return;

    // Android 초기화 설정
    const androidSettings = AndroidInitializationSettings('@mipmap/ic_launcher');
    
    // iOS 초기화 설정
    final iosSettings = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
      onDidReceiveLocalNotification: _onDidReceiveLocalNotification,
    );

    final initSettings = InitializationSettings(
      android: androidSettings,
      iOS: iosSettings,
    );

    await _notifications.initialize(
      initSettings,
      onDidReceiveNotificationResponse: _onNotificationTapped,
    );

    // 권한 요청
    await _requestPermissions();

    _initialized = true;
  }

  // 권한 요청
  Future<void> _requestPermissions() async {
    if (Platform.isAndroid) {
      // Android 13+ 알림 권한
      if (await Permission.notification.isDenied) {
        await Permission.notification.request();
      }
      
      // Android 12+ 정확 알람 권한
      if (await Permission.scheduleExactAlarm.isDenied) {
        await Permission.scheduleExactAlarm.request();
      }
    } else if (Platform.isIOS) {
      await _notifications
          .resolvePlatformSpecificImplementation<IOSFlutterLocalNotificationsPlugin>()
          ?.requestPermissions(
            alert: true,
            badge: true,
            sound: true,
          );
    }
  }

  // 알림 탭 핸들러
  Future<void> _onNotificationTapped(NotificationResponse response) async {
    final taskId = response.payload;
    if (taskId != null) {
      // 봤음 처리
      final storage = StorageService();
      final task = storage.getTask(taskId);
      if (task != null && task.status != null) {
        task.status!.seenAt = DateTime.now();
        await storage.saveTask(task);
      }
    }
  }

  // iOS 로컬 알림 수신 (앱이 포그라운드일 때)
  void _onDidReceiveLocalNotification(int id, String? title, String? body, String? payload) async {
    // iOS에서 앱이 열려있을 때 알림 처리
  }

  // 알림 스케줄링
  Future<void> scheduleNotification(Task task) async {
    if (!task.enabled) return;

    final storage = StorageService();
    final notificationMode = storage.getNotificationMode();
    final alarmSound = storage.getAlarmSound();

    // 알림 ID는 task.id의 해시코드 사용
    final notificationId = task.id.hashCode.abs();

    // 알림 시간이 이미 지났으면 스케줄하지 않음
    if (task.dateTime.isBefore(DateTime.now())) {
      return;
    }

    // 알림 세부 설정
    final androidDetails = _getAndroidNotificationDetails(notificationMode, alarmSound);
    final iosDetails = _getIOSNotificationDetails(notificationMode, alarmSound);

    final notificationDetails = NotificationDetails(
      android: androidDetails,
      iOS: iosDetails,
    );

    // 스케줄링
    await _notifications.zonedSchedule(
      notificationId,
      '지금 할 시간!',
      '${task.title} - 완료하면 체크하세요',
      tz.TZDateTime.from(task.dateTime, tz.local),
      notificationDetails,
      androidScheduleMode: AndroidScheduleMode.exactAllowWhileIdle,
      uiLocalNotificationDateInterpretation: UILocalNotificationDateInterpretation.absoluteTime,
      payload: task.id,
    );
  }

  // Android 알림 상세 설정
  AndroidNotificationDetails _getAndroidNotificationDetails(String mode, String soundFile) {
    switch (mode) {
      case Constants.notificationModeMelody:
        // 멜로디 모드: 커스텀 사운드 + 진동
        // soundFile에서 확장자 제거 (sound_01.mp3 -> sound_01)
        final soundName = soundFile.replaceAll('.mp3', '').replaceAll('.wav', '');
        return AndroidNotificationDetails(
          'task_channel',
          '할일 알림',
          channelDescription: '숙제와 준비물 알림',
          importance: Importance.high,
          priority: Priority.high,
          sound: RawResourceAndroidNotificationSound(soundName),
          enableVibration: true,
          vibrationPattern: Int64List.fromList([0, 500, 250, 500]),
          playSound: true,
        );

      case Constants.notificationModeVibrate:
        // 진동 모드: 사운드 없이 진동만
        return AndroidNotificationDetails(
          'task_channel',
          '할일 알림',
          channelDescription: '숙제와 준비물 알림',
          importance: Importance.high,
          priority: Priority.high,
          playSound: false,
          enableVibration: true,
          vibrationPattern: Int64List.fromList([0, 1000, 500, 1000, 500, 1000]),
        );

      case Constants.notificationModeSilent:
      default:
        // 무음 모드: 알림만 표시
        return const AndroidNotificationDetails(
          'task_channel',
          '할일 알림',
          channelDescription: '숙제와 준비물 알림',
          importance: Importance.high,
          priority: Priority.high,
          playSound: false,
          enableVibration: false,
        );
    }
  }

  // iOS 알림 상세 설정
  DarwinNotificationDetails _getIOSNotificationDetails(String mode, String soundFile) {
    switch (mode) {
      case Constants.notificationModeMelody:
        // 멜로디 모드: 커스텀 사운드
        return DarwinNotificationDetails(
          sound: soundFile,
          presentAlert: true,
          presentBadge: true,
          presentSound: true,
        );

      case Constants.notificationModeVibrate:
        // 진동 모드: 기본 사운드 없이
        return const DarwinNotificationDetails(
          presentAlert: true,
          presentBadge: true,
          presentSound: false,
        );

      case Constants.notificationModeSilent:
      default:
        // 무음 모드
        return const DarwinNotificationDetails(
          presentAlert: true,
          presentBadge: true,
          presentSound: false,
        );
    }
  }

  // 알림 취소
  Future<void> cancelNotification(Task task) async {
    final notificationId = task.id.hashCode.abs();
    await _notifications.cancel(notificationId);
  }

  // 모든 알림 취소
  Future<void> cancelAllNotifications() async {
    await _notifications.cancelAll();
  }

  // 알림 재스케줄 (설정 변경 시)
  Future<void> rescheduleAllNotifications() async {
    final storage = StorageService();
    final tasks = storage.getAllTasks();
    
    await cancelAllNotifications();
    
    for (final task in tasks) {
      if (task.enabled && task.dateTime.isAfter(DateTime.now())) {
        await scheduleNotification(task);
      }
    }
  }
}

