import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'tvplayer_platform_interface.dart';

/// An implementation of [TvplayerPlatform] that uses method channels.
class MethodChannelTvplayer extends TvplayerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('tvplayer');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<int?> openPlayer(Map data) async {
    final position = await methodChannel.invokeMethod<int>('openPlayer', data);
    return position;
  }
}
