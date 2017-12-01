package com.video.demo;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uis.lib.simpleplayer.OnScreenListener;
import com.uis.lib.simpleplayer.PlayerLayout;
import com.uis.lib.simpleplayer.Vlog;
import com.uis.lib.simpleplayer.player.PlayerUtils;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * @author uis on 2017/11/22.
 */

public class VideoPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener,Observer{

    private LinkedList<View> mList = new LinkedList<>();
    private PlayerLayout mVideo;
    private final int WIDTH;
    private int currentPos;
    private  boolean canVisibility;
    private View ivClose;

    public VideoPagerAdapter() {
        canVisibility = false;
        currentPos = 0;
        WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
        ScreenConvertor.setPlaying();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View v;
        if(position>=0) {
            if (mList.size() > 0) {
                v = mList.pop();
            }else {
                v = new SimpleDraweeView(container.getContext());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, WIDTH);
                v.setLayoutParams(params);
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
                        ScreenConvertor.destroyFullScreen(mVideo.getContext());
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
            ScreenConvertor.setPlaying();
        }
        if(position == 0 && ScreenConvertor.isPlaying()){
            PlayerUtils.start();
        }else {
            PlayerUtils.pause();
        }
        currentPos = position;
        if(ivClose!=null) {
            ivClose.setVisibility((canVisibility && position == 0) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void update(Observable observable, Object data) {
        if(data == null){
            ivClose.setVisibility(View.GONE);
        }else if(data != null && currentPos == 0){
            ivClose.setVisibility(View.VISIBLE);
        }
        canVisibility = (data!=null);
    }

    void changeScreen(boolean canPlay){
        ScreenConvertor.setObserver(this);
        ScreenConvertor.setFullScreen(canPlay,mVideo);
    }
}
