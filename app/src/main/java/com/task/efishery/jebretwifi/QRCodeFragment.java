package com.task.efishery.jebretwifi;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;


/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeFragment extends Fragment {

    TextView tvScanResult;
    Button btScan;
    EditText pass;
    WifiManager wifiManager;

    public QRCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        tvScanResult = (TextView) view.findViewById(R.id.tv_scanresult);
        btScan = (Button) view.findViewById(R.id.bt_scan);
        wifiManager =(WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Membuat intent baru untuk memanggil CaptureActivity bawaan ZXing
                Intent captureIntent = new Intent(getActivity(), CaptureActivity.class);

                // Kemudian kita mengeset pesan yang akan ditampilkan ke user saat menjalankan QRCode scanning
                CaptureActivityIntents.setPromptMessage(captureIntent, "Barcode scanning...");

                // Melakukan startActivityForResult, untuk menangkap balikan hasil dari QR Code scanning
                startActivityForResult(captureIntent, 0);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String value = data.getStringExtra("SCAN_RESULT");
                tvScanResult.setText(value);
                connectToWifi(value);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                tvScanResult.setText("Scanning Gagal, mohon coba lagi.");
            }
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
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
