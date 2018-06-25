package com.uis.lib.simpleplayer.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.uis.lib.simpleplayer.R;

/**
 * @author uis on 2017/11/25.
 */

public abstract class BasePlayerLayout extends RelativeLayout {

    public final static int MaxRate = 1000;
    public static int sTimerMills = 3500;
    protected PlayerView player;
    protected PlayerComplete mComplete;
    protected PlayerCallback mCallback;
    protected int totalTime=0;
    protected int currentTime = 0;

    private String mUrl;
    private String thumbUrl;
    private boolean isFullScreen = false;
    private boolean hasFullscreen = false;
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

    private void innerInit(){
        mCounter = PlayerCounter.createCounter();
        init();
    }

    private void initPlayer(){
        if(player == null){
            player = PlayerUtils.initPlayer(getContext().getApplicationContext());
        }
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
        initPlayer();
        setFullScreen(isFull);
        if(player.getParent()!=null && player.getParent() instanceof ViewGroup){
            ViewGroup vg = (ViewGroup)player.getParent();
            vg.removeView(player);
        }
        return player;
    }

    public <T extends View> T id(int resId){
        return (T)findViewById(resId);
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

    public boolean isPlaying(){
        return player!=null && player.isPlaying();
    }

    public boolean isRelease(){
        return player==null || player.isRelease();
    }

    protected void setFullScreen(boolean isFull){
        isFullScreen = isFull;
        if(player!=null) {
            player.setFullScreen(isFull);
        }
    }

    public boolean isFullScreen(){
        return isFullScreen;
    }

    protected void setDataSource(){
        if(player!=null) {
            player.setDataSource(getContext().hashCode(),mUrl, mCallback,isFullScreen ? null : mComplete);
        }
    }

    protected void resize(){
        PlayerCounter.mHandler.postDelayed(new InnerResize(player),32);
    }

    static class InnerResize implements Runnable{
        PlayerView playerView;

        public InnerResize(PlayerView playerView) {
            this.playerView = playerView;
        }

        @Override
        public void run() {
            if (playerView != null) {
                playerView.resize();
            }
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
