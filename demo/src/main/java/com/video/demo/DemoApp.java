package com.video.demo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

//import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by lhb on 2017/7/25.
 */

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
