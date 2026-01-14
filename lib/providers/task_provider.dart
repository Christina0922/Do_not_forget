import 'package:flutter/material.dart';
import '../models/task.dart';
import '../services/storage_service.dart';
import '../services/notification_service.dart';

class TaskProvider with ChangeNotifier {
  final StorageService _storage = StorageService();
  final NotificationService _notification = NotificationService();

  List<Task> _tasks = [];

  List<Task> get tasks => _tasks;
  List<Task> get todayTasks => _storage.getTodayTasks();
  List<Task> get upcomingTasks => _storage.getUpcomingTasks();

  TaskProvider() {
    loadTasks();
  }

  Future<void> loadTasks() async {
    await _storage.initialize();
    _tasks = _storage.getAllTasks();
    notifyListeners();
  }

  Future<void> addTask(Task task) async {
    await _storage.saveTask(task);
    
    if (task.enabled) {
      await _notification.scheduleNotification(task);
    }
    
    await loadTasks();
  }

  Future<void> updateTask(Task task) async {
    await _storage.saveTask(task);
    
    // 알림 재스케줄
    await _notification.cancelNotification(task);
    if (task.enabled && task.dateTime.isAfter(DateTime.now())) {
      await _notification.scheduleNotification(task);
    }
    
    await loadTasks();
  }

  Future<void> deleteTask(Task task) async {
    await _notification.cancelNotification(task);
    await _storage.deleteTask(task.id);
    await loadTasks();
  }

  Future<void> toggleTaskEnabled(Task task) async {
    final updatedTask = task.copyWith(enabled: !task.enabled);
    await updateTask(updatedTask);
  }

  Future<void> markAsCompleted(Task task) async {
    if (task.status == null) {
      task.status = TaskStatus();
    }
    task.status!.completedAt = DateTime.now();
    await _storage.saveTask(task);
    notifyListeners();
  }

  Future<void> markAsSeen(Task task) async {
    if (task.status == null) {
      task.status = TaskStatus();
    }
    if (task.status!.seenAt == null) {
      task.status!.seenAt = DateTime.now();
      await _storage.saveTask(task);
      notifyListeners();
    }
  }
}

