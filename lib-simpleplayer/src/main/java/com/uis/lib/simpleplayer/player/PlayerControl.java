package com.uis.lib.simpleplayer.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.util.ArrayMap;
import android.view.Surface;

import com.uis.lib.simpleplayer.Vlog;


/**
 * MediaPlayer播放器
 * @author uis on 2017/11/25
 */

final class PlayerControl implements PlayerListener {

    private final String TAG = "PlayerControl";
    private final static int sTimerMills = 300;
    private final PlayerListener mVideoPlayer;
    private final PlayerCounter mCounter;

    private MediaPlayer mPlayer;
    private ArrayMap<String,PlayerComplete> mComplete = new ArrayMap<>();
    private PlayerCallback mCallback;
    private PlayerEntity mEntity;
    private Surface mSurface;

    private static PlayerListener newInstance(){
        return new PlayerControl();
    }

    private static class Inner{
        final static PlayerListener sPlayer = PlayerControl.newInstance();
    }

    public static PlayerListener createPlayer(){
        return Inner.sPlayer;
    }

    private PlayerControl(){
        mCounter = PlayerCounter.createCounter();
        mVideoPlayer = this;
        init();
    }

    private void startTimer(){
        mCounter.startTimer(sTimerMills,new InnerCount(this));
    }

    static class InnerCount implements Runnable{
        PlayerControl control;
        public InnerCount(PlayerControl c) {
            this.control = c;
        }

        @Override
        public void run() {
            if(control != null && control.mPlayer != null) {
                control.onProgress(control.getCurrentPosition());
            }
        }
    }

    private void stopTimer(){
        mCounter.stopTimer();
    }

