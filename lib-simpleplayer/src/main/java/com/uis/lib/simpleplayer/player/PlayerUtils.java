package com.uis.lib.simpleplayer.player;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

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
        PlayerControl.createPlayer().releaseAll();
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
}
