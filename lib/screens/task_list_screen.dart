import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/task_provider.dart';
import '../widgets/task_card.dart';
import 'task_edit_screen.dart';

class TaskListScreen extends StatelessWidget {
  const TaskListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('전체 항목'),
      ),
      body: Consumer<TaskProvider>(
        builder: (context, taskProvider, _) {
          final tasks = taskProvider.tasks;
          
          if (tasks.isEmpty) {
            return const Center(
              child: Text(
                '항목이 없습니다',
                style: TextStyle(fontSize: 16, color: Color(0xFFB0B0B0)),
              ),
            );
          }
          
          return ListView.builder(
            padding: const EdgeInsets.symmetric(vertical: 8),
            itemCount: tasks.length,
            itemBuilder: (context, index) {
              final task = tasks[index];
              return TaskCard(
                task: task,
                showFullInfo: true,
                onComplete: () => taskProvider.markAsCompleted(task),
                onToggle: () => taskProvider.toggleTaskEnabled(task),
                onEdit: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => TaskEditScreen(task: task),
                    ),
                  );
                },
                onDelete: () => _confirmDelete(context, taskProvider, task),
              );
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const TaskEditScreen()),
          );
        },
        backgroundColor: const Color(0xFF39FF14),
        child: const Icon(Icons.add, color: Color(0xFF0A0E27)),
      ),
    );
  }

  Future<void> _confirmDelete(BuildContext context, TaskProvider taskProvider, task) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('삭제 확인'),
        content: const Text('이 항목을 삭제하시겠습니까?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('취소'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('삭제'),
          ),
        ],
      ),
    );
    
    if (confirmed == true) {
      await taskProvider.deleteTask(task);
    }
  }
}

