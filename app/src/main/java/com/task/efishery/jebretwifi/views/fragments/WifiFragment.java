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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.task.efishery.jebretwifi.models.Connection;
import com.task.efishery.jebretwifi.views.activities.MainActivity;
import com.task.efishery.jebretwifi.views.components.ConnectionListAdapter;

import java.util.ArrayList;
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
    List<Connection> connections = new ArrayList<>();
    ConnectionListAdapter adapter;


    public WifiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ConnectionListAdapter(getActivity(), connections);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        ButterKnife.inject(this,view);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected wifi
                Connection connection = (Connection) adapter.getItem(position);



                TextView label = view.findViewById(R.id.label);

                String ssid = label.getText().toString();
                if(connection.getIsConnected()){
                    Toast.makeText(getContext(), "You have been connected", Toast.LENGTH_SHORT).show();
                }else {
                    connectToWifi(ssid, connection.getIsWPA(), position);
                }

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getWifi();
    }

    private void getWifi(){
        connections.clear();
        adapter.notifyDataSetChanged();

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
                    connections.clear();
                    adapter.notifyDataSetChanged();

                    List<ScanResult> wifiScanList = wifiManager.getScanResults();
                    securities = new Boolean[wifiScanList.size()];;

                    for (ScanResult scanResult : wifiScanList) {

                        int level = WifiManager.calculateSignalLevel(scanResult.level, 4);
                        String wifi;
                        boolean isWPA = false;
                        String capabilities = scanResult.capabilities;
                        boolean isConnected = false;
                        if(("\""+scanResult.SSID+"\"").equals(wifiManager.getConnectionInfo().getSSID().toString())){
                            isConnected = true;
                        }

                        if(capabilities.contains("WPA2-PSK")){
                            isWPA = true;
                        } else if(capabilities.contains("ESS")){
                            isWPA = false;
                        }
                        wifi = ((scanResult).toString());
                        String[] temp = wifi.split(",");
                        String wifiName = temp[0].substring(5).trim();

                        connections.add(new Connection(wifiName,level,isWPA,isConnected));
                    }
                    adapter.notifyDataSetChanged();
                }

            }, filter);
            wifiManager.startScan();
        } else {
            Toast.makeText(getActivity(),"Please turn on your WIFI",Toast.LENGTH_LONG).show();
        }
    }

    private void connectToWifi(final String wifiSSID, Boolean isWPA, final int position) {
        if(isWPA) {
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_connect);
            TextView tv = dialog.findViewById(R.id.title_dialog);
            tv.setText("Connect to " + wifiSSID);
            dialogButton = dialog.findViewById(R.id.okButton);
            pass = dialog.findViewById(R.id.textPassword);

            // if button is clicked, connect to the network;
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String checkPassword = pass.getText().toString();
                    finallyConnect(wifiSSID,checkPassword,position);
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else{
            finallyConnect(wifiSSID,"",position);
        }
    }

    private void finallyConnect(String wifiSSID, String wifiPass, int position){

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", wifiSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", wifiPass);
        if(wifiPass.equals("")) {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }else{
            wifiConfig.preSharedKey = "\"" + wifiPass + "\"";
        }
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

    }

}