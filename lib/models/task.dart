import 'package:hive/hive.dart';

part 'task.g.dart';

@HiveType(typeId: 0)
class Task extends HiveObject {
  @HiveField(0)
  String id;
  
  @HiveField(1)
  String title;
  
  @HiveField(2)
  DateTime dateTime; // 날짜 + 시간
  
  @HiveField(3)
  bool enabled;
  
  @HiveField(4)
  String? note;
  
  @HiveField(5)
  DateTime createdAt;
  
  @HiveField(6)
  DateTime updatedAt;
  
  @HiveField(7)
  TaskStatus? status;

  Task({
    required this.id,
    required this.title,
    required this.dateTime,
    this.enabled = true,
    this.note,
    DateTime? createdAt,
    DateTime? updatedAt,
    this.status,
  })  : createdAt = createdAt ?? DateTime.now(),
        updatedAt = updatedAt ?? DateTime.now();

  Task copyWith({
    String? title,
    DateTime? dateTime,
    bool? enabled,
    String? note,
    DateTime? updatedAt,
    TaskStatus? status,
  }) {
    return Task(
      id: id,
      title: title ?? this.title,
      dateTime: dateTime ?? this.dateTime,
      enabled: enabled ?? this.enabled,
      note: note ?? this.note,
      createdAt: createdAt,
      updatedAt: updatedAt ?? DateTime.now(),
      status: status ?? this.status,
    );
  }

  // 오늘 할 일인지 확인
  bool isToday() {
    final now = DateTime.now();
    return dateTime.year == now.year &&
        dateTime.month == now.month &&
        dateTime.day == now.day;
  }

  // 지난 할 일인지 확인
  bool isPast() {
    return dateTime.isBefore(DateTime.now());
  }
}

@HiveType(typeId: 1)
class TaskStatus extends HiveObject {
  @HiveField(0)
  DateTime? seenAt;
  
  @HiveField(1)
  DateTime? completedAt;

  TaskStatus({
    this.seenAt,
    this.completedAt,
  });

  bool get isSeen => seenAt != null;
  bool get isCompleted => completedAt != null;
}

