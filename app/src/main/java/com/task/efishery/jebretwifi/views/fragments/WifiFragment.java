package com.task.efishery.jebretwifi.views.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.task.efishery.jebretwifi.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemSelected;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiFragment extends Fragment {

    @InjectView(R.id.listview)
    ListView list;

    TextView textSSID;
    Button dialogButton;
    EditText pass;
    Dialog dialog;

    WifiManager wifiManager;
    NetworkInfo networkInfo;
    String wifis[];

    public WifiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        ButterKnife.inject(this,view);
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
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(wifiManager.isWifiEnabled()) {
                getActivity().registerReceiver(new BroadcastReceiver() {

                    @SuppressLint("UseValueOf")
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        List<ScanResult> wifiScanList = wifiManager.getScanResults();
                        wifis = new String[wifiScanList.size()];
                        for (int i = 0; i < wifiScanList.size(); i++) {
                            int level = WifiManager.calculateSignalLevel(wifiScanList.get(i).level, 5);
                            wifis[i] = "        Strength : " + level + "    " + ((wifiScanList.get(i)).toString());
                        }
                        String filtered[] = new String[wifiScanList.size()];
                        int counter = 0;
                        for (String eachWifi : wifis) {
                            String[] temp = eachWifi.split(",");
                            filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength

                            counter++;

                        }
                        list.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.label, filtered));
                    }

                }, filter);
                wifiManager.startScan();
            } else {
                Toast.makeText(getActivity(),"Please turn on your WIFI",Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(getActivity(),"Please check your connection",Toast.LENGTH_LONG).show();
        }
    }

    private void connectToWifi(final String wifiSSID) {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_connect);
        dialog.setTitle("Connect to Network");

        textSSID = dialog.findViewById(R.id.textSSID1);
        dialogButton = (Button) dialog.findViewById(R.id.okButton);
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