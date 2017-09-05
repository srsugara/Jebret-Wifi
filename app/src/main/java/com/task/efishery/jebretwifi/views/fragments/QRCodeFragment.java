package com.task.efishery.jebretwifi.views.fragments;


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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.task.efishery.jebretwifi.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;


/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeFragment extends Fragment {

    @InjectView(R.id.tv_scanresult)
    TextView tvScanResult;

    private WifiManager wifiManager;

    public QRCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        ButterKnife.inject(this,view);

        wifiManager =(WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        return view;
    }

    @OnClick(R.id.bt_scan)
    public void onClickScan(){
        // Membuat intent baru untuk memanggil CaptureActivity bawaan ZXing
        Intent captureIntent = new Intent(getActivity(), CaptureActivity.class);

        // Kemudian kita mengeset pesan yang akan ditampilkan ke user saat menjalankan QRCode scanning
        CaptureActivityIntents.setPromptMessage(captureIntent, "Barcode scanning...");

        // Melakukan startActivityForResult, untuk menangkap balikan hasil dari QR Code scanning
        startActivityForResult(captureIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String value = data.getStringExtra("SCAN_RESULT");
                tvScanResult.setText(value);
                connectToWifi(value);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                tvScanResult.setText("Scanning Failed, please try again.");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void connectToWifi(final String wifiSSID) {
        Button dialogButton;
        final EditText pass;
        final Dialog dialog;

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
                finallyConnect(checkPassword, wifiSSID);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void finallyConnect(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        // remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

}
