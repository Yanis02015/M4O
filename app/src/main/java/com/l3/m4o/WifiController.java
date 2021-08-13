package com.l3.m4o;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
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
    private final WifiManager wifiManager;

    public WifiController(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void startWifiResult() {

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                boolean connectionSuccess = isConnected();
                System.out.println(success + " " + connectionSuccess);
                if (success && !isConnected()) {
                    System.out.println("On est pas connecté >>> startConnection");
                    startConnection(getListScanResult());
                } else if(!isConnected()) {
                    System.out.println("Impossible de scanner");
                    scanFailure();
                } else {
                    System.out.println("On est connecté au bon wifi");
                    MainActivity.listen.setValue("Do action");
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            System.out.println("!success >>> scanFailure");
            scanFailure();
        }
    }

    public void startConnection() {
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
                System.out.println("Wifi not added");
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
                    System.out.println("added réussi !");
                    // do post connect processing here...
                }
            };
            context.registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private void startConnection(List<ScanResult> listScanResult) {
        System.out.println("startConnection >>>>> ");
        for(ScanResult result : listScanResult){
            if(result.SSID.equals(WIFI_SSID)) {
                // After SDK 29, it is imperative to use this way
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
                    //final WifiManager manager =
                    //        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);


                    final int status = wifiManager.addNetworkSuggestions(suggestionsList);
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
                            //MainActivity.listen.setValue("Go to action");
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

    private List<ScanResult> getListScanResult() {
        List<ScanResult> results = wifiManager.getScanResults();
        System.out.println("new result size = " + results.size());
        return results;
    }
    private void scanFailure() {
        List<ScanResult> results = wifiManager.getScanResults();
        System.out.println("old result size = " + results.size());
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo.isConnected()) {
            System.out.println("co est ok");
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            System.out.println(wifiInfo.getSSID());
            return wifiInfo.getSSID().equals("\"" + WIFI_SSID + "\"");
        }
        else {
            System.out.println("else");
            return false;
        }
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }
}
