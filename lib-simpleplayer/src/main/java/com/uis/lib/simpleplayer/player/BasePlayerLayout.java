package com.uis.lib.simpleplayer.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.uis.lib.simpleplayer.R;
import com.uis.lib.simpleplayer.Vlog;

/**
 * @author uis on 2017/11/25.
 */

public abstract class BasePlayerLayout extends RelativeLayout {

    public final static int MaxRate = 1000;
    public static int sTimerMills = 5000;
    protected static PlayerView sPlayer;
    protected PlayerView player;
    protected PlayerComplete mComplete;
    protected PlayerCallback mCallback;
    protected int totalTime=0;
    protected int currentTime = 0;

    private String mUrl;
    private String thumbUrl;
    private boolean isFullScreen = false;
    private boolean hasFullscreen = false;
    private boolean isLand = false;
    private PlayerCounter mCounter;

    protected TypedArray mATTR;

    public BasePlayerLayout(@NonNull Context context) {
        this(context,null);
    }

    public BasePlayerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BasePlayerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mATTR = context.obtainStyledAttributes(attrs, R.styleable.PlayerLayout, defStyleAttr,0);
        innerInit();
    }

    @TargetApi(21)
    public BasePlayerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mATTR = context.obtainStyledAttributes(attrs, R.styleable.PlayerLayout, defStyleAttr,defStyleRes);
        innerInit();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resize();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopTimer();
        super.onDetachedFromWindow();
    }

    private void innerInit(){
        mCounter = PlayerCounter.createCounter();
        if(sPlayer == null) {
            sPlayer = new PlayerView(getContext());
            sPlayer.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    ));
        }
        init();
    }

    protected void stopTimer(){
        mCounter.stopTimer();
    }

    protected void startTimer(){
        mCounter.startTimer(true,false,sTimerMills,new Runnable() {
            @Override
            public void run() {
                onCounter(false);
            }
        });
    }

    protected abstract void init();

    protected void onCounter(boolean userStop){

    }

    protected PlayerView createPlayerView(boolean isFull){
        if(player == null){
            player = sPlayer;
        }
        setFullScreen(isFull);
        if(sPlayer.getParent()!=null && sPlayer.getParent() instanceof ViewGroup){
            ViewGroup vg = (ViewGroup)sPlayer.getParent();
            vg.removeView(player);
        }
        return player;
    }

    public <T extends View> T id(int resId){
        return (T)findViewById(resId);
    }

    public static double getRate(int maxRate,int current,int total){
        return maxRate*(1.0d*current/total);
    }

    public static String getTime(int current){
        StringBuilder builder = new StringBuilder();
        current /= 1000;
        if(current>60){//60s
            if(current<600){//600s
                builder.append("0");
            }
            builder.append(current/60);
        }else{
            builder.append("00");
        }
        builder.append(":");
        int seconds = current%60;
        if(seconds<10){//10s
            builder.append("0");
        }
        builder.append(seconds);
        return builder.toString();
    }

    protected void seekTo(int seek){
        if(player!=null) {
            player.seekTo(seek);
        }
    }

    protected void prepare(){
        if(player!=null) {
            player.prepare();
        }
    }

    protected void pause(){
        if(player!=null) {
            player.pause();
        }
    }

    protected boolean isPlaying(){
        return player!=null && player.isPlaying();
    }

    protected boolean isRelease(){
        return player==null || player.isRelease();
    }

    protected void setFullScreen(boolean isFull){
        isFullScreen = isFull;
        if(player!=null) {
            player.setFullScreen(isFull);
        }
    }

    protected boolean isFullScreen(){
        return isFullScreen;
    }

    protected void setDataSource(){
        if(player!=null) {
            player.setDataSource(mUrl, mCallback,isFullScreen ? null : mComplete);
        }
    }

    protected void resize(){
        if(player!=null) {
            player.resize();
        }
    }

    protected void onChanged(){
        if(player!=null){
            player.onChanged();
        }
    }

    protected boolean hasFullScreen(){
        return hasFullscreen;
    }

    protected void setHasFullScreen(boolean hasFull){
        this.hasFullscreen = hasFull;
    }

    protected void setVideoUrl(String url){
        mUrl = url;
    }

    protected String getVideoUrl(){
        return mUrl;
    }

    protected void setThumbUrl(String url){
        thumbUrl = url;
    }

    protected String getThumbUrl(){
        return thumbUrl;
    }
}
