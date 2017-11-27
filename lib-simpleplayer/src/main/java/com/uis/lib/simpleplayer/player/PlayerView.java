package com.uis.lib.simpleplayer.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 播放显示器
 * @author uis on 2017/11/21.
 */

final class PlayerView extends TextureView implements TextureView.SurfaceTextureListener{

    protected PlayerListener mPlayer;
    private SurfaceTexture savedSurface;
    private boolean isFullScreen = false;
    private String url;

    public PlayerView(Context context) {
        this(context,null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mPlayer = PlayerControl.createPlayer();
        setSurfaceTextureListener(this);
    }

    public void setFullScreen(boolean isFull){
        isFullScreen = isFull;
        resize();
    }

    public void setDataSource(String key, PlayerCallback callback, PlayerComplete complete){
        url = key;
        mPlayer.registerPlayer(key,callback,complete);
    }

    public void prepare(){
        mPlayer.prepare(url);
    }

    public void seekTo(int time){
        if(mPlayer.isRelease()) {
            prepare();
        }else{
            mPlayer.seekTo(time);
        }
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying(url);
    }

    public void pause(){
        mPlayer.pause();
    }

    void resize(){
        int w = mPlayer.getVideoWidth();
        int h = mPlayer.getVideoHeight();
        if(w<=0 || h<=0){
            return;
        }
        int screenW;
        int screenH;
        if(isFullScreen){
            screenW = getResources().getDisplayMetrics().widthPixels;
            screenH = getResources().getDisplayMetrics().heightPixels;
        }else{
            ViewGroup root = (ViewGroup)getParent();
            if(root==null){
                return;
            }
            screenW = root.getMeasuredWidthAndState();
            screenH = root.getMeasuredHeightAndState();
        }
        if(getRate(w,h) >= getRate(screenW,screenH)){//fixed width
            h = screenW * h / w;
            w = screenW;
        }else{//fixed height
            w = screenH * w / h;
            h = screenH;
        }
        ViewGroup.LayoutParams mParams = getLayoutParams();
        if(mParams.width!=w || mParams.height!=h) {
            mParams.width = w;
            mParams.height = h;
            if (mParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) mParams).gravity = Gravity.CENTER;
            }
            setLayoutParams(mParams);
        }
    }

    void onChanged(){
        if(isFullScreen && mPlayer!=null) {
            mPlayer.onChanged(url);
        }
    }

    private float getRate(int a,int b){
        return 1.0f*a/b;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if(savedSurface == null) {
            savedSurface = surface;
            mPlayer.setSurface(new Surface(savedSurface));
        }else{
            setSurfaceTexture(savedSurface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
