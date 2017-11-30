package com.video.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

public class VideoPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{

    private LinkedList<View> mList = new LinkedList<>();
    private PlayerLayout mVideo;
    private final int WIDTH;
    private int currentPos;
    private static boolean isPlaying;
    private static boolean canVisibility;
    private View ivClose;

    public VideoPagerAdapter() {
        canVisibility = false;
        currentPos = 0;
        isPlaying = false;
        WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static void setPlaying(){
        isPlaying = PlayerUtils.isPlaying();
    }

    public static boolean isPlaying(){
        return isPlaying;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View v;
        if(position>0) {
            if (mList.size() > 0) {
                v = mList.pop();
            }else {
                v = new SimpleDraweeView(container.getContext());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, WIDTH);
                v.setLayoutParams(params);
                //v = LayoutInflater.from(container.getContext()).inflate(R.layout.vh_image, container, false);
            }
            SimpleDraweeView sdView = (SimpleDraweeView)v;
            sdView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeScreen(false);
                }
            });
            sdView.setImageURI(DemoApp.URL);
        }else{
            if(mVideo == null) {
                mVideo = new PlayerLayout(container.getContext());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, WIDTH);
                mVideo.setLayoutParams(params);
                //LayoutInflater.from(container.getContext()).inflate(R.layout.vh_video, container, false);
                //final PlayerLayout player = VideoAdapter.id(mVideo, R.id.player);
                String url = DemoApp.mUrl[3];
                mVideo.start(position >= 0 ? url : "", DemoApp.URL);
                mVideo.setOnScreenListener(new OnScreenListener() {
                    @Override
                    public void onScreen(boolean isFullScreen) {//点击了放大
                        changeScreen(true);
                    }
                });
                Activity ac = (Activity)container.getContext();
                ViewGroup vg = (ViewGroup)ac.findViewById(Window.ID_ANDROID_CONTENT);
                final View cv = vg.findViewById(R.id.video_container_id);
                ivClose = cv.findViewById(R.id.video_close_id);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        destroyFullScreen(mVideo.getContext());
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(currentPos == 0){
            setPlaying();
        }
        if(position == 0 && isPlaying()){
            PlayerUtils.start();
        }else {
            PlayerUtils.pause();
        }
        currentPos = position;
        if(ivClose!=null) {
            ivClose.setVisibility((VideoPagerAdapter.canVisibility && position == 0) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    void changeScreen(boolean canPlay){
        if(mVideo == null){
            return;
        }
        Activity ac = (Activity)mVideo.getContext();
        ViewGroup out = (ViewGroup)ac.findViewById(R.id.video_outside_id);
        boolean isOutSide = out.getChildCount() == 0;
        if(canPlay && isOutSide){
            mVideo.controlFullScreen();
        }else{
            createFullScreen(mVideo.getContext());
        }
    }

    void createFullScreen(Context mc){
        if(!(mc instanceof Activity)){
            return;
        }
        Activity ac = (Activity)mc;
        ViewGroup vg = (ViewGroup)ac.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup out = (ViewGroup)ac.findViewById(R.id.video_outside_id);
        final View container = vg.findViewById(R.id.video_container_id);
        if(container != null && out != null && out.getChildCount()>0) {
            PlayerUtils.hideActionBar(ac);
            out.removeView(container);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            vg.addView(container,params);
            canVisibility = true;
            final View close = container.findViewById(R.id.video_close_id);
            if(close!=null && currentPos==0) {
                close.setVisibility(View.VISIBLE);
            }
            container.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if(canVisibility){
                        setUiFullScreen(container);
                    }
                }
            });
            setUiFullScreen(container);
            ac.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    static void setUiFullScreen(View v){
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
            ViewGroup vg = (ViewGroup)ac.findViewById(Window.ID_ANDROID_CONTENT);
            ViewGroup out = (ViewGroup)ac.findViewById(R.id.video_outside_id);
            View container = vg.findViewById(R.id.video_container_id);
            if (container != null && out != null && out.getChildCount()==0) {
                PlayerUtils.showActionBar(ac);
                vg.removeView(container);
                out.addView(container);
                canVisibility = false;
                View close = container.findViewById(R.id.video_close_id);
                if(close!=null) {
                    close.setVisibility(View.GONE);
                }
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
