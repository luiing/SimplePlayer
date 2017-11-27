package com.uis.lib.simpleplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uis.lib.simpleplayer.player.BasePlayerLayout;
import com.uis.lib.simpleplayer.player.PlayerCallback;
import com.uis.lib.simpleplayer.player.PlayerComplete;
import com.uis.lib.simpleplayer.player.PlayerListener;
import com.uis.lib.simpleplayer.player.PlayerUtils;

/**
 * 播放控制器界面
 * @author uis on 2017/11/21.
 */

public class PlayerLayout extends BasePlayerLayout implements View.OnClickListener{
    private FrameLayout frameLayout;
    private FrameLayout playerFrame;
    private SimpleDraweeView thumb;
    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivBack;
    private ImageView ivClose;
    private ProgressBar pbarLoading;
    private ProgressBar pbarRate;
    private LinearLayout llRate;
    private TextView tvPlayTime;
    private TextView tvTotalTime;
    private ImageView ivFullscreen;
    private SeekBar sbarRate;
    private boolean isInit = false;
    private boolean isSeeking = false;

    public PlayerLayout(@NonNull Context context) {
        this(context,null);
    }

    public PlayerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public PlayerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init(){
        setBackgroundColor(Color.BLACK);
        setClickable(true);
        inflate(getContext(), R.layout.video_player_layout,this);
        frameLayout = id(R.id.player_id_frame);
        playerFrame = id(R.id.player_id_player);
        thumb = id(R.id.player_id_thumb);
        ivPlay = id(R.id.player_id_iv_play);
        ivPause = id(R.id.player_id_iv_pause);
        pbarLoading = id(R.id.player_id_pbar_loading);
        pbarRate = id(R.id.player_id_pbar_rate);
        llRate = id(R.id.player_id_ll_rate);
        tvPlayTime = id(R.id.player_id_tv_play_time);
        tvTotalTime = id(R.id.player_id_tv_total_time);
        ivFullscreen = id(R.id.player_id_iv_fullscreen);
        sbarRate = id(R.id.player_id_seekBar);
        ivBack = id(R.id.player_id_iv_back);
        ivClose = id(R.id.player_id_iv_close);
        pbarRate.setMax(MaxRate);
        sbarRate.setMax(MaxRate);

        frameLayout.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPause.setOnClickListener(this);
        ivFullscreen.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivClose.setOnClickListener(this);

        llRate.setVisibility(VISIBLE);
        pbarRate.setVisibility(VISIBLE);

        sbarRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && tvPlayTime != null && totalTime>0){
                    int timeTxt = (int)getRate(totalTime,progress,MaxRate);
                    tvPlayTime.setText(getTime(timeTxt));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                if(isInit) {
                    int seekTime = totalTime / 1000 * seekBar.getProgress();
                    seekTo(seekTime);
                }else{
                    seekBar.setProgress(0);
                }
            }
        });

        mComplete = new PlayerComplete() {

            @Override
            public void onChanged() {
                if(!isFullScreen() && hasFullScreen()){
                    setHasFullScreen(false);
                    createPlayer(false);
                    playState(isPlaying());
                }
            }

            @Override
            public void onComplete(int state) {
                if(PlayerComplete.STATE_START == state){
                    startStateUi();
                }else {
                    if(PlayerComplete.STATE_RELEASE == state){
                        isInit = false;
                    }
                    pauseStateUi();
                }
            }
        };

        mCallback = new PlayerCallback() {
            @Override
            public void onPrepared(PlayerListener vp) {
                totalTime = vp.getDuration();
                startStateUi();
                resize();
            }

            @Override
            public void onBufferingUpdate(PlayerListener vp, int percent) {
                if(percent>98) {
                    percent = 98;
                }
                percent *= 10;
                int pb = pbarRate.getSecondaryProgress();
                if(percent > pb) {
                    pbarRate.setSecondaryProgress(percent);
                    sbarRate.setSecondaryProgress(percent);
                }
            }

            @Override
            public void onCompletion(PlayerListener vp) {
                if(isFullScreen() && isLand()) {
                    controlFullScreen(true);
                }
                pauseStateUi();
            }

            @Override
            public void onSeekComplete(PlayerListener vp) {

            }

            @Override
            public void onProgress(int current, int total) {
                if(totalTime == 0){
                    totalTime = total;
                }
                if(current != currentTime){
                    if(VISIBLE == pbarLoading.getVisibility()) {
                        pbarLoading.setVisibility(GONE);
                    }
                }else{
                    if(VISIBLE == pbarLoading.getVisibility()) {
                        pbarLoading.setVisibility(VISIBLE);
                    }
                }
                currentTime = current;
                int rate = total==0 ? 0 : (int)getRate(MaxRate,current,total);
                pbarRate.setProgress(rate);
                if(!isSeeking) {
                    sbarRate.setProgress(rate);
                    tvPlayTime.setText(getTime(current));
                }
                if(tvTotalTime.length() == 0) {
                    tvTotalTime.setText(getTime(total));
                }
            }

            @Override
            public void onVideoSizeChanged(PlayerListener vp, int width, int height) {
                resize();
            }
        };
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.player_id_frame){
            displayUi();
        }else if(id == R.id.player_id_iv_close || id == R.id.player_id_iv_back){
            controlFullScreen(true);
        }else if(id == R.id.player_id_iv_fullscreen){
            controlFullScreen(false);
        }else if(id == R.id.player_id_iv_play){
            playState(true);
        }else if(id == R.id.player_id_iv_pause){
            playState(false);
        }
    }

    private void pauseStateUi(){
        pbarLoading.setVisibility(GONE);
        ivPlay.setVisibility(VISIBLE);
        ivPause.setVisibility(GONE);
    }

    private void startStateUi(){
        pbarLoading.setVisibility(VISIBLE);
        ivPlay.setVisibility(GONE);
        ivPause.setVisibility(VISIBLE);
    }

    private void playState(boolean isPlay){
        createPlayer(isFullScreen());
        setDataSource();
        if(isPlay){
            startStateUi();
            prepare();
            isInit = true;
        }else{
            pauseStateUi();
            pause();
        }
    }

    private void controlFullScreen(boolean isClosed){
        if(isFullScreen()){
            if(isClosed && !isLand()){
                destroyFullScreen();
                return;
            }
            setLand(!isLand());
            createFullScreen();
            if(isLand()){
                ivClose.setVisibility(GONE);
                ivBack.setVisibility(VISIBLE);
                ivFullscreen.setImageResource(R.mipmap.fullscreen_exit);
            }else{
                ivClose.setVisibility(VISIBLE);
                ivBack.setVisibility(GONE);
                ivFullscreen.setImageResource(R.mipmap.fullscreen_start);
            }
        }else{
            setHasFullScreen(true);
            createFullScreen();
        }
    }

    private void displayUi(){

    }

    public void start(String key){
        if(TextUtils.isEmpty(key)){
            return;
        }
        String[] url = key.split("\\,");
        if(url.length>1){
            key = url[0];
            thumb.setImageURI(url[1]);
        }
        setUrl(key);
        if(isFullScreen()) {
            ivClose.setVisibility(VISIBLE);
            playState(isPlaying());
        }
    }

    private void createPlayer(boolean isFull){
        if(playerFrame.getChildCount() == 0) {
            playerFrame.addView(createPlayerView(isFull));
            if(!isFullScreen()){
                resize();
            }
        }
    }

    void createFullScreen(){
        Activity ac = (Activity)getContext();
        ViewGroup vg = ac.findViewById(Window.ID_ANDROID_CONTENT);
        View view = vg.findViewById(R.id.video_fullscreen_id);
        if(view == null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            PlayerLayout frame = new PlayerLayout(ac);
            frame.setLayoutParams(params);
            frame.setId(R.id.video_fullscreen_id);
            frame.createPlayer(true);
            frame.start(getUrl());
            view = frame;
            vg.addView(view);
            PlayerUtils.hideActionBar(ac);
        }
        view.setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN );
        ac.setRequestedOrientation(isLand() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    void destroyFullScreen(){
        Activity ac = (Activity)getContext();
        ViewGroup vg = ac.findViewById(Window.ID_ANDROID_CONTENT);
        View video = vg.findViewById(R.id.video_fullscreen_id);
        if(video != null) {
            vg.removeView(video);
            if(video instanceof PlayerLayout){
                ((PlayerLayout)video).onChanged();
            }
            PlayerUtils.showActionBar(ac);
            video.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE );
            ac.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static boolean onBackPressed(Context mc){
        PlayerLayout playerLayout = getFullScreen(mc);
        if(playerLayout!=null && playerLayout.isFullScreen()){
            playerLayout.controlFullScreen(true);
            return true;
        }
        return false;
    }

    static PlayerLayout getFullScreen(Context mc){
        if(mc!=null && mc instanceof Activity) {
            Activity ac = (Activity) mc;
            ViewGroup vg = ac.findViewById(Window.ID_ANDROID_CONTENT);
            View video = vg.findViewById(R.id.video_fullscreen_id);
            if(video != null){
                return (PlayerLayout)video;
            }
        }
        return null;
    }
}
