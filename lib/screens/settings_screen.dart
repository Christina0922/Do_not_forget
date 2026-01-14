import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/settings_provider.dart';
import '../services/audio_service.dart';
import '../utils/constants.dart';
import '../utils/theme.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('알림 설정'),
      ),
      body: Consumer<SettingsProvider>(
        builder: (context, settings, _) {
          return ListView(
            padding: const EdgeInsets.all(16),
            children: [
              // 알림 모드 선택
              _buildSectionTitle('알림 모드'),
              const SizedBox(height: 12),
              _buildNotificationModeSelector(context, settings),
              
              const SizedBox(height: 32),
              
              // 멜로디 선택 (멜로디 모드일 때만 표시)
              if (settings.notificationMode == Constants.notificationModeMelody) ...[
                _buildSectionTitle('멜로디 선택'),
                const SizedBox(height: 12),
                _buildMelodySelector(context, settings),
              ],
              
              const SizedBox(height: 24),
              _buildInfoCard(),
            ],
          );
        },
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Text(
      title,
      style: const TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.bold,
        color: AppTheme.limeAccent,
      ),
    );
  }

  Widget _buildNotificationModeSelector(BuildContext context, SettingsProvider settings) {
    return Card(
      child: Column(
        children: [
          _buildModeOption(
            context,
            settings,
            '🎵 멜로디',
            '선택한 멜로디가 울립니다',
            Constants.notificationModeMelody,
          ),
          const Divider(height: 1),
          _buildModeOption(
            context,
            settings,
            '📳 진동',
            '진동만 울립니다',
            Constants.notificationModeVibrate,
          ),
          const Divider(height: 1),
          _buildModeOption(
            context,
            settings,
            '🔕 무음',
            '알림만 표시됩니다',
            Constants.notificationModeSilent,
          ),
        ],
      ),
    );
  }

  Widget _buildModeOption(
    BuildContext context,
    SettingsProvider settings,
    String title,
    String subtitle,
    String mode,
  ) {
    final isSelected = settings.notificationMode == mode;
    
    return ListTile(
      title: Text(
        title,
        style: TextStyle(
          fontSize: 16,
          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
          color: isSelected ? AppTheme.limeAccent : AppTheme.textPrimary,
        ),
      ),
      subtitle: Text(
        subtitle,
        style: const TextStyle(fontSize: 14, color: AppTheme.textSecondary),
      ),
      trailing: isSelected
          ? const Icon(Icons.check_circle, color: AppTheme.limeAccent)
          : const Icon(Icons.circle_outlined, color: AppTheme.textSecondary),
      onTap: () {
        settings.setNotificationMode(mode);
      },
    );
  }

  Widget _buildMelodySelector(BuildContext context, SettingsProvider settings) {
    return Card(
      child: Column(
        children: Constants.alarmSounds.map((sound) {
          final isSelected = settings.alarmSound == sound.fileName;
          
          return Column(
            children: [
              ListTile(
                leading: Text(
                  sound.emoji,
                  style: const TextStyle(fontSize: 24),
                ),
                title: Text(
                  sound.displayName,
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                    color: isSelected ? AppTheme.limeAccent : AppTheme.textPrimary,
                  ),
                ),
                trailing: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    IconButton(
                      icon: const Icon(Icons.play_arrow, color: AppTheme.limeAccent),
                      onPressed: () {
                        AudioService().playPreview(sound.fileName);
                      },
                      tooltip: '미리듣기',
                    ),
                    const SizedBox(width: 8),
                    isSelected
                        ? const Icon(Icons.check_circle, color: AppTheme.limeAccent)
                        : const Icon(Icons.circle_outlined, color: AppTheme.textSecondary),
                  ],
                ),
                onTap: () {
                  settings.setAlarmSound(sound.fileName);
                },
              ),
              if (sound != Constants.alarmSounds.last) const Divider(height: 1),
            ],
          );
        }).toList(),
      ),
    );
  }

  Widget _buildInfoCard() {
    return Card(
      color: AppTheme.charcoal,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: const [
            Text(
              '💡 알림 설정 안내',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                color: AppTheme.limeAccent,
              ),
            ),
            SizedBox(height: 12),
            Text(
              '• 멜로디: 선택한 사운드가 재생됩니다\n'
              '• 진동: 사운드 없이 진동만 동작합니다\n'
              '• 무음: 알림만 조용히 표시됩니다\n\n'
              '설정을 변경하면 모든 예정된 알림에 적용됩니다.',
              style: TextStyle(
                fontSize: 14,
                color: AppTheme.textSecondary,
                height: 1.5,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

