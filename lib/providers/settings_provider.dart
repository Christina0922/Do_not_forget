import 'package:flutter/material.dart';
import '../services/storage_service.dart';
import '../services/notification_service.dart';
import '../utils/constants.dart';

class SettingsProvider with ChangeNotifier {
  final StorageService _storage = StorageService();
  final NotificationService _notification = NotificationService();

  String _notificationMode = Constants.defaultNotificationMode;
  String _alarmSound = Constants.defaultAlarmSound;

  String get notificationMode => _notificationMode;
  String get alarmSound => _alarmSound;

  SettingsProvider() {
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    _notificationMode = _storage.getNotificationMode();
    _alarmSound = _storage.getAlarmSound();
    notifyListeners();
  }

  Future<void> setNotificationMode(String mode) async {
    _notificationMode = mode;
    await _storage.setNotificationMode(mode);
    
    // 모든 알림 재스케줄
    await _notification.rescheduleAllNotifications();
    
    notifyListeners();
  }

  Future<void> setAlarmSound(String soundFileName) async {
    _alarmSound = soundFileName;
    await _storage.setAlarmSound(soundFileName);
    
    // 멜로디 모드인 경우에만 알림 재스케줄
    if (_notificationMode == Constants.notificationModeMelody) {
      await _notification.rescheduleAllNotifications();
    }
    
    notifyListeners();
  }

  String get notificationModeDisplayName {
    switch (_notificationMode) {
      case Constants.notificationModeMelody:
        return '멜로디';
      case Constants.notificationModeVibrate:
        return '진동';
      case Constants.notificationModeSilent:
        return '무음';
      default:
        return '알 수 없음';
    }
  }
}

