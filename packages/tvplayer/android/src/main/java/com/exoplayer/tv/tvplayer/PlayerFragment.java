/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.exoplayer.tv.tvplayer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.widget.PlaybackControlsRow;

import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.material.snackbar.Snackbar;
import android.os.Handler;
import java.util.concurrent.TimeUnit;

public class PlayerFragment extends VideoSupportFragment {

//    private static final String URL = "https://storage.googleapis.com/shaka-demo-assets/angel-one-hls/hls.m3u8";
    private static final String URL = "https://vz-fa6a66b7-f16.b-cdn.net/9c53dea5-510a-4c5d-9682-a82cf3da0c3c/playlist.m3u8";
    private static final String SUBTITLE = "https://vz-fa6a66b7-f16.b-cdn.net/9c53dea5-510a-4c5d-9682-a82cf3da0c3c/captions/EN.vtt";
    public static final String TAG = "VideoConsExoPlayer";
    private VideoMediaPlayerGlue<ExoPlayerAdapter> mMediaPlayerGlue;
    final VideoSupportFragmentGlueHost mHost = new VideoSupportFragmentGlueHost(this);

    private PlayerWatchTimeListener mPlayerWatchTimeListener;
    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener
            = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int state) {
        }
    };
    private ExoPlayerAdapter playerAdapter;
    private Activity playerActivity;
    private int videoId;
    private final int interval = 5000; // 5 Seconds
    private Handler handler = new Handler();
    private Runnable runnable;
    private Long pausePos,firstPausePos,playPos,seekFwdPos,seekBackPos,seekPos, prevPlayPos, prevSeekFwdPos, prevSeekBackPos, watchTime, newPlayPos, wt;
    private String mWatchTime,mFirstPausePos ,mPausePos,mPlayPos,mSeekFwdPos,mSeekBackPos,mSeekPos, mWatchDiff, mPrevPlayPos, mNewPlayPos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerActivity = getActivity();
        pausePos = 0L;
        playPos = 0L;
        seekFwdPos = 0L;
        seekBackPos = 0L;
        seekPos = 0L;
        firstPausePos = 0L;
        prevPlayPos = 0L;
        prevSeekFwdPos = 0L;
        prevSeekBackPos = 0L;
        watchTime = 0L;
        newPlayPos = 0L;
        wt = 0L;
        mPlayerWatchTimeListener = new PlayerWatchTimeListener() {
            @Override
            public void playerPaused(long position) {  if (pausePos == 0L && firstPausePos == 0L){
                mFirstPausePos = milliConvert(position);
                firstPausePos = position;
                SharedPreferences sharedPref = getContext().getSharedPreferences("FlutterSharedPreferences",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                watchTime = position;
                mWatchTime = String.valueOf(watchTime);
                Log.d("time", "watchTime playerPaused if  l88" + mWatchTime);
                editor.putString(PlayerActivity.PLAYER_WATCH_TIME_POS, mWatchTime);
                editor.apply();
            } else if (pausePos == 0L){
                mPausePos = milliConvert(position);
                Log.d("time", "watchTime playerPaused " + mPausePos);
                Log.d("time", "watchTime playerFirstPaused " + mFirstPausePos);
                pausePos = position;
                wt = pausePos - firstPausePos;
                SharedPreferences sharedPref = getContext().getSharedPreferences("FlutterSharedPreferences",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                watchTime = wt + watchTime;
                mWatchTime = String.valueOf(watchTime);
                editor.putString(PlayerActivity.PLAYER_WATCH_TIME_POS, mWatchTime);
                editor.apply();
            } else if (position > pausePos){
                wt = position - pausePos;
                pausePos = position;
                SharedPreferences sharedPref = getContext().getSharedPreferences("FlutterSharedPreferences",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                watchTime = wt + watchTime;
                mWatchTime = String.valueOf(watchTime);
                Log.d("time", "watchTime playerPaused  l109" + mWatchTime);
                editor.putString(PlayerActivity.PLAYER_WATCH_TIME_POS, mWatchTime);
                editor.apply();
            }

            }

            @Override
            public void playerPlayed(long position) {Log.d("time", "watchTime prev playerPlayed " + mPlayPos);
                mPlayPos = milliConvert(position);
                Log.d("time", "watchTime playerPlayed " + mPlayPos);
                prevPlayPos = playPos;
                mPrevPlayPos = milliConvert(prevPlayPos);
                playPos = position;
                if (pausePos < playPos){
                    pausePos = playPos;
                }
            }

            @Override
            public void playerSeekForwardTime(long position) {
//                mSeekFwdPos = milliConvert(position);
//                seekFwdPos = position;
//                Log.d("time", "watchTime playerSeekForwardTime " + mSeekFwdPos);

                if (!playPos.equals(position)) {
                    mSeekFwdPos = milliConvert(position);
                    mPlayPos = milliConvert(playPos);
                    Log.d("time", "seek forwarded position from " + mSeekFwdPos);
                    Log.d("time", "last play position from " + mPlayPos);
                    wt = position - playPos;
                    mWatchDiff = milliConvert(wt);
                    Log.d("time", "watchTime diff " + mWatchDiff);
                    playPos = position + 10_000;
                    pausePos = position + 10_000;
                    SharedPreferences sharedPref = getContext().getSharedPreferences("FlutterSharedPreferences",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    watchTime = wt + watchTime;
                    mWatchTime = String.valueOf(watchTime);
                    mSeekPos =  milliConvert(watchTime);
                    editor.putString(PlayerActivity.PLAYER_WATCH_TIME_POS, mWatchTime);
                    editor.apply();
                    Log.d("time", "watchTime " + mSeekPos);
                }
            }

            @Override
            public void playerSeekBackwardTime(long position) {
//                prevSeekBackPos = seekBackPos;
//                mSeekBackPos = milliConvert(position);
//                Log.d("time", "watchTime playerSeekBackwardTime " + mSeekBackPos);
//                seekBackPos = position;
//                long exactSeekBPos = seekBackPos - 10_000;
//                if (exactSeekBPos < firstPausePos && exactSeekBPos >= 0L){
//                    mNewPlayPos = milliConvert(position);
//                    newPlayPos = position;
//                }
//                if (prevPlayPos <= 0) {
//                    long diff;
//                    diff = seekBackPos - prevPlayPos;
//                    watchTime = watchTime + diff;
//                    mWatchTime = milliConvert(watchTime);
//                    Log.d("time", "watchTime WatchTime " + mWatchTime);
//                    prevPlayPos = seekBackPos;
//                }
               /* SharedPreferences sharedPref = getContext().getSharedPreferences("FlutterSharedPreferences",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                watchTime = position + watchTime;
                mWatchTime = String.valueOf(watchTime);
                editor.putString(PlayerActivity.PLAYER_WATCH_TIME_POS, mWatchTime);
                editor.apply();*/
            }

            @Override
            public void playerSeekPosition(long position) {
//                mSeekPos = milliConvert(position);
//                Log.d("time", "watchTime playerSeekPosition " + mSeekPos);
//                seekPos = position;
//                if (seekPos < firstPausePos && seekPos >= 0L){
//                    mNewPlayPos = milliConvert(position);
//                    newPlayPos = position;
//                }
                seekPos = position;
            }
        };
        playerAdapter = new ExoPlayerAdapter(getActivity(), mPlayerWatchTimeListener);
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);
        mMediaPlayerGlue = new VideoMediaPlayerGlue<>(getActivity(), playerAdapter);
        mMediaPlayerGlue.setHost(mHost);
        AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(TAG, "video player cannot obtain audio focus!");
        }

        MediaMetaData intentMetaData = getActivity().getIntent().getParcelableExtra(
                PlayerActivity.TAG);
        if (intentMetaData != null) {
            videoId = intentMetaData.getMediaAlbumArtResId();
            mMediaPlayerGlue.setTitle(intentMetaData.getMediaTitle());
            mMediaPlayerGlue.setSubtitle(intentMetaData.getMediaArtistName());
            if (intentMetaData.getStartPosition() > 0) {
                Log.i("24TV", "position setting "+intentMetaData.getStartPosition());
                playerAdapter.initialPosition = intentMetaData.getStartPosition();
            }
            Log.w(TAG, "subs: "+intentMetaData.getmMediaSubsUri());
            mMediaPlayerGlue.getPlayerAdapter().setDataSource(
                    Uri.parse(intentMetaData.getMediaSourcePath()), intentMetaData.getmMediaSubsUri());
            if (intentMetaData.isLive()) {
                mMediaPlayerGlue.setSeekProvider(null);
                mMediaPlayerGlue.setSeekEnabled(false);
            } else {
                mMediaPlayerGlue.setSeekProvider(new PlaybackSeekMetadataDataProvider(getActivity(), intentMetaData.getMediaSourcePath(), 10000));
            }
            if (intentMetaData.getPositionCallback() != null) {
                runnable = () -> {
                    try {
                        Log.d("Nzm","timer hit:"+playerAdapter.mPlayer.getCurrentPosition());
                        intentMetaData.getPositionCallback().onPositionUpdated(playerAdapter.mPlayer.getCurrentPosition());
                    } finally {
                        handler.postDelayed(runnable, interval);
                    }
                };
            }
        }/* else {
            mMediaPlayerGlue.setTitle("Clear hls - Angel one");
            mMediaPlayerGlue.setSubtitle("Example with subs and quality");
            mMediaPlayerGlue.getPlayerAdapter().setDataSource(Uri.parse(URL), Uri.parse(SUBTITLE));
            mMediaPlayerGlue.setSeekProvider(new PlaybackSeekMetadataDataProvider(getActivity(), URL, 10000));
        }*/
        mMediaPlayerGlue.playWhenPrepared();
        hideControlsOverlay(false);
        setBackgroundType(BG_LIGHT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        SubtitleView subtitleView = view.findViewById(R.id.leanback_subtitles);
        if (playerAdapter != null) {
            playerAdapter.setSubtitleView(subtitleView);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (runnable != null) {
            handler.postDelayed(runnable, interval);
        }
    }

    @Override
    public void onPause() {
        if (getActivity() != null) {
            SharedPreferences sharedPref = getContext().getSharedPreferences("FlutterSharedPreferences",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(PlayerActivity.PLAYER_RESUME_POS, playerAdapter.mPlayer.getCurrentPosition());
            editor.putInt(PlayerActivity.PLAYER_RESUME_VIDEO, videoId);

            /*if (pausePos < playerAdapter.mPlayer.getCurrentPosition()) {
                wt = playerAdapter.mPlayer.getCurrentPosition() - pausePos;
                mWatchDiff = milliConvert(wt);
                Log.d("time", "watchTime diff onPause" + mWatchDiff);
                watchTime = watchTime + wt;
                mWatchTime = String.valueOf(watchTime);
                mSeekPos = milliConvert(watchTime);
                Log.d("time", "watchTime  onPause" + mSeekPos);
                editor.putString(PlayerActivity.PLAYER_WATCH_TIME_POS, mWatchTime);
            }*/
            editor.apply();
            //Log.i("24TV Watchtime", mWatchTime);
            Log.i("24TV", "preference saved onPause");
        }
        if (mMediaPlayerGlue != null) {
            mMediaPlayerGlue.pause();
        }
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (getActivity() != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(PlayerActivity.PLAYER_RESUME_POS, playerAdapter.mPlayer.getCurrentPosition());
            SharedPreferences sharedPref = getContext().getSharedPreferences("FlutterSharedPreferences",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            if (pausePos < playerAdapter.mPlayer.getCurrentPosition()) {
                wt = playerAdapter.mPlayer.getCurrentPosition() - pausePos;
                mWatchDiff = milliConvert(wt);
                Log.d("time", "watchTime diff onStop" + mWatchDiff);
                watchTime = watchTime + wt;
                mWatchTime = String.valueOf(watchTime);
                mSeekPos = milliConvert(watchTime);
                Log.d("time", "watchTime  onStop" + mSeekPos);
                editor.putString(PlayerActivity.PLAYER_WATCH_TIME_POS, mWatchTime);
            }
            editor.apply();
            getActivity().setResult(Activity.RESULT_OK, resultIntent);
            Log.i("24TV Watchtime onStop", mWatchTime);
        }
        super.onStop();
    }


    @Override
    protected void onError(int errorCode, CharSequence errorMessage) {
        super.onError(errorCode, errorMessage);
        Snackbar.make(getView(), "Failed to playback", Snackbar.LENGTH_LONG)
                .show();
    }

    public String milliConvert(long milli){
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milli),
                TimeUnit.MILLISECONDS.toMinutes(milli) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milli)),
                TimeUnit.MILLISECONDS.toSeconds(milli) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milli)));
        return hms;
    }
}
