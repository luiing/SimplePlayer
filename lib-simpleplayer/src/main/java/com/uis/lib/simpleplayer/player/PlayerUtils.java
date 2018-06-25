package com.uis.lib.simpleplayer.player;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.uis.lib.simpleplayer.PlayerLayout;

/**
 * @author uis on 2017/11/21.
 */

public class PlayerUtils {

    public static void pause(){
        PlayerControl.createPlayer().pause();
    }

    public static boolean isPlaying(){
        return PlayerControl.createPlayer().isPlaying();
    }

    public static boolean isPlaying(String videoPath){
        return PlayerControl.createPlayer().isPlaying(videoPath);
    }

    public static boolean isRelease(){
        return PlayerControl.createPlayer().isRelease();
    }

    public static void start(){
        PlayerControl.createPlayer().start();
    }

    public static void release(){
        PlayerControl.createPlayer().releasePlayer();
    }

    public static void showActionBar(Context mc){
        if(mc!=null) {
            ActionBar bar = ((Activity) mc).getActionBar();
            if(bar!=null) {
                bar.show();
            }else if(mc instanceof AppCompatActivity){
                android.support.v7.app.ActionBar abar = ((AppCompatActivity) mc).getSupportActionBar();
                if(abar!=null) {
                    abar.show();
                }
            }
        }
    }

    public static void hideActionBar(Context mc){
        if(mc!=null) {
            ActionBar bar = ((Activity) mc).getActionBar();
            if(bar!=null) {
                bar.hide();
            }else if(mc instanceof AppCompatActivity){
                android.support.v7.app.ActionBar abar = ((AppCompatActivity) mc).getSupportActionBar();
                if(abar!=null) {
                    abar.hide();
                }
            }
        }
    }

    public static void toast(Context context,String msg){
        if(context!=null && !TextUtils.isEmpty(msg)) {
            Toast.makeText(context, msg,Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static String getTime(int current){
        StringBuilder builder = new StringBuilder();
        current /= 1000;
        if(current>60){//60s
            if(current<600){//600s
                builder.append("0");
            }
            builder.append(current/60);
        }else{
            builder.append("00");
        }
        builder.append(":");
        int seconds = current%60;
        if(seconds<10){//10s
            builder.append("0");
        }
        builder.append(seconds);
        return builder.toString();
    }

    public static String getUniqueCode(int unique,String url){
        return String.valueOf(new StringBuilder(url).append(unique).toString().hashCode());
    }

    public static double getRate(int maxRate,int current,int total){
        return maxRate*(1.0d*current/total);
    }

    public static PlayerView initPlayer(Context mc){
        if(InitPlayer.sPlayer == null) {
            InitPlayer.sPlayer = new PlayerView(mc);
            InitPlayer.sPlayer.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    ));
        }
        return InitPlayer.sPlayer;
    }

    public static int getPlayerWidth(){
        return InitPlayer.sWidth;
    }

    public static int getPlayerHeight(){
        return InitPlayer.sHeight;
    }

    static class InitPlayer{
        static PlayerView sPlayer;
        static int sWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        static int sHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
