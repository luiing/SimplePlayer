package com.uis.lib.simpleplayer.player;

/**
 * @author uis on 2017/11/25.
 */

public interface PlayerComplete {
    int STATE_PREPARE = 0x04;
    int STATE_START = 0x00;
    int STATE_PAUSE = 0x01;
    int STATE_RELEASE = 0x02;
    int STATE_RESET = 0x03;
    void onChanged();
    void onComplete(int state);
}
