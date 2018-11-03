package com.codevice.androvice.app;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import com.codevice.androvice.service.ServiceTime;

public class Androvice extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //we can start the service from application instead of clicking 'START' button
        //startService();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ServiceTime.class));
        } else {
            startService(new Intent(this, ServiceTime.class));
        }
    }
}
