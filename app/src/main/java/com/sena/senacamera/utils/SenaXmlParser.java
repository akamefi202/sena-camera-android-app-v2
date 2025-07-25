package com.sena.senacamera.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.telecom.Call;

import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SenaXmlParser extends AsyncTask<String, Void, Boolean> {
    private final String TAG = SenaXmlParser.class.getSimpleName();

    private final String SENA_CAMERA_XML_URL = "https://api.senachina.com/support/SenaCamera/sncam.xml";
    private Context context;

    public List<String> firmwareLangaugeList = new ArrayList<>(), firmwareUrlList = new ArrayList<>();
    public String userGuideUrl = "", quickGuideUrl = "", videoGuideUrl = "", supportUrl = "", forumUrl = "", mailingListUrl = "", termsUrl = "", privacyPolicyUrl = "";
    public String latestFirmwareVersion = "";

    // instance
    private static final class InstanceHolder {
        private static final SenaXmlParser instance = new SenaXmlParser();
    }
    public static SenaXmlParser getInstance() {
        return SenaXmlParser.InstanceHolder.instance;
    }

    public SenaXmlParser() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        // initialize values
        firmwareLangaugeList = new ArrayList<>();
        firmwareUrlList = new ArrayList<>();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        cm.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                AppLog.i(TAG, "onAvailable");
                try {
                    URL url = new URL(SENA_CAMERA_XML_URL);

                    HttpURLConnection connection = (HttpURLConnection) network.openConnection(url);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        AppLog.e(TAG, "failed to connect: " + responseCode);
                        return;
                    }

                    InputStream inputStream = connection.getInputStream();
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(inputStream, null);

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
                } catch (Exception e) {
                    AppLog.e(TAG, "error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onUnavailable() {
                AppLog.i(TAG, "onUnavailable");
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
        } else if (tag.equals("quickGuide")) {
            quickGuideUrl = parser.getAttributeValue(null, "url");
        } else if (tag.equals("userGuide")) {
            userGuideUrl = parser.getAttributeValue(null, "url");
        } else if (tag.equals("videoGuide")) {
            videoGuideUrl = parser.getAttributeValue(null, "url");
        } else if (tag.equals("otaLanguage")) {
            String language = parser.getAttributeValue(null, "name");
            firmwareLangaugeList.add(language);
        } else if (tag.equals("package")) {
            String url = parser.getAttributeValue(null, "url");
            firmwareUrlList.add(url);
        } else if (tag.equals("product")) {
            latestFirmwareVersion = parser.getAttributeValue(null, "latestVersion");
        }
    }
}
