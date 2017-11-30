package com.uis.lib.simpleplayer.player;

/**
 * @author uis on 2017/11/21.
 */

final class PlayerEntity {
    public String url;
    public int current = 0;
    public boolean canPrepare = true;
    public boolean canPlay = false;
    public boolean isPause = false;

    public PlayerEntity(String url) {
        this.url = url;
    }
}
