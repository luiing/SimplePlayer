package com.uis.lib.simpleplayer.player;

/**
 * @author uis on 2017/7/26.
 */

public abstract class PlayerCallback {
    public  void onPrepared(PlayerListener vp){}
    public  void onBufferingUpdate(PlayerListener vp, int percent){}
    public  void onCompletion(PlayerListener vp){}
    public  void onSeekComplete(PlayerListener vp){}
    public  void onInfo(PlayerListener vp, int what, int extra){}
    public  void onError(PlayerListener vp, int what, int extra){}
    public  void onProgress(int current,int total){}
    public  void onVideoSizeChanged(PlayerListener vp, int width, int height){}
}
