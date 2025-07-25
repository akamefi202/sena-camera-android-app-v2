package com.sena.senacamera.MyCamera;

import android.util.Log;

import com.sena.senacamera.log.AppLog;
import com.icatchtek.control.customer.ICatchCameraSession;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchTransportException;
import com.icatchtek.reliant.customer.transport.ICatchITransport;

public class CommandSession {
    private static final String TAG = CommandSession.class.getSimpleName();

    private static int scanFlag;
    private ICatchCameraSession session;
    private String ipAddress;
    private String uid;
    private String username;
    private String password;
    private boolean sessionPrepared = false;

    public CommandSession(String ipAddress, String uid, String username, String password) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.uid = uid;
    }

    public CommandSession() {
    }

    public boolean prepareSession(ICatchITransport itrans) {
        // TODO Auto-generated constructor stub
        try {
            ICatchCameraSession.getCameraConfig(itrans).enablePTPIP();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }
        sessionPrepared = true;
        session = ICatchCameraSession.createSession();
        boolean retValue = false;
        try {
            retValue = session.prepareSession(itrans);
        } catch (IchTransportException e) {
            AppLog.d(TAG, "IchTransportException");
            e.printStackTrace();
        }
        sessionPrepared = retValue;
        AppLog.e(TAG, "preparePanoramaSession =" + sessionPrepared);
        return retValue;
    }

    public boolean prepareSession(ICatchITransport itrans,boolean enablePTPIP) {
        // TODO Auto-generated constructor stub
        AppLog.d(TAG, "start prepareSession itrans=" + itrans + " enablePTPIP=" +enablePTPIP);
        if (enablePTPIP) {
            try {
                ICatchCameraSession.getCameraConfig(itrans).enablePTPIP();
            } catch (IchInvalidArgumentException e) {
                AppLog.e(TAG, "enablePTPIP IchInvalidArgumentException");
                e.printStackTrace();
            }
        } else {
            try {
                ICatchCameraSession.getCameraConfig(itrans).disablePTPIP();
            } catch (IchInvalidArgumentException e) {
                AppLog.e(TAG, "disablePTPIP IchInvalidArgumentException");
                e.printStackTrace();
            }
        }

        sessionPrepared = true;
        AppLog.d(TAG, "start createSession");
        session = ICatchCameraSession.createSession();

        boolean retValue = false;
        try {
            retValue = session.prepareSession(itrans);
        } catch (IchTransportException e) {
            e.printStackTrace();
        }
        if (!retValue) {
            AppLog.e(TAG, "failed to preparePanoramaSession");
            sessionPrepared = false;
        }
        AppLog.d(TAG, "end preparePanoramaSession ret=" + sessionPrepared);
        return sessionPrepared;
    }

    public ICatchCameraSession getSDKSession() {
        AppLog.d(TAG, "getSDKSession =" + session);
        return session;
    }

    public boolean checkWifiConnection() {
        AppLog.i(TAG, "Start checkWifiConnection");
        boolean retValue = false;

        try {
            retValue = session.checkConnection();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }

        AppLog.i(TAG, "End checkWifiConnection,retValue=" + retValue);
        return retValue;
    }

    public boolean destroySession() {
        AppLog.i(TAG, "Start destroyPanoramaSession");
        Boolean retValue = false;
        try {
            retValue = session.destroySession();
            AppLog.i(TAG, "End  destroyPanoramaSession,retValue=" + retValue);
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retValue;
    }

    public static boolean startDeviceScan() {
        AppLog.i(TAG, "Start startDeviceScan");
        boolean tempStartDeviceScanValue = false;
//        boolean tempStartDeviceScanValue = ICatchCameraSession.startDeviceScan();

        AppLog.i(TAG, "End startDeviceScan,tempStartDeviceScanValue=" + tempStartDeviceScanValue);
        if (tempStartDeviceScanValue) {
            scanFlag = 1;
        }
        return tempStartDeviceScanValue;
    }

    public static void stopDeviceScan() {
        AppLog.i(TAG, "Start stopDeviceScan");
        boolean tempStopDeviceScanValue = false;
        if (scanFlag == 1) {
//            tempStopDeviceScanValue = ICatchCameraSession.stopDeviceScan();
        } else {
            tempStopDeviceScanValue = true;
        }
        scanFlag = 0;
        AppLog.i(TAG, "End stopDeviceScan,tempStopDeviceScanValue=" + tempStopDeviceScanValue);
    }
}
