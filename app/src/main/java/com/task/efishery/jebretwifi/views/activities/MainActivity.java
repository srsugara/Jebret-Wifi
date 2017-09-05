package com.task.efishery.jebretwifi.views.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.task.efishery.jebretwifi.views.fragments.QuoteFragment;
import com.task.efishery.jebretwifi.R;
import com.task.efishery.jebretwifi.views.components.TabMenuAdapter;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends AppCompatActivity implements MaterialTabListener {

    @InjectView(R.id.tabHost)
    MaterialTabHost tabHost;
    @InjectView(R.id.viewPager)
    ViewPager viewPager;
    @InjectView(R.id.toolBar)
    Toolbar toolBar;

    private TabMenuAdapter androidAdapter;

    private static final int ACCESS_COARSE_LOCATION = 100;
    private static final int CAMERA = 101;
    private static final int ACCESS_FINE_LOCATION = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //android toolbar
        this.setSupportActionBar(toolBar);

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

        doCheckPermissions();
    }

    /**
     * Check permission
     */
    public void doCheckPermissions() {
        if (isAccessesGranted()) {
            gettingPermissions();
        }
    }

    /**
     * Check if our app has all required access
     * @return boolean
     */
    public boolean isAccessesGranted() {
        int granted = PackageManager.PERMISSION_GRANTED;
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != granted
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != granted
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != granted;
    }

    /**
     * Get required permission
     */
    public void gettingPermissions() {
        Map<String, Integer> permissions = new HashMap<>();
        permissions.put(Manifest.permission.ACCESS_COARSE_LOCATION, ACCESS_COARSE_LOCATION);
        permissions.put(Manifest.permission.CAMERA, CAMERA);
        permissions.put(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);

        for (String permission : permissions.keySet()) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Toast.makeText(MainActivity.this,"getting permission",Toast.LENGTH_LONG).show();
                }
                requestAccess(permission, permissions.get(permission));
                break;
            }
        }
    }

    /**
     * Request permission to system
     * @param permission
     * @param requestCode
     */
    public void requestAccess(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_COARSE_LOCATION:
            case CAMERA:
            case ACCESS_FINE_LOCATION:
            default:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"kjbvkjd",Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: disable functionality that needs this permissions
                    Toast.makeText(MainActivity.this,"Access Denied",Toast.LENGTH_SHORT).show();
                }
                doCheckPermissions();
                break;
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