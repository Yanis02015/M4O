package com.l3.m4o;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class WifiController {
    private static final int REQUEST_CODE_ASK_PERMISSION = 1;
    public static final String WIFI_SSID = "WEB_WIFI";
    public static final String WIFI_PASS = "AZEROTH.1406";

    private final Context context;
    private WifiManager wifiManager;

    public WifiController(Context context) {
        this.context = context;
    }

    public void startWifiResult() {
        wifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    System.out.println("C OK ?");
                    startConnection(scanSuccess());
                } else {
                    System.out.println("C PAS OK");
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            System.out.println("C PAS DU TOUT OK");
            scanFailure();
        }
    }

    private void startConnection(List<ScanResult> scanSuccess) {
        for(ScanResult result : scanSuccess){
            if(result.SSID.equals(WIFI_SSID)) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    final WifiNetworkSuggestion suggestion =
                            new WifiNetworkSuggestion.Builder()
                                    .setSsid(WIFI_SSID)
                                    .setWpa2Passphrase(WIFI_PASS)
                                    .setIsAppInteractionRequired(true)
                                    .build();

                    final List<WifiNetworkSuggestion> suggestionsList =
                            new ArrayList<WifiNetworkSuggestion>() {{
                                add(suggestion);
                            }};
                    final WifiManager manager =
                            (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);


                    final int status = manager.addNetworkSuggestions(suggestionsList);
                    if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                        // do error handling here…
                    }
                    final IntentFilter intentFilter =
                            new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

                    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (!intent.getAction().equals(
                                    WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                                return;
                            }
                            // do post connect processing here...
                        }
                    };
                    context.registerReceiver(broadcastReceiver, intentFilter);
                } else {
                    WifiConfiguration wifiConfiguration = new WifiConfiguration();
                    wifiConfiguration.SSID = "\"" + WIFI_SSID + "\"";
                    wifiConfiguration.preSharedKey = "\"" + WIFI_PASS + "\"";

                    int netId = wifiManager.addNetwork(wifiConfiguration);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(netId, true);
                    wifiManager.reconnect();

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Permission non accordées", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                }
            }
        }
    }

    private List<ScanResult> scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        System.out.println("new result size = " + results.size());
        return results;
    }
    private void scanFailure() {
        List<ScanResult> results = wifiManager.getScanResults();
        System.out.println("old result size = " + results.size());
    }
}
