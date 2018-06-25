package com.uis.lib.simpleplayer.player;

import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author uis on 2017/11/25.
 */

public class PlayerCounter {

    public static Handler mHandler = new Handler(Looper.getMainLooper());
    private Timer mTimer;

    public static PlayerCounter createCounter(){
        return new PlayerCounter();
    }

    private PlayerCounter() {
    }

    public void startTimer(int mills,Runnable run){
        this.startTimer(true,true,mills,run);
    }

    public void startTimer(boolean isMainLoop,boolean isLooper,int mills,Runnable run){
        if(mTimer == null) {
            mTimer = new Timer();
            if(isLooper) {
                mTimer.schedule(getTimerTask(isMainLoop, run), 0, mills);
            }else{
                mTimer.schedule(getTimerTask(isMainLoop, run),mills);
            }
        }
    }

    public void stopTimer(){
        if(mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 每次需要重新创建，因为stop时候有cancel
     * @return
     */
    private TimerTask getTimerTask(final boolean isMainLoop, final Runnable run){
        return new TimerTask() {
            @Override
            public void run() {
                if(mTimer == null){
                    return;
                }
                if(isMainLoop){
                    mHandler.post(run);
                }else{
                    run.run();
                }
            }
        };
    }
}
