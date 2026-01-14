import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/task.dart';
import '../utils/theme.dart';

class TaskCard extends StatelessWidget {
  final Task task;
  final bool showFullInfo;
  final VoidCallback? onComplete;
  final VoidCallback? onToggle;
  final VoidCallback? onEdit;
  final VoidCallback? onDelete;

  const TaskCard({
    super.key,
    required this.task,
    this.showFullInfo = false,
    this.onComplete,
    this.onToggle,
    this.onEdit,
    this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    final timeFormat = DateFormat('HH:mm');
    final dateFormat = DateFormat('M/d (E)', 'ko_KR');

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 제목 + 상태
            Row(
              children: [
                Expanded(
                  child: Text(
                    task.title,
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                _buildStatusBadge(),
              ],
            ),

            const SizedBox(height: 12),

            // 날짜/시간
            Row(
              children: [
                const Icon(Icons.schedule, size: 16, color: AppTheme.limeAccent),
                const SizedBox(width: 6),
                Text(
                  showFullInfo
                      ? '${dateFormat.format(task.dateTime)} ${timeFormat.format(task.dateTime)}'
                      : timeFormat.format(task.dateTime),
                  style: const TextStyle(
                    fontSize: 16,
                    color: AppTheme.limeAccent,
                  ),
                ),
              ],
            ),

            // 메모
            if (task.note != null && task.note!.isNotEmpty) ...[
              const SizedBox(height: 8),
              Text(
                task.note!,
                style: const TextStyle(
                  fontSize: 14,
                  color: AppTheme.textSecondary,
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
            ],

            const SizedBox(height: 16),

            // 버튼들
            Row(
              children: [
                // 완료 버튼
                if (onComplete != null && task.status?.isCompleted != true)
                  ElevatedButton(
                    onPressed: onComplete,
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                    ),
                    child: const Text('완료'),
                  ),

                const SizedBox(width: 8),

                // 편집 버튼
                if (onEdit != null)
                  OutlinedButton(
                    onPressed: onEdit,
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    ),
                    child: const Text('편집'),
                  ),

                const SizedBox(width: 8),

                // 삭제 버튼
                if (onDelete != null)
                  TextButton(
                    onPressed: onDelete,
                    style: TextButton.styleFrom(
                      foregroundColor: AppTheme.errorRed,
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    ),
                    child: const Text('삭제'),
                  ),

                const Spacer(),

                // 알림 토글
                if (onToggle != null)
                  Row(
                    children: [
                      Text(
                        task.enabled ? '켜짐' : '꺼짐',
                        style: const TextStyle(fontSize: 14),
                      ),
                      Switch(
                        value: task.enabled,
                        onChanged: (_) => onToggle?.call(),
                      ),
                    ],
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatusBadge() {
    String text;
    Color bgColor;
    Color textColor;

    if (task.status?.isCompleted == true) {
      text = '완료';
      bgColor = AppTheme.limeAccent.withOpacity(0.2);
      textColor = AppTheme.limeAccent;
    } else if (task.status?.isSeen == true) {
      text = '봤음';
      bgColor = AppTheme.textSecondary.withOpacity(0.2);
      textColor = AppTheme.textSecondary;
    } else {
      text = '안 봄';
      bgColor = AppTheme.errorRed.withOpacity(0.2);
      textColor = AppTheme.errorRed;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        text,
        style: TextStyle(
          fontSize: 12,
          fontWeight: FontWeight.bold,
          color: textColor,
        ),
      ),
    );
  }
}

