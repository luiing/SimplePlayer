package com.uis.lib.simpleplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
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

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
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
    private final static String TAG = "PlayerLayout";
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
    private PlayerStateCallback stateCallback;
    private int percentTime;
    private static final int MAX_PERCENT = 980;
    private NetChangeReceiver mReceiver;

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

    interface OnNetChanged{
        void onChanged();
    }

    static class NetChangeReceiver extends BroadcastReceiver {
        OnNetChanged mOnNetChanged;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

            }else{
                return;
            }
            if(mOnNetChanged != null){
                mOnNetChanged.onChanged();
            }
        }

        public void setOnNetChanged(OnNetChanged changed){
            mOnNetChanged = changed;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerNetChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterNetChanged();
    }

    private void registerNetChanged(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        if(mReceiver == null){
            mReceiver = new NetChangeReceiver();
            mReceiver.setOnNetChanged(new OnNetChanged() {
                @Override
                public void onChanged() {
                    if(isRelease() && !isLoading()){
                        checkWifi();
                    }
                }
            });
        }
        if(mReceiver != null && getContext()!=null) {
            getContext().registerReceiver(mReceiver, filter);
        }
    }

    private void unregisterNetChanged(){
        if(mReceiver != null && getContext()!=null) {
            getContext().unregisterReceiver(mReceiver);
        }
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
        //ivClose.setOnClickListener(this);
        boolean isEdit = isInEditMode();
        llRate.setVisibility(isEdit ? VISIBLE:GONE);
        pbarRate.setVisibility(isEdit ? VISIBLE:GONE);
        ivPlay.setVisibility(isEdit ? VISIBLE:GONE);
        llPlay.setVisibility(isEdit ? VISIBLE:GONE);
        setPlaceHolderBackground(Color.WHITE);
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
                    int timeTxt = (int)PlayerUtils.getRate(totalTime,progress,MaxRate);
                    tvPlayTime.setText(PlayerUtils.getTime(timeTxt));
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
                switch (state){
                    case PlayerComplete.STATE_START:

                        break;
                    case PlayerComplete.STATE_PAUSE:
                        break;
                    case PlayerComplete.STATE_PREPARE:
                        displayUi();
                        break;
                    case PlayerComplete.STATE_PREPARING:
                        initState(true);
                        showLoading();
                        break;
                    case PlayerComplete.STATE_RELEASE:
                        isInit = false;
                    case PlayerComplete.STATE_RESET:

                        percentTime = 0;
                        totalTime = 0;
                        initState(false);
                        break;
                }
                if(stateCallback != null){
                    stateCallback.onState(state);
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
                percentTime = 10*percent;
                int pb = pbarRate.getSecondaryProgress();
                if(percentTime > pb) {
                    pbarRate.setSecondaryProgress(percentTime);
                    sbarRate.setSecondaryProgress(percentTime);
                }
            }

            @Override
            public void onCompletion(PlayerListener vp) {
                if(isFullScreen()) {
                    controlFullScreen();
                }
                initState(false);
                if(stateCallback != null){
                    stateCallback.onCompletion();
                }
            }

            @Override
            public void onError(PlayerListener vp, int what, int extra) {
                PlayerUtils.toast(getContext(), "视频播放失败，请检查网络");
            }

            @Override
            public void onSeekComplete(PlayerListener vp) {

            }

            @Override
            public void onProgress(int current, int total) {
                if(current>0 && thumb.getVisibility() == VISIBLE){
                    thumbVisibility(false);
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
                int rate = total==0 ? 0 : (int)PlayerUtils.getRate(MaxRate,current,total);
                pbarRate.setProgress(rate);
                if(!isSeeking) {
                    sbarRate.setProgress(rate);
                    tvPlayTime.setText(PlayerUtils.getTime(current));
                }
                tvTotalTime.setText(PlayerUtils.getTime(total));
                if(isFullScreen() && percentTime == MAX_PERCENT){
                    pbarRate.setSecondaryProgress(percentTime);
                    sbarRate.setSecondaryProgress(percentTime);
                }
            }

            @Override
            public void onVideoSizeChanged(PlayerListener vp, int width, int height) {
                isVideoLand = width > height;
                resize();
            }
        };
    }

    private void setPlayGone(){
        ivPlay.setVisibility(GONE);
        llPlay.setVisibility(GONE);
    }

    private void checkWifi(){
        if(!PlayerUtils.isWifiConnected(getContext())){
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

    public void setPlayerStateCallback(PlayerStateCallback callback){
        this.stateCallback = callback;
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
            if(!PlayerUtils.isConnected(getContext()) && isRelease() && !isVideoFile()) {
                PlayerUtils.toast(getContext(), "当前无网络连接");
            }else{
                playState();
            }
        }else if(id == R.id.player_id_iv_pause){
            playState();
        }
    }

    private boolean isVideoFile(){
        String path = getVideoUrl();
        return !TextUtils.isEmpty(path)&&path.startsWith("/");
    }

    private void initState(boolean isStart){
        if(isStart){
            ivPlay.setVisibility(GONE);
            ivPause.setVisibility(GONE);
            llPlay.setVisibility(GONE);
        }else{
            pbarLoading.setVisibility(GONE);
            pbarRate.setVisibility(GONE);
            llRate.setVisibility(GONE);
            thumbVisibility(true);
            boolean isCurrent = false;
            if(PlayerUtils.isPlaying()){
                if(PlayerUtils.isPlaying(getVideoUrl())) {//play current videoUrl
                    isCurrent = true;
                }else{//play another videoUrl
                }
            }
            if(isCurrent){
                setPlayGone();
                ivPause.setVisibility(VISIBLE);
            }else {
                checkWifi();
                ivPause.setVisibility(GONE);
            }
        }
    }

    private void playState(){
        playerStateInit();
        if(!isPlaying()){
            prepare();
            isInit = true;
            if(stateCallback != null){
                stateCallback.onPlay(isRelease());
            }
        }else{
            pause();
            if(stateCallback != null){
                stateCallback.onPause();
            }
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

    private boolean isLoading(){
        return pbarLoading!=null && VISIBLE == pbarLoading.getVisibility();
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

    public void setPlaceHolderBackground(int color){
        if(thumb != null){
            thumb.setBackgroundColor(color);
        }
    }

    public void setPlaceHolderImage(int resourceId){
        this.setPlaceHolderImage(resourceId,ScalingUtils.ScaleType.CENTER_INSIDE);
    }

    public void setPlaceHolderImage(int resourceId,ScalingUtils.ScaleType scaleType){
        if(thumb!=null){
            GenericDraweeHierarchy gdh = thumb.getHierarchy();
            gdh.setPlaceholderImage(resourceId, scaleType);
        }
    }

    public void start(String videoPath,String thumbPath){
        if(!TextUtils.isEmpty(thumbPath)) {
            setThumbUrl(thumbPath);
            thumb.setImageURI(thumbPath);
            thumbVisibility(true);
        }
        if(TextUtils.isEmpty(videoPath)){
            setPlayGone();
            return;
        }
        setVideoUrl(videoPath);
        if(isFullScreen()) {
            playerStateInit();
            displayUi();
        }else{
            initState(false);
        }
    }

    private void thumbVisibility(boolean isVisibility){
        if(!isFullScreen() && isVisibility){
            thumb.setVisibility(VISIBLE);
        }else{
            thumb.setVisibility(GONE);
        }
    }

    private void removePlayer(){
        if(!isFullScreen()){
            createPlayerView(false);
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
            frame.totalTime = totalTime;
            frame.percentTime = percentTime;
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
            PlayerUtils.showActionBar(ac);
            video.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE );
            ac.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if(video instanceof PlayerLayout){
                ((PlayerLayout)video).onChanged();
            }
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
