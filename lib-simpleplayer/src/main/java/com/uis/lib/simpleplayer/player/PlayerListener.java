package com.uis.lib.simpleplayer.player;

import android.view.Surface;

/**
 * 播放控制
 * @author uis on 2017/7/26.
 */

public interface PlayerListener {
    void setVolume(float leftVolume, float rightVolume);
    void setLooping(boolean isLoop);
    void setVideoScalingMode(int mode);
    void setSurface(Surface surface);

    void prepare(String key);
    void start();
    void seekTo(int millsec);
    void pause();
    void release();
    void releaseAll();

    int getDuration();
    int getCurrentPosition();
    int getVideoWidth();
    int getVideoHeight();
    void onProgress(int current);
    void registerPlayer(String key, PlayerCallback callback, PlayerComplete complete);
    void onChanged(String key);

    boolean isPlaying(String key);
    boolean isPlaying();
    boolean isRelease();
}
