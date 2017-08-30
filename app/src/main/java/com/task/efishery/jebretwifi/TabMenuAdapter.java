package com.task.efishery.jebretwifi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by syauqi on 30/08/17.
 */

public class TabMenuAdapter extends FragmentPagerAdapter {

    private String[] titles = {"Wifi", "QR Code", "Quote"};

    public TabMenuAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int num) {
        // Buat implementasi more fragment buat pengecekan disini
        if(num == 1){
            return new QRCodeFragment();
        } else if(num == 2){
            return new QuoteFragment();
        }
        return new WifiFragment();
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int tabposition) {
        return titles[tabposition];
    }
}