package com.uis.lib.simpleplayer;

/**
 * 播放状态回调
 * @author uis on 2017/12/5.
 */

public abstract class PlayerStateCallback {
    public void onPlay(boolean inInit){}
    public void onPause(){}
    public void onCompletion(){}
    public void onState(int state){}
}
