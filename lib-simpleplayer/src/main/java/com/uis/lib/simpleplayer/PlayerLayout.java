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
import android.widget.Toast;

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
    private ImageView ivPlaySmall;
    private ImageView ivPause;
    private ImageView ivBack;
    private ImageView ivClose;
    private ProgressBar pbarLoading;
    private ProgressBar pbarRate;
    private LinearLayout llRate;
    private LinearLayout llPlay;
    private TextView tvPlayTime;
    private TextView tvTotalTime;
    private ImageView ivFullscreen;
    private SeekBar sbarRate;
    private boolean isInit = false;
    private boolean isSeeking = false;
    private boolean isVideoLand;
    private OnScreenListener onScreenListener;

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
        int layoutId = mATTR.getResourceId(R.styleable.PlayerLayout_playLayout,R.layout.video_player_layout);
        mATTR.recycle();
        setBackgroundColor(Color.BLACK);
        setClickable(true);
        inflate(getContext(),layoutId,this);
        frameLayout = id(R.id.player_id_frame);
        playerFrame = id(R.id.player_id_player);
        llPlay = id(R.id.player_id_ll_play);
        ivPlaySmall = id(R.id.player_id_iv_play_s);
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
        ivPlaySmall.setOnClickListener(this);
        ivPause.setOnClickListener(this);
        ivFullscreen.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        boolean isEdit = isInEditMode();
        llRate.setVisibility(isEdit ? VISIBLE:GONE);
        pbarRate.setVisibility(isEdit ? VISIBLE:GONE);
        ivPlay.setVisibility(isEdit ? VISIBLE:GONE);
        llPlay.setVisibility(isEdit ? VISIBLE:GONE);
        setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(isFullScreen() && visibility != SYSTEM_UI_FLAG_FULLSCREEN){
                    setUiFullScreen();
                }
            }
        });
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
                if(isInit || isFullScreen()) {
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
                    playerStateInit();
                    displayUi();
                }
            }

            @Override
            public void onComplete(int state) {
                if(PlayerComplete.STATE_START == state || PlayerComplete.STATE_PAUSE == state){

                }else if(PlayerComplete.STATE_PREPARE == state){
                    displayUi();
                }else {
                    if(PlayerComplete.STATE_RELEASE == state){
                        isInit = false;
                    }
                    initState(false);
                }
            }
        };

        mCallback = new PlayerCallback() {
            @Override
            public void onPrepared(PlayerListener vp) {
                totalTime = vp.getDuration();
                pbarRate.setVisibility(VISIBLE);
                initState(true);
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
                Vlog.e("xx","isFull="+isFullScreen());
                if(isFullScreen()) {
                    controlFullScreen();
                }
                initState(false);
            }

            @Override
            public void onError(PlayerListener vp, int what, int extra) {
                if(getContext()!=null) {
                    Toast.makeText(getContext(), "视频播放失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSeekComplete(PlayerListener vp) {

            }

            @Override
            public void onProgress(int current, int total) {
                if(current>0 && thumb.getVisibility() == VISIBLE){
                    thumb.setVisibility(GONE);
                }
                if(totalTime == 0){
                    totalTime = total;
                }
                if(current != currentTime){
                    hideLoading();
                }else{
                    showLoading();
                }
                currentTime = current;
                int rate = total==0 ? 0 : (int)getRate(MaxRate,current,total);
                pbarRate.setProgress(rate);
                if(!isSeeking) {
                    sbarRate.setProgress(rate);
                    tvPlayTime.setText(getTime(current));
                }
                tvTotalTime.setText(getTime(total));
            }

            @Override
            public void onVideoSizeChanged(PlayerListener vp, int width, int height) {
                isVideoLand = width > height;
                resize();
            }
        };
    }

    private void checkWifi(){
        if(PlayerUtils.isMobileConnected(getContext())){
            llPlay.setVisibility(VISIBLE);
            ivPlay.setVisibility(GONE);
        }else{
            ivPlay.setVisibility(VISIBLE);
            llPlay.setVisibility(GONE);
        }
    }

    public void setOnScreenListener(OnScreenListener onScreenListener) {
        this.onScreenListener = onScreenListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.player_id_frame){
            displayUi();
        }else if(id == R.id.player_id_iv_close || id == R.id.player_id_iv_back){
            controlFullScreen();
        }else if(id == R.id.player_id_iv_fullscreen){
            if(onScreenListener!=null){
                onScreenListener.onScreen(isFullScreen());
            }else {
                controlFullScreen();
            }
        }else if(id == R.id.player_id_iv_play || id == R.id.player_id_iv_play_s){
            if(isRelease()){
                initState(true);
                showLoading();
            }
            playState();
        }else if(id == R.id.player_id_iv_pause){
            playState();
        }
    }

    private void initState(boolean isStart){
        if(isStart){
            //thumb.setVisibility(GONE);
            ivPlay.setVisibility(GONE);
            ivPause.setVisibility(GONE);
        }else{
            pbarLoading.setVisibility(GONE);
            pbarRate.setVisibility(GONE);
            llRate.setVisibility(GONE);
            thumb.setVisibility(VISIBLE);
            checkWifi();
            ivPause.setVisibility(GONE);
        }
    }

    private void playState(){
        playerStateInit();
        if(!isPlaying()){
            prepare();
            isInit = true;
        }else{
            pause();
        }
        playerStateUi();
    }

    private void playerStateInit(){
        createPlayer(isFullScreen());
        setDataSource();
    }

    private void playerStateUi(){
        if(llRate.getVisibility() == VISIBLE) {
            boolean isPlaying = isPlaying();
            if (isPlaying) {
                ivPlay.setVisibility(GONE);
                ivPause.setVisibility(VISIBLE);
            } else {
                ivPlay.setVisibility(VISIBLE);
                ivPause.setVisibility(GONE);
            }
        }
    }

    public void controlFullScreen(){
        if(isFullScreen()){
            destroyFullScreen();
        }else{
            setHasFullScreen(true);
            createFullScreen(isVideoLand);
        }
    }

    private void showLoading(){
        if(GONE == pbarLoading.getVisibility()) {
            pbarLoading.setVisibility(VISIBLE);
        }
    }

    private void hideLoading() {
        if(VISIBLE == pbarLoading.getVisibility()) {
            pbarLoading.setVisibility(GONE);
        }
    }

    private void displayUi(){
        stopTimer();
        onCounter(true);
    }

    @Override
    protected void onCounter(boolean userStop) {
        if(llRate == null || isRelease() || totalTime<=0){
            return;
        }
        int vis = llRate.getVisibility();
        if(vis == VISIBLE){
            ivPlay.setVisibility(GONE);
            ivPause.setVisibility(GONE);
            llRate.setVisibility(GONE);
            pbarRate.setVisibility(VISIBLE);
            if(isFullScreen()){
                ivBack.setVisibility(GONE);
            }
        }else{
            startTimer();
            pbarRate.setVisibility(GONE);
            llRate.setVisibility(VISIBLE);
            playerStateUi();
            if(isFullScreen()){
                ivBack.setVisibility(VISIBLE);
            }
        }
    }

    public void start(String videoPath,String thumbPath){
        if(!TextUtils.isEmpty(thumbPath)) {
            setThumbUrl(thumbPath);
            thumb.setImageURI(thumbPath);
        }
        if(TextUtils.isEmpty(videoPath)){
            ivPlay.setVisibility(GONE);
            llPlay.setVisibility(GONE);
            return;
        }
        setVideoUrl(videoPath);
        if(isFullScreen()) {
            //ivClose.setVisibility(VISIBLE);
            //pbarRate.setVisibility(VISIBLE);
            //ivPlay.setVisibility(GONE);
            playerStateInit();
            displayUi();
        }else{
            initState(false);
        }
    }

    private void createPlayer(boolean isFull){
        if(playerFrame.getChildCount() == 0) {
            playerFrame.addView(createPlayerView(isFull));
            resize();
        }
        if(isFull){
            ivBack.setVisibility(VISIBLE);
            ivFullscreen.setImageResource(R.mipmap.fullscreen_exit);
        }else{
            ivBack.setVisibility(GONE);
            ivFullscreen.setImageResource(R.mipmap.fullscreen_start);
        }
    }

    void setUiFullScreen(){
        setUiFullScreen(this);
    }

    void setUiFullScreen(View v){
        if(v != null) {
            v.setSystemUiVisibility(
                    SYSTEM_UI_FLAG_FULLSCREEN
                    |SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    void createFullScreen(boolean isLand){
        Activity ac = (Activity)getContext();
        ViewGroup vg = (ViewGroup) ac.findViewById(Window.ID_ANDROID_CONTENT);
        View view = vg.findViewById(R.id.video_fullscreen_id);
        if(view == null) {
            PlayerUtils.hideActionBar(ac);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            PlayerLayout frame = new PlayerLayout(ac);
            frame.setId(R.id.video_fullscreen_id);
            vg.addView(frame,params);
            frame.createPlayer(true);
            frame.start(getVideoUrl(),getThumbUrl());
            view = frame;
        }
        setUiFullScreen(view);
        ac.setRequestedOrientation(isLand ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    void destroyFullScreen(){
        Activity ac = (Activity)getContext();
        ViewGroup vg = (ViewGroup)ac.findViewById(Window.ID_ANDROID_CONTENT);
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
            playerLayout.controlFullScreen();
            return true;
        }
        return false;
    }

    static PlayerLayout getFullScreen(Context mc){
        if(mc!=null && mc instanceof Activity) {
            Activity ac = (Activity) mc;
            ViewGroup vg = (ViewGroup)ac.findViewById(Window.ID_ANDROID_CONTENT);
            View video = vg.findViewById(R.id.video_fullscreen_id);
            if(video != null){
                return (PlayerLayout)video;
            }
        }
        return null;
    }
}
