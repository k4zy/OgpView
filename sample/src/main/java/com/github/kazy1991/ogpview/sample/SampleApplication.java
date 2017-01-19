package com.github.kazy1991.ogpview.sample;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
