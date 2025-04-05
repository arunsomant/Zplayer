
import 'tvplayer_platform_interface.dart';

class Tvplayer {
  Future<String?> getPlatformVersion() {
    return TvplayerPlatform.instance.getPlatformVersion();
  }

  Future<int?> openPlayer(Map data) {
    return TvplayerPlatform.instance.openPlayer(data);
  }
}
