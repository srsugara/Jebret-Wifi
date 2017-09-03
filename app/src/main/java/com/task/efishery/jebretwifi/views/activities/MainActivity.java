package com.task.efishery.jebretwifi.views.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.crashlytics.android.Crashlytics;
import com.task.efishery.jebretwifi.views.fragments.QuoteFragment;
import com.task.efishery.jebretwifi.R;
import com.task.efishery.jebretwifi.views.components.TabMenuAdapter;

import io.fabric.sdk.android.Fabric;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends AppCompatActivity implements MaterialTabListener {

    MaterialTabHost tabHost;
    ViewPager viewPager;
    TabMenuAdapter androidAdapter;
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        //android toolbar
        toolBar = (android.support.v7.widget.Toolbar) this.findViewById(R.id.toolBar);
        this.setSupportActionBar(toolBar);

        //tab host
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);

        //adapter view
        androidAdapter = new TabMenuAdapter(getSupportFragmentManager());
        viewPager.setAdapter(androidAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int tabposition) {
                tabHost.setSelectedNavigationItem(tabposition);
            }
        });

        //for tab position
        for (int i = 0; i < androidAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(androidAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
    }

    //tab on selected
    @Override
    public void onTabSelected(MaterialTab materialTab) {

        viewPager.setCurrentItem(materialTab.getPosition());
    }

    //tab on reselected
    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    //tab on unselected
    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    // view pager adapter
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int num) {
            return new QuoteFragment();
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int tabposition) {
            return "Tab " + tabposition;
        }
    }
}
