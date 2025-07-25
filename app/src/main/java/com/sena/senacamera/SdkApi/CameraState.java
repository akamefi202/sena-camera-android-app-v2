/**
 * Added by zhangyanhu C01012,2014-7-2
 */
package com.sena.senacamera.SdkApi;


import com.sena.senacamera.log.AppLog;
import com.icatchtek.control.customer.ICatchCameraState;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;

/**
 * Added by zhangyanhu C01012,2014-7-2
 */
public class CameraState {
    private static final String TAG = CameraState.class.getSimpleName();
    private ICatchCameraState cameraState;

    public CameraState(ICatchCameraState cameraState) {
        this.cameraState = cameraState;
    }

    public boolean isMovieRecording() {
        AppLog.i(TAG, "begin isMovieRecording");
        boolean retValue = false;
        try {
            retValue = cameraState.isMovieRecording();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end isMovieRecording retValue=" + retValue);
        return retValue;
    }

    public boolean isTimeLapseVideoOn() {
        AppLog.i(TAG, "begin isTimeLapseVideoOn");
        boolean retValue = false;
        try {
            retValue = cameraState.isTimeLapseVideoOn();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end isTimeLapseVideoOn retValue=" + retValue);
        return retValue;
    }

    public boolean isTimeLapseStillOn() {
        AppLog.i(TAG, "begin isTimeLapseStillOn");
        boolean retValue = false;
        try {
            retValue = cameraState.isTimeLapseStillOn();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end isTimeLapseStillOn retValue=" + retValue);
        return retValue;
    }

    public boolean isSupportImageAutoDownload() {
        AppLog.i(TAG, "begin isSupportImageAutoDownload");
        boolean retValue = false;
        try {
            retValue = cameraState.supportImageAutoDownload();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end isSupportImageAutoDownload = " + retValue);
        return retValue;
    }
}
