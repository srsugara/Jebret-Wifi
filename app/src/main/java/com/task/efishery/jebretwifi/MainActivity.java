package com.task.efishery.jebretwifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiManager wifiManager;
    ListView list;
    String wifis[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=(ListView) findViewById(R.id.listview);
        getWifi();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                String ssid = ((TextView) view).getText().toString();
                Toast.makeText(MainActivity.this,"Wifi SSID : "+ssid,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getWifi(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiManager =
                (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(new BroadcastReceiver(){

            @SuppressLint("UseValueOf") @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> wifiScanList = wifiManager.getScanResults();
                wifis = new String[wifiScanList.size()];
                for(int i = 0; i < wifiScanList.size(); i++){
                    wifis[i] = ((wifiScanList.get(i)).toString());
                }
                String filtered[] = new String[wifiScanList.size()];
                int counter = 0;
                for (String eachWifi : wifis) {
                    String[] temp = eachWifi.split(",");

                    filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength

                    counter++;

                }
                list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,R.id.label, filtered));
            }

        },filter);
        wifiManager.startScan();
    }
}
