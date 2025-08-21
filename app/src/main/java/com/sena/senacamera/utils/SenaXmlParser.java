package com.sena.senacamera.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class SenaXmlParser extends AsyncTask<String, Void, Boolean> {
    private final String TAG = SenaXmlParser.class.getSimpleName();

    private final String SENA_CAMERA_XML_URL_CHINA = "https://api.senachina.com/support/SenaCamera/sncam.xml";
    private final String SENA_CAMERA_XML_URL = "https://api.sena.com/support/SenaCamera/sncam.xml";
    private Context context;
    private Callback callback;

    private HashMap<String, List<String>> firmwareLanguageListMap, firmwareUrlListMap;
    private HashMap<String, String> deviceModelMap, userGuideUrlMap, quickGuideUrlMap, videoGuideUrlMap, firmwareVersionMap;
    public String supportUrl = "", forumUrl = "", mailingListUrl = "", termsUrl = "", privacyPolicyUrl = "";
    public boolean isExecuted = false;
    private String parsingModelName = "", currentModelName = "";

    // instance
    private static final class InstanceHolder {
        private static final SenaXmlParser instance = new SenaXmlParser();
    }
    public static SenaXmlParser getInstance() {
        return SenaXmlParser.InstanceHolder.instance;
    }

    public SenaXmlParser() {
        // initialize device model map consists of product id & model name
        deviceModelMap = new HashMap<>();
        deviceModelMap.put("3568", "PRISM 2");
        deviceModelMap.put("096a", "PHANTOM CAMERA");

        initData();
    }

    public void initData() {
        firmwareLanguageListMap = new HashMap<>();
        firmwareUrlListMap = new HashMap<>();
        userGuideUrlMap = new HashMap<>();
        quickGuideUrlMap = new HashMap<>();
        videoGuideUrlMap =  new HashMap<>();
        firmwareVersionMap = new HashMap<>();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setCurrentModel(String productId) {
        if (deviceModelMap.get(productId) != null) {
            currentModelName = deviceModelMap.get(productId);
        } else {
            currentModelName = "";
        }
    }

    public List<String> getFirmwareLanguageList() {
        if (currentModelName.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> list = firmwareLanguageListMap.get(currentModelName);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public List<String> getFirmwareUrlList() {
        if (currentModelName.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> list = firmwareUrlListMap.get(currentModelName);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public String getUserGuideUrl() {
        if (currentModelName.isEmpty()) {
            return "";
        }

        String value = userGuideUrlMap.get(currentModelName);
        if (value == null) {
            return "";
        }
        return value;
    }

    public String getQuickGuideUrl() {
        if (currentModelName.isEmpty()) {
            return "";
        }

        String value = quickGuideUrlMap.get(currentModelName);
        if (value == null) {
            return "";
        }
        return value;
    }

    public String getVideoGuideUrl() {
        if (currentModelName.isEmpty()) {
            return "";
        }

        String value = videoGuideUrlMap.get(currentModelName);
        if (value == null) {
            return "";
        }
        return value;
    }

    public String getLatestFirmwareVersion() {
        if (currentModelName.isEmpty()) {
            return "";
        }

        String value = firmwareVersionMap.get(currentModelName);
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        isExecuted = true;

        if (context == null) {
            AppLog.e(TAG, "doInBackground: context is null");
            return false;
        }

        Handler handler = new Handler(Looper.getMainLooper());
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // akamefi202: to be fixed
        // read xml data via wifi
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        handler.postDelayed(() -> {
            if (callback != null) {
                callback.processFailed();
            }
        }, 30000);
        cm.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                handler.removeCallbacksAndMessages(null);

                try {
                    URL url = new URL(TimeZone.getDefault().getID().equals("Asia/Shanghai")? SENA_CAMERA_XML_URL_CHINA: SENA_CAMERA_XML_URL);

                    HttpURLConnection connection = (HttpURLConnection) network.openConnection(url);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        AppLog.e(TAG, "failed to connect: " + responseCode);
                        if (callback != null) {
                            callback.processFailed();
                        }
                        return;
                    }

                    InputStream inputStream = connection.getInputStream();
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(inputStream, null);

                    // initialize values
                    initData();

                    int eventType = parser.getEventType();
                    String tagName = null;
                    String currentText = "";

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                tagName = parser.getName();
                                getValues(tagName, parser);
                                break;

                            case XmlPullParser.TEXT:
                                currentText = parser.getText();
                                break;
                        }
                        eventType = parser.next();
                    }

                    inputStream.close();

                    writeToSharedPref();

                    if (callback != null) {
                        callback.processSucceed();
                    }
                } catch (Exception e) {
                    AppLog.e(TAG, "error: " + e.getMessage());
                    e.printStackTrace();

                    if (callback != null) {
                        callback.processFailed();
                    }
                }
            }

            @Override
            public void onUnavailable() {
                handler.removeCallbacksAndMessages(null);

                if (callback != null) {
                    callback.processFailed();
                }
            }
        });

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {

    }

    public void getValues(String tag, XmlPullParser parser) {
        if (tag.equals("tu")) {
            termsUrl = parser.getAttributeValue(null, "url");
        } else if (tag.equals("pp")) {
            privacyPolicyUrl = parser.getAttributeValue(null, "url");
        } else if (tag.equals("profile")) {
            mailingListUrl = parser.getAttributeValue(null, "url");
        } else if (tag.equals("support")) {
            supportUrl = parser.getAttributeValue(null, "url");
        } else if (tag.equals("forum")) {
            forumUrl = parser.getAttributeValue(null, "url");
        } else if (tag.equals("productMenu")) {
            String id = parser.getAttributeValue(null, "id");
            if (!parsingModelName.isEmpty()) {
                switch (id) {
                    case "quickGuide":
                        quickGuideUrlMap.put(parsingModelName, parser.getAttributeValue(null, "url"));
                        break;
                    case "userGuide":
                        userGuideUrlMap.put(parsingModelName, parser.getAttributeValue(null, "url"));
                        break;
                    case "videoGuide":
                        videoGuideUrlMap.put(parsingModelName, parser.getAttributeValue(null, "url"));
                        break;
                }
            }
        } else if (tag.equals("userGuide")) {
            if (!parsingModelName.isEmpty()) {
                userGuideUrlMap.put(parsingModelName, parser.getAttributeValue(null, "url"));
            }
        } else if (tag.equals("videoGuide")) {
            if (!parsingModelName.isEmpty()) {
                videoGuideUrlMap.put(parsingModelName, parser.getAttributeValue(null, "url"));
            }
        } else if (tag.equals("otaLanguage")) {
            if (!parsingModelName.isEmpty()) {
                List<String> list = firmwareLanguageListMap.get(parsingModelName);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(parser.getAttributeValue(null, "name"));
                firmwareLanguageListMap.put(parsingModelName, list);
            }
        } else if (tag.equals("package")) {
            if (!parsingModelName.isEmpty()) {
                List<String> list = firmwareUrlListMap.get(parsingModelName);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(parser.getAttributeValue(null, "url"));
                firmwareUrlListMap.put(parsingModelName, list);
            }
        } else if (tag.equals("product")) {
            parsingModelName = parser.getAttributeValue(null, "name");
            firmwareVersionMap.put(parsingModelName, parser.getAttributeValue(null, "latestVersion"));
        }
    }

    public void readFromSharedPref() {
        Gson gson = new Gson();
        String json;

        // read firmware language list map
        json = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.FIRMWARE_LANGUAGE_LIST, "");
        if (json != null && !json.isEmpty()) {
            AppLog.i(TAG, "readFromSharedPref: firmwareLanguageListMap: " + json);
            Type type = new TypeToken<HashMap<String, List<String>>>() {}.getType();
            firmwareLanguageListMap = gson.fromJson(json, type);
        } else {
            AppLog.i(TAG, "readFromSharedPref: firmwareLanguageListMap: json is null");
            firmwareLanguageListMap = new HashMap<>();
        }

        // read firmware url list map
        json = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.FIRMWARE_URL_LIST, "");
        if (json != null && !json.isEmpty()) {
            AppLog.i(TAG, "readFromSharedPref: firmwareUrlListMap: " + json);
            Type type = new TypeToken<HashMap<String, List<String>>>() {}.getType();
            firmwareUrlListMap = gson.fromJson(json, type);
        } else {
            AppLog.i(TAG, "readFromSharedPref: firmwareUrlListMap: json is null");
            firmwareUrlListMap = new HashMap<>();
        }

        // read firmware version map
        json = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.LATEST_FIRMWARE_VERSION, "");
        if (json != null && !json.isEmpty()) {
            AppLog.i(TAG, "readFromSharedPref: firmwareVersionMap: " + json);
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            firmwareVersionMap = gson.fromJson(json, type);
        } else {
            AppLog.i(TAG, "readFromSharedPref: firmwareVersionMap: json is null");
            firmwareVersionMap = new HashMap<>();
        }

        // read user guide url map
        json = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.USER_GUIDE_URL, "");
        if (json != null && !json.isEmpty()) {
            AppLog.i(TAG, "readFromSharedPref: userGuideUrlMap: " + json);
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            userGuideUrlMap = gson.fromJson(json, type);
        } else {
            AppLog.i(TAG, "readFromSharedPref: userGuideUrlMap: json is null");
            userGuideUrlMap = new HashMap<>();
        }

        // read quick guide url map
        json = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.QUICK_GUIDE_URL, "");
        if (json != null && !json.isEmpty()) {
            AppLog.i(TAG, "readFromSharedPref: quickGuideUrlMap: " + json);
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            quickGuideUrlMap = gson.fromJson(json, type);
        } else {
            AppLog.i(TAG, "readFromSharedPref: quickGuideUrlMap: json is null");
            quickGuideUrlMap = new HashMap<>();
        }

        // read video guide url map
        json = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.VIDEO_GUIDE_URL, "");
        if (json != null && !json.isEmpty()) {
            AppLog.i(TAG, "readFromSharedPref: videoGuideUrlMap: " + json);
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            videoGuideUrlMap = gson.fromJson(json, type);
        } else {
            AppLog.i(TAG, "readFromSharedPref: videoGuideUrlMap: json is null");
            videoGuideUrlMap = new HashMap<>();
        }

        supportUrl = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.SUPPORT_URL, "");
        forumUrl = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.FORUM_URL, "");
        mailingListUrl = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.MAILING_LIST_URL, "");
        termsUrl = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.TERMS_URL, "");
        privacyPolicyUrl = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.PRIVACY_POLICY_URL, "");
    }

    public void writeToSharedPref() {
        Gson gson = new Gson();
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.FIRMWARE_LANGUAGE_LIST, gson.toJson(firmwareLanguageListMap));
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.FIRMWARE_URL_LIST, gson.toJson(firmwareUrlListMap));
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.LATEST_FIRMWARE_VERSION, gson.toJson(firmwareVersionMap));
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.USER_GUIDE_URL, gson.toJson(userGuideUrlMap));
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.QUICK_GUIDE_URL, gson.toJson(quickGuideUrlMap));
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.VIDEO_GUIDE_URL, gson.toJson(videoGuideUrlMap));
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.SUPPORT_URL, supportUrl);
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.FORUM_URL, forumUrl);
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.MAILING_LIST_URL, mailingListUrl);
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.TERMS_URL, termsUrl);
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.PRIVACY_POLICY_URL, privacyPolicyUrl);
    }
}
