package com.exoplayer.tv.tvplayer;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import com.exoplayer.tv.tvplayer.PlayerActivity;
import com.exoplayer.tv.tvplayer.MediaMetaData;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.net.Uri;
import android.content.Context;
import android.util.Log;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
/** TvplayerPlugin */
public class TvplayerPlugin implements FlutterPlugin, MethodCallHandler,ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  public MethodChannel channel;
  public Context context;
  public MethodChannel.Result result;
  public Activity activity;
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "tvplayer");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    this.result = result;
    if (call.method.equals("openPlayer")) {
      Intent intent = new Intent(context, PlayerActivity.class);

      String url = call.argument("videoUrl");
      String videoUrl = call.argument("videoUrl");
      String subsUrl = call.argument("subtitleUrl");
      String title = call.argument("title");
      String description = call.argument("description");
      Boolean isLive = call.argument("isLive");
      int position = call.argument("position");
      Log.i("24TV", "position methodChannel "+position);
      int id = call.argument("id");
      if(subsUrl == null || subsUrl.isEmpty() || subsUrl.trim().isEmpty()){
        subsUrl = null;
      }

      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      MediaMetaData mediaMetaData = new MediaMetaData(
              Uri.parse(url), (subsUrl!=null ? Uri.parse(subsUrl) : null), videoUrl,
              title, description, null, id, null, isLive,position
      );
      /*Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
              .toBundle();*/
      intent.putExtra(PlayerActivity.TAG, mediaMetaData);
      intent.setFlags(0);
      activity.startActivityForResult(intent,1024);
    } else {
      result.notImplemented();
    }
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }
  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }
  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }
  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }


  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i("24TV", "onActivityResult callback received");
    Log.i("24TV", "onActivityResult requestCode:"+requestCode);
    Log.i("24TV", "onActivityResult resultCode:"+resultCode);
    result.success(32);
    if (resultCode == Activity.RESULT_OK) {
      Log.i("24TV", "onActivityResult is OK");
      int re = data.getIntExtra("resume_position",0);
      Log.i("24TV", ""+re);
      result.success(re);
    }
    return  true;
  }
}