    private void init(){
        Vlog.e(TAG,"-----init-----");
        mPlayer = new MediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        mPlayer.reset();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Vlog.e(TAG,"------onPrepared------");
                if(mCallback != null){
                    mCallback.onPrepared(mVideoPlayer);
                }
                boolean isPause = false;
                if(mEntity!=null) {
                    mEntity.canPlay = true;
                    isPause = mEntity.isPause;
                }
                if(!isPause) {
                    start();
                }else{
                    onComplete(PlayerComplete.STATE_PREPARE);
                }
            }
        });

        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if(mCallback != null){
                    mCallback.onBufferingUpdate(mVideoPlayer,percent);
                }
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Vlog.i(TAG,"-----OnCompletionListener-----");
                onProgress(getDuration());
                if(mCallback != null) {
                    mCallback.onCompletion(mVideoPlayer);
                }
                reset();
            }
        });

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Vlog.i(TAG,"-----Error-----what="+what+",extra="+extra);
                if(mCallback != null) {
                    mCallback.onError(mVideoPlayer, what, extra);
                }
                release();
                return true;
            }
        });

        mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Vlog.i(TAG,"-----Info-----what="+what+",extra="+extra);
                if(mCallback != null) {
                    mCallback.onInfo(mVideoPlayer, what, extra);
                }
                return true;
            }
        });

        mPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if(mCallback != null) {
                    mCallback.onVideoSizeChanged(mVideoPlayer, width, height);
                }
            }
        });

        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Vlog.i(TAG,getDuration()+"-----OnSeekCompleteListener-----"+getCurrentPosition());
                if(mCallback != null) {
                    mCallback.onSeekComplete(mVideoPlayer);
                }
                onProgress(getCurrentPosition());
                if(isPlaying()) {
                    start();
                }
            }
        });
    }

    /**
     * @param key (file-Path or http/rtsp url) to use
     */
    private void createEntityWhenNone(String key,String url){
        if(mEntity==null || !mEntity.key.equals(key)){
            mEntity = new PlayerEntity(key,url);
        }
    }

    private void prepare(){
        Vlog.e(TAG,"-------prepare---------");
        try {
            if(mEntity != null) {
                mPlayer.setDataSource(mEntity.url);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.prepareAsync();
                onComplete(PlayerComplete.STATE_PREPARING);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void reset(){
        try{
            Vlog.e(TAG,"--------reset-----------");
            stopTimer();
            if(mPlayer!=null){
                mPlayer.reset();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            onComplete(PlayerComplete.STATE_RESET);
            mPlayer = null;
            if(mEntity!=null){
                mEntity.canPrepare = true;
                mEntity.canPlay = false;
                mEntity.isPause = false;
            }
        }
    }

    @Override
    public void release(){
        try {
            Vlog.e(TAG,"--------release-----------");
            stopTimer();
            if(mPlayer!=null) {
                mPlayer.release();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            onComplete(PlayerComplete.STATE_RELEASE);
            if(mEntity != null){
                mComplete.remove(mEntity.key);
            }
            mEntity = null;
            mPlayer = null;
        }
    }

    @Override
    public void setSurface(Surface surface){
        try{
            if(surface != null) {
                if (mPlayer != null) {
                    mPlayer.setSurface(surface);
                }
                mSurface = surface;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void setVideoScalingMode(int mode) {
        if(mPlayer != null) {
            mPlayer.setVideoScalingMode(mode);
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if(mPlayer != null) {
            mPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public void setLooping(boolean isLoop) {
        if(mPlayer != null) {
            mPlayer.setLooping(isLoop);
        }
    }

    @Override
    public void prepare(String key,String url) {
        Vlog.e(TAG,"----prepare---mEntity is null = "+(mEntity==null));
        if(mEntity!=null && !mEntity.key.equals(key)){
            release();
        }
        if(mPlayer==null){
            init();
        }
        createEntityWhenNone(key,url);
        if(mEntity.canPrepare){
            prepare();
            setSurface(mSurface);
            mEntity.canPrepare = false;
        }else{
            start();
        }
    }

    @Override
    public boolean isPlaying(){
        return mEntity!=null && isPlaying(mEntity.url);
    }

    @Override
    public boolean isPlaying(String key) {
        boolean isPlaying = false;
        try {
            if (mPlayer!=null && mEntity!=null && mEntity.url.equals(key) && mPlayer.isPlaying()) {
                isPlaying = true;
            }
        }catch (Exception ex){
            //ex.printStackTrace();
        }
        Vlog.e(TAG,"------isPlaying----"+isPlaying+",key="+key);
        return isPlaying;
    }

    @Override
    public boolean isRelease() {
        return (mPlayer!=null && mEntity == null) || mPlayer==null || mCallback==null;
    }

    @Override
    public void start(){
        try{
            Vlog.e(TAG,"--------start-----------"+isPlaying());
            if(mPlayer!=null && !isPlaying() && mEntity!=null && mEntity.canPlay) {
                mPlayer.start();
                startTimer();
                onComplete(PlayerComplete.STATE_START);
                if(mEntity != null){
                    mEntity.isPause = false;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void seekTo(int millsec) {
        try {
            Vlog.e(TAG,"-----------seek----------"+millsec);
            if(mEntity!=null && mEntity.canPlay) {
                mPlayer.seekTo(millsec);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void pause(){
        try {
            Vlog.e(TAG,"--------pause-----------");
            stopTimer();
            if(mPlayer!=null && isPlaying()) {
                mPlayer.pause();
                if(mEntity != null){
                    mEntity.isPause = true;
                }
                onComplete(PlayerComplete.STATE_PAUSE);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void releasePlayer() {
        release();
        Vlog.e(TAG,"--mComplete--size="+mComplete.size()+"\n"+mComplete);
        mCallback = null;
    }

    @Override
    public int getDuration() {
        int duration = 0;
        try {
            if(mPlayer!=null) {
                duration = mPlayer.getDuration();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return duration;
    }

    @Override
    public int getCurrentPosition(){
        int duration = 0;
        try {
            if(mPlayer!=null) {
                duration = mPlayer.getCurrentPosition();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return duration;
    }

    @Override
    public int getVideoWidth() {
        return mPlayer==null ? 0:mPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mPlayer==null ? 0:mPlayer.getVideoHeight();
    }

    @Override
    public void onProgress(int current) {
        if(mEntity==null || mEntity.current != current) {
            if(mEntity!=null){
                mEntity.current = current;
            }
            int mTotal = getDuration();
            if(mCallback != null && current <= mTotal) {
                mCallback.onProgress(current, mTotal);
            }
        }
    }

    @Override
    public void registerPlayer(String key, PlayerCallback callback,PlayerComplete complete){
        if(mCallback != callback){
            mCallback = callback;
        }
        if(complete!=null && !mComplete.containsKey(key)){
            mComplete.put(key,complete);
        }
    }

    @Override
    public void onChanged(String key){
        if(mComplete.containsKey(key)){
            mComplete.get(key).onChanged();
        }
    }

    private void onComplete(int state){
        Vlog.e(TAG,"----------onComplete------"+state);
        if(mEntity != null) {
            String key = mEntity.key;
            if (mComplete.containsKey(key)) {
                mComplete.get(key).onComplete(state);
                if(PlayerComplete.STATE_RELEASE == state) {
                    mComplete.remove(key);
                }
            }
        }
    }
}
