package com.task.efishery.jebretwifi.dagger;

import com.task.efishery.jebretwifi.views.activities.MainActivity;
import com.task.efishery.jebretwifi.views.fragments.QuoteFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by syauqi on 03/09/17.
 */

@Singleton
@Component(
        modules={JebretWifiModule.class}
)
public interface JebretWifiComponent {
    void inject(QuoteFragment quoteFragment);
}
