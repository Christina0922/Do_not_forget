import 'package:hive/hive.dart';
import '../models/task.dart';
import '../utils/constants.dart';

class StorageService {
  static final StorageService _instance = StorageService._internal();
  factory StorageService() => _instance;
  StorageService._internal();

  Box<Task>? _tasksBox;
  Box? _settingsBox;

  Future<void> initialize() async {
    _tasksBox = await Hive.openBox<Task>(Constants.tasksBox);
    _settingsBox = await Hive.openBox(Constants.settingsBox);
    
    // 첫 실행 시 기본 항목 생성
    await _createDefaultTasks();
  }

  // 기본 4개 항목 생성 (중복 방지)
  Future<void> _createDefaultTasks() async {
    if (_tasksBox == null) return;
    
    // 이미 항목이 있으면 생성하지 않음
    if (_tasksBox!.isNotEmpty) return;

    final defaultTitles = [
      '마스크 챙겨오기',
      '응가역사 숙제하기',
      '과학 숙제하기',
      '사회 숙제하기',
    ];

    final now = DateTime.now();
    final tomorrow8AM = DateTime(now.year, now.month, now.day + 1, 8, 0);

    for (final title in defaultTitles) {
      final task = Task(
        id: DateTime.now().millisecondsSinceEpoch.toString() + title.hashCode.toString(),
        title: title,
        dateTime: tomorrow8AM,
        enabled: true,
      );
      await _tasksBox!.put(task.id, task);
    }
  }

  // Task CRUD
  Future<void> saveTask(Task task) async {
    await _tasksBox?.put(task.id, task);
  }

  Future<void> deleteTask(String id) async {
    await _tasksBox?.delete(id);
  }

  List<Task> getAllTasks() {
    return _tasksBox?.values.toList() ?? [];
  }

  Task? getTask(String id) {
    return _tasksBox?.get(id);
  }

  // 오늘 할 일 가져오기
  List<Task> getTodayTasks() {
    final tasks = getAllTasks();
    final now = DateTime.now();
    
    return tasks.where((task) {
      return task.enabled &&
          task.dateTime.year == now.year &&
          task.dateTime.month == now.month &&
          task.dateTime.day == now.day;
    }).toList()
      ..sort((a, b) => a.dateTime.compareTo(b.dateTime));
  }

  // 다가오는 할 일 가져오기
  List<Task> getUpcomingTasks() {
    final tasks = getAllTasks();
    final now = DateTime.now();
    
    return tasks.where((task) {
      return task.enabled && task.dateTime.isAfter(now);
    }).toList()
      ..sort((a, b) => a.dateTime.compareTo(b.dateTime));
  }

  // 설정 관련
  Future<void> setNotificationMode(String mode) async {
    await _settingsBox?.put(Constants.keyNotificationMode, mode);
  }

  String getNotificationMode() {
    return _settingsBox?.get(
      Constants.keyNotificationMode,
      defaultValue: Constants.defaultNotificationMode,
    ) ?? Constants.defaultNotificationMode;
  }

  Future<void> setAlarmSound(String soundFileName) async {
    await _settingsBox?.put(Constants.keyAlarmSound, soundFileName);
  }

  String getAlarmSound() {
    return _settingsBox?.get(
      Constants.keyAlarmSound,
      defaultValue: Constants.defaultAlarmSound,
    ) ?? Constants.defaultAlarmSound;
  }
}

