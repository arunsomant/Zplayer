import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'tvplayer_method_channel.dart';

abstract class TvplayerPlatform extends PlatformInterface {
  /// Constructs a TvplayerPlatform.
  TvplayerPlatform() : super(token: _token);

  static final Object _token = Object();

  static TvplayerPlatform _instance = MethodChannelTvplayer();

  /// The default instance of [TvplayerPlatform] to use.
  ///
  /// Defaults to [MethodChannelTvplayer].
  static TvplayerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [TvplayerPlatform] when
  /// they register themselves.
  static set instance(TvplayerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<int?> openPlayer(Map data) {
    throw UnimplementedError('openPlayer() has not been implemented.');
  }
}
