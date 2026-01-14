import 'package:audioplayers/audioplayers.dart';

class AudioService {
  static final AudioService _instance = AudioService._internal();
  factory AudioService() => _instance;
  AudioService._internal();

  final AudioPlayer _player = AudioPlayer();

  // 미리듣기 재생
  Future<void> playPreview(String soundFileName) async {
    try {
      await _player.stop();
      await _player.play(AssetSource('sound/$soundFileName'));
    } catch (e) {
      print('Error playing sound: $e');
    }
  }

  // 재생 중지
  Future<void> stop() async {
    await _player.stop();
  }

  // 리소스 정리
  void dispose() {
    _player.dispose();
  }
}

