package com.exoplayer.tv.tvplayer;

public interface PlayerWatchTimeListener {

    void playerPaused(long position);
    void playerPlayed(long position);
    void playerSeekForwardTime(long position);
    void playerSeekBackwardTime(long position);
    void playerSeekPosition(long position);
}
