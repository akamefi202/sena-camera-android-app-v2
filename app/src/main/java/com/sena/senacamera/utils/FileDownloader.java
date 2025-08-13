package com.sena.senacamera.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader extends AsyncTask<String, Void, Boolean> {
    private final String TAG = FileDownloader.class.getSimpleName();

    private Context context;
    private Callback callback;

    public FileDownloader(Context context) {
        this.context = context;
    }

    public FileDownloader(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        Handler handler = new Handler(Looper.getMainLooper());
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        handler.postDelayed(() -> {
            if (callback != null) {
                callback.processFailed();
            }
        }, 60000);
        cm.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                handler.removeCallbacksAndMessages(null);

                try {
                    String fileUrl = urls[0]; // URL to download
                    String filePath = urls[1]; // temporary file name
                    AppLog.i(TAG, "fileUrl: " + fileUrl);
                    URL url = new URL(fileUrl);
                    File file = new File(filePath);
                    HttpURLConnection conn = (HttpURLConnection) network.openConnection(url);

                    conn.setConnectTimeout(10000);
                    conn.connect();

                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        if (callback != null) {
                            callback.processFailed();
                        }
                        return;
                        //return "Server returned HTTP " + conn.getResponseCode();
                    }

                    InputStream input = conn.getInputStream();
                    FileOutputStream output = new FileOutputStream(file);

                    byte[] data = new byte[4096];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    output.close();
                    input.close();

                    //return "Downloaded to: " + file.getAbsolutePath();
                    if (callback != null) {
                        callback.processSucceed();
                    }
                } catch (Exception e) {
                    AppLog.e(TAG, "error: " + e.getMessage());
                    e.printStackTrace();
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
}