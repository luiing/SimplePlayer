package com.video.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import com.uis.lib.simpleplayer.PlayerLayout;
import com.uis.lib.simpleplayer.player.PlayerUtils;
import java.util.Observer;

/**
 * 屏幕视图变换器
 * @author uis on 2017/12/1.
 */

public class ScreenConvertor {

    private static boolean isPlaying;
    private static Observer sObserver;

    static void deleteObserver(){
        sObserver = null;
    }

    static void notifyobserver(Object data){
        if(sObserver != null){
            sObserver.update(null,data);
        }
    }

    public static void setObserver(Observer observer){
        sObserver = observer;
    }

    public static void setPlaying(){
        isPlaying = PlayerUtils.isPlaying();
    }

    public static boolean isPlaying(){
        return isPlaying;
    }

    /**
     * 设置全屏播放
     * @param canPlay true：全屏，false：独立页面
     * @param layout
     */
    public static void setFullScreen(boolean canPlay,PlayerLayout layout){
        if(layout == null || !(layout.getContext() instanceof Activity)){
            return;
        }
        Activity ac = (Activity)layout.getContext();
        ViewGroup out = (ViewGroup)ac.findViewById(R.id.video_outside_id);
        boolean isOutSide = out.getChildCount() == 0;
        if(canPlay && isOutSide){//全屏播放
            layout.controlFullScreen();
        }else{//独立页面播放
            createFullScreen(ac);
        }
    }

    public static boolean onBackPressed(Context mc){
        return PlayerLayout.onBackPressed(mc) || destroyFullScreen(mc);
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
                notifyobserver(null);
                deleteObserver();
                container.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                ac.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return false;
    }

    static void createFullScreen(Activity ac){
        ViewGroup vg = (ViewGroup)ac.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup out = (ViewGroup)ac.findViewById(R.id.video_outside_id);
        final View container = vg.findViewById(R.id.video_container_id);
        if(container != null && out != null && out.getChildCount()>0) {
            PlayerUtils.hideActionBar(ac);
            out.removeView(container);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            vg.addView(container,params);
            notifyobserver("true");
            container.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if(sObserver!=null){
                        setViewFullScreen(container);
                    }
                }
            });
            setViewFullScreen(container);
            ac.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    static void setViewFullScreen(View v){
        if(v != null) {
            v.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }
}
