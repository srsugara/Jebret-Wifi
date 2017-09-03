package com.task.efishery.jebretwifi.dagger;

import android.app.Application;

import dagger.internal.DaggerCollections;

/**
 * Created by syauqi on 03/09/17.
 */

public class JebretWifiApplication extends Application {
    private JebretWifiComponent jebretWifiComponent;

    @Override
    public void onCreate(){
        super.onCreate();

        jebretWifiComponent = DaggerJebretWifiComponent.builder()
                .jebretWifiModule(new JebretWifiModule())
                .build();
    }

    public JebretWifiComponent getJebretWifiComponent(){
        return jebretWifiComponent;
    }
}
