package com.task.efishery.jebretwifi.dagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.task.efishery.jebretwifi.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by syauqi on 03/09/17.
 */

@Module
public class JebretWifiModule {
    private String url="http://api.forismatic.com";

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    public Retrofit initialRetrofit(Gson gson){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }
}
