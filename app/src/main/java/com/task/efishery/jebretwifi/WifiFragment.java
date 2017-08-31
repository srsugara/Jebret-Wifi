package com.task.efishery.jebretwifi;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiFragment extends Fragment {

    WifiManager wifiManager;
    ListView list;
    String wifis[];
    EditText pass;

    public WifiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        list=(ListView) view.findViewById(R.id.listview);
        getWifi();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected wifi
                String ssid = ((TextView) view).getText().toString();
                connectToWifi(ssid.substring(22).trim());
                Toast.makeText(getActivity(),"Wifi SSID : "+ssid,Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    private void getWifi(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiManager =(WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        getActivity().registerReceiver(new BroadcastReceiver(){

            @SuppressLint("UseValueOf") @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> wifiScanList = wifiManager.getScanResults();
                wifis = new String[wifiScanList.size()];
                for(int i = 0; i < wifiScanList.size(); i++){
                    int level = WifiManager.calculateSignalLevel(wifiScanList.get(i).level, 5);
                    wifis[i] = "        Strength : "+level+"    "+((wifiScanList.get(i)).toString());
                }
                String filtered[] = new String[wifiScanList.size()];
                int counter = 0;
                for (String eachWifi : wifis) {
                    String[] temp = eachWifi.split(",");
                    filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength

                    counter++;

                }
                list.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.list_item,R.id.label, filtered));
            }

        },filter);
        wifiManager.startScan();
    }

    private void connectToWifi(final String wifiSSID) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_connect);
        dialog.setTitle("Connect to Network");
        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);

        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        pass = (EditText) dialog.findViewById(R.id.textPassword);
        textSSID.setText(wifiSSID);

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();
                finallyConnect(checkPassword, wifiSSID);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void finallyConnect(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        // remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + networkSSID + "\"\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
        wifiManager.addNetwork(conf);
    }

}

//public class MainActivity extends AppCompatActivity {
//    WifiManager wifiManager;
//    ListView list;
//    String wifis[];
//    EditText pass;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        list=(ListView) findViewById(R.id.listview);
//        getWifi();
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                // selected item
//                String ssid = ((TextView) view).getText().toString();
//                connectToWifi(ssid);
//                Toast.makeText(MainActivity.this,"Wifi SSID : "+ssid,Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private void getWifi(){
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        wifiManager =
//                (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        registerReceiver(new BroadcastReceiver(){
//
//            @SuppressLint("UseValueOf") @Override
//            public void onReceive(Context context, Intent intent) {
//                List<ScanResult> wifiScanList = wifiManager.getScanResults();
//                wifis = new String[wifiScanList.size()];
//                for(int i = 0; i < wifiScanList.size(); i++){
//                    wifis[i] = ((wifiScanList.get(i)).toString());
//                }
//                String filtered[] = new String[wifiScanList.size()];
//                int counter = 0;
//                for (String eachWifi : wifis) {
//                    String[] temp = eachWifi.split(",");
//
//                    filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength
//
//                    counter++;
//
//                }
//                list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,R.id.label, filtered));
//            }
//
//        },filter);
//        wifiManager.startScan();
//    }
//
//    private void connectToWifi(final String wifiSSID) {
//        final Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_connect);
//        dialog.setTitle("Connect to Network");
//        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);
//
//        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
//        pass = (EditText) dialog.findViewById(R.id.textPassword);
//        textSSID.setText(wifiSSID);
//
//        // if button is clicked, connect to the network;
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String checkPassword = pass.getText().toString();
//                finallyConnect(checkPassword, wifiSSID);
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }
//
//    private void finallyConnect(String networkPass, String networkSSID) {
//        WifiConfiguration wifiConfig = new WifiConfiguration();
//        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
//        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
//
//        // remember id
//        int netId = wifiManager.addNetwork(wifiConfig);
//        wifiManager.disconnect();
//        wifiManager.enableNetwork(netId, true);
//        wifiManager.reconnect();
//
//        WifiConfiguration conf = new WifiConfiguration();
//        conf.SSID = "\"\"" + networkSSID + "\"\"";
//        conf.preSharedKey = "\"" + networkPass + "\"";
//        wifiManager.addNetwork(conf);
//    }
//
//}
