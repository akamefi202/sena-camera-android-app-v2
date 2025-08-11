package com.sena.senacamera.data.SystemInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;

import java.util.ArrayList;
import java.util.List;

public class MWifiManager {
    private static final String TAG = MWifiManager.class.getSimpleName();
    private static final String WIFI_SSID_UNKNOWN = "unknown";

    @SuppressLint("MissingPermission")
    public static String getSsid(Context context) {
        if (!isWifiEnabled(context)) {
            AppLog.e(TAG, "----------ssid is null=");
            return null;
        }
        //android 8.0及以下
        String ssid = WIFI_SSID_UNKNOWN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
                if (ssid.contains("\"")) {
                    ssid =ssid.replace("\"","");
                }
            } else {
                AppLog.i(TAG, "getSsid wifiInfo is null");
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo2 = mConnectivityManager.getActiveNetworkInfo();
            AppLog.i(TAG, "getSsid wifiInfo2:" + wifiInfo2);
            if (wifiInfo2 !=null) {
                AppLog.i(TAG, "getSsid wifiInfo2.getExtraInfo()" + wifiInfo2.getExtraInfo());
            }
            if (wifiInfo2 == null || wifiInfo2.getExtraInfo() == null) {
                ssid = getSsidByNetworkId(context);
            } else {
                String wifiName = wifiInfo2.getExtraInfo();
                ssid =  wifiName.replaceAll("\"", "");
            }
        } else {
            WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
                if (ssid.contains("\"")) {
                    ssid =ssid.replace("\"","");
                }
            } else {
                AppLog.i(TAG, "getSsid wifiInfo is null");
            }
        }
        //android 8.0以后

        AppLog.i(TAG, "getSsid ssid:" + ssid);
        return ssid;
    }
    /**
     * 华为android 9.0 获取 ssid
     */
    @SuppressLint("MissingPermission")
    private static String getSsidByNetworkId(Context context) {
        AppLog.d(TAG, "getSsidByNetworkId ");
        String ssid = null;
        WifiManager wifiManager = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        if (null != wifiManager) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int networkId = wifiInfo.getNetworkId();
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiConfiguration:configuredNetworks) {
                if (wifiConfiguration.networkId==networkId) {
                    ssid=wifiConfiguration.SSID;
                    break;
                }
            }
        }

        if (ssid != null && ssid.contains("\"")) {
            ssid =ssid.replace("\"","");
        }
        AppLog.d(TAG, "getSsidByNetworkId ssid:" + ssid);
        return ssid;
    }

//    public static String getIp(Context context) {
//        String ip = "192.168.1.1";
//        if (HotSpot.isApEnabled(context)) {
//            String value = HotSpot.getFirstConnectedHotIP();
//            if (value != null) {
//                ip = value;
//            }
//        } else if (AppInfo.youtubeLive) {
//            String value = AppSharedPreferences.readIp(context);
//            if (value != null) {
//                ip = value;
//            }
//        }
//        AppLog.d(TAG,"getIp ip=" + ip);
//        return ip;
//    }
    /**
     ** 判断WIFI网络是否可用
     ** @param context
     ** @return
     *      
     */

    @SuppressLint("MissingPermission")
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo != null && wifiNetworkInfo.isConnected();

    }
    public static boolean isWifiEnabled(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return mWifiManager != null && mWifiManager.isWifiEnabled();
    }

    public static void disconnect(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.disconnect();
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    public static void removeCurrentNetwork(Context context, String ssid, String password, ConnectivityManager.NetworkCallback callback) {
        WifiNetworkSuggestion suggestion = new WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build();

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiNetworkSuggestion> suggestions = new ArrayList<>();
        suggestions.add(suggestion);

        int status = wifiManager.removeNetworkSuggestions(suggestions);

        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            AppLog.i(TAG, "remove suggestion added successfully");
        } else {
            AppLog.i(TAG, "remove failed to add suggestion: " + status);
        }
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    public static void connect(Context context, String ssid, String password, Callback callback) {
        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build();

        // build the wifi request
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build();

        // connect to wifi network
        Handler handler = new Handler(Looper.getMainLooper());
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // define network callback
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                handler.removeCallbacksAndMessages(null);

                connectivityManager.bindProcessToNetwork(network);
                if (callback != null) {
                    callback.processSucceed();
                }
            }

            @Override
            public void onUnavailable() {
                handler.removeCallbacksAndMessages(null);

                if (callback != null) {
                    callback.processFailed();
                }
            }
        };

        handler.postDelayed(() -> {
            if (callback != null) {
                callback.processFailed();
            }
        }, 30000);
        connectivityManager.requestNetwork(networkRequest, networkCallback);

//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
//        // suggest network
//        WifiNetworkSuggestion suggestion = new WifiNetworkSuggestion.Builder()
//                .setSsid(ssid)
//                .setWpa2Passphrase(password)
//                .build();
//
//        List<WifiNetworkSuggestion> suggestions = new ArrayList<>();
//        suggestions.add(suggestion);
//
//        int status = wifiManager.addNetworkSuggestions(suggestions);
//
//        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//            AppLog.i(TAG, "connect suggestion added successfully");
//        } else {
//            AppLog.i(TAG, "connect failed to add suggestion: " + status);
//        }
    }
}
