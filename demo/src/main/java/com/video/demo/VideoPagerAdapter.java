package com.video.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uis.lib.simpleplayer.OnScreenListener;
import com.uis.lib.simpleplayer.PlayerLayout;
import com.uis.lib.simpleplayer.Vlog;
import com.uis.lib.simpleplayer.player.PlayerUtils;

import java.util.LinkedList;

/**
 * @author uis on 2017/11/22.
 */

public class VideoPagerAdapter extends PagerAdapter {

    private LinkedList<View> mList = new LinkedList<>();
    private View mVideo;
    private boolean isOutSide = false;

    public VideoPagerAdapter() {
    }

    public boolean isPlaying(){
        return PlayerUtils.isPlaying(DemoApp.mUrl[3]);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View v;
        if(position>0) {
            if (mList.size() > 0) {
                v = mList.pop();
            }else {
                v = LayoutInflater.from(container.getContext()).inflate(R.layout.vh_image, container, false);
            }
            SimpleDraweeView sdView = VideoAdapter.id(v,R.id.thumb);
            sdView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeScreen(false);
                }
            });
            sdView.setImageURI(DemoApp.URL);
        }else{
            if(mVideo == null) {
                mVideo = LayoutInflater.from(container.getContext()).inflate(R.layout.vh_video, container, false);
                final PlayerLayout player = VideoAdapter.id(mVideo, R.id.player);
                String url = DemoApp.mUrl[3];
                player.start(position >= 0 ? url : "", DemoApp.URL);
                player.setOnScreenListener(new OnScreenListener() {
                    @Override
                    public void onScreen(boolean isFullScreen) {//点击了放大
                        changeScreen(true);
                    }
                });
            }
            v = mVideo;
        }
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View v = (View)object;
        container.removeView(v);
        if(position!=0) {
            mList.push(v);
        }
    }

    @Override
    public int getCount() {
        return DemoApp.mUrl.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    void changeScreen(boolean canPlay){
        //Vlog.a("xx","canPlay="+canPlay);
        if(mVideo == null){
            return;
        }
        PlayerLayout player = VideoAdapter.id(mVideo, R.id.player);
        if(player == null){
            return;
        }
        Activity ac = (Activity)mVideo.getContext();
        ViewGroup out = ac.findViewById(R.id.video_outside_id);
        boolean isOutSide = out.getChildCount() == 0;
        if(canPlay && isOutSide){
            player.controlFullScreen();
        }else{
            createFullScreen(mVideo.getContext());
        }
    }

    void createFullScreen(Context mc){
        if(!(mc instanceof Activity)){
            return;
        }
        Activity ac = (Activity)mc;
        ViewGroup vg = ac.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup out = ac.findViewById(R.id.video_outside_id);
        View container = vg.findViewById(R.id.video_container_id);
        if(container != null && out != null && out.getChildCount()>0) {
            isOutSide = true;
            PlayerUtils.hideActionBar(ac);
            out.removeView(container);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            vg.addView(container,params);
            setUiFullScreen(container);
            ac.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    void setUiFullScreen(View v){
        if(v != null) {
            v.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    public static boolean destroyFullScreen(Context mc){
        if(mc!=null && mc instanceof Activity) {
            Activity ac = (Activity) mc;
            ViewGroup vg = ac.findViewById(Window.ID_ANDROID_CONTENT);
            ViewGroup out = ac.findViewById(R.id.video_outside_id);
            View container = vg.findViewById(R.id.video_container_id);
            if (container != null && out != null && out.getChildCount()==0) {
                PlayerUtils.showActionBar(ac);
                vg.removeView(container);
                out.addView(container);
                container.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                ac.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return false;
    }

    public static boolean onBackPressed(Context mc){
        return PlayerLayout.onBackPressed(mc) || destroyFullScreen(mc);
    }
}
