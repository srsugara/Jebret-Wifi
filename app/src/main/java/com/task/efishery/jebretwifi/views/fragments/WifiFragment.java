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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.task.efishery.jebretwifi.R;
import com.task.efishery.jebretwifi.views.activities.MainActivity;
import com.task.efishery.jebretwifi.views.components.CustomListAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


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
    int strengths[];
    Boolean securities[];

    public WifiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        ButterKnife.inject(this,view);
        getWifi();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected wifi
                Boolean security = securities[position];
                TextView label = (TextView) view.findViewById(R.id.label);
                String ssid = label.getText().toString();
                connectToWifi(ssid,security,position);
                Toast.makeText(getActivity(),"Wifi SSID : "+ssid,Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    private void getWifi(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) {
            getActivity().registerReceiver(new BroadcastReceiver() {

                @SuppressLint("UseValueOf")
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<ScanResult> wifiScanList = wifiManager.getScanResults();
                    securities = new Boolean[wifiScanList.size()];;
                    wifis = new String[wifiScanList.size()];
                    strengths = new int[wifiScanList.size()];
                    for (int i = 0; i < wifiScanList.size(); i++) {
                        int level = WifiManager.calculateSignalLevel(wifiScanList.get(i).level, 4);
                        String capabilities = wifiScanList.get(i).capabilities;
                        if(capabilities.contains("WPA2-PSK")){
                            securities[i] = true;
                        } else if(capabilities.contains("ESS")){
                            securities[i] = false;
                        }
                        wifis[i] = ((wifiScanList.get(i)).toString());
                        strengths[i] = level;
                    }
                    String filtered[] = new String[wifiScanList.size()];

                    int counter = 0;
                    for (String eachWifi : wifis) {
                        String[] temp = eachWifi.split(",");
                        filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength
                        counter++;

                    }
                    list.setAdapter(new CustomListAdapter((MainActivity) getActivity(),filtered, strengths));
                }

            }, filter);
            wifiManager.startScan();
        } else {
            Toast.makeText(getActivity(),"Please turn on your WIFI",Toast.LENGTH_LONG).show();
        }
    }

    private void connectToWifi(final String wifiSSID, Boolean security, final int position) {
        if(security) {
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_connect);
            TextView tv = dialog.findViewById(R.id.title_dialog);
            tv.setText("Connect to " + wifiSSID);
            dialogButton = (Button) dialog.findViewById(R.id.okButton);
            pass = (EditText) dialog.findViewById(R.id.textPassword);

            // if button is clicked, connect to the network;
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String checkPassword = pass.getText().toString();
                    finallyConnect(checkPassword, wifiSSID,position);
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else{
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", wifiSSID);

            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"\"" + wifiSSID + "\"\"";
            wifiManager.addNetwork(conf);
        }
    }

    private void finallyConnect(String networkPass, String networkSSID,int position) {
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

        list.getAdapter().getItem(position);
    }

}