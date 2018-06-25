package com.uis.lib.simpleplayer.player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.uis.lib.simpleplayer.Vlog;

/**
 * 播放显示器
 * @author uis on 2017/11/21.
 */

final class PlayerView extends TextureView implements TextureView.SurfaceTextureListener{

    private final static String TAG = "PlayerView";
    protected PlayerListener mPlayer;
    private SurfaceTexture savedSurface;
    private boolean isFullScreen = false;
    private String url;
    private String key;
    private static int sWidth = PlayerUtils.getPlayerWidth();
    private static int sHeight = PlayerUtils.getPlayerHeight();
    private static long sResizeTime;

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

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if(isFullScreen) {
            resize(newConfig.orientation);
        }
    }

    private void init(){
        if(!isInEditMode()) {
            mPlayer = PlayerControl.createPlayer();
            setSurfaceTextureListener(this);
        }
    }

    public void setFullScreen(boolean isFull){
        isFullScreen = isFull;
    }

    public void setDataSource(int unique,String url, PlayerCallback callback, PlayerComplete complete){
        this.url = url;
        this.key = PlayerUtils.getUniqueCode(unique,url);
        mPlayer.registerPlayer(key,callback,complete);
    }

    public void prepare(){
        mPlayer.prepare(key,url);
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

    public boolean isRelease(){
        return mPlayer.isRelease();
    }

    public void pause(){
        mPlayer.pause();
    }

    void resize(){
        resize(1);
    }

    void resize(int config){
        if(System.currentTimeMillis() - sResizeTime < 100){
            return;
        }
        int w = mPlayer.getVideoWidth();
        int h = mPlayer.getVideoHeight();
        if(w<=0 || h<=0){
            return;
        }
        int screenW,screenH;
        ViewGroup root = (ViewGroup)getParent();
        if(root==null){
            return;
        }
        if(isFullScreen){
            if(Configuration.ORIENTATION_LANDSCAPE == config){
                screenW = sHeight;
                screenH = sWidth;
            }else{
                screenW = sWidth;
                screenH = sHeight;
            }
        }else{
            screenW = root.getWidth();
            screenH = root.getHeight();
        }
        if(getRate(w,h) >= getRate(screenW,screenH)){//fixed width
            h = screenW * h / w;
            w = screenW;
        }else{//fixed height
            w = screenH * w / h;
            h = screenH;
        }
        Vlog.e(TAG,"screenW="+screenW+",screenH="+screenH+",w="+w+",h="+h);
        ViewGroup.LayoutParams mParams = getLayoutParams();
        if(mParams.width!=w || mParams.height!=h) {
            mParams.width = w;
            mParams.height = h;
            if (mParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) mParams).gravity = Gravity.CENTER;
            }
            setLayoutParams(mParams);
            sResizeTime = System.currentTimeMillis();
        }
    }

    void onChanged(){
        if(isFullScreen && mPlayer!=null) {
            mPlayer.onChanged(key);
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
