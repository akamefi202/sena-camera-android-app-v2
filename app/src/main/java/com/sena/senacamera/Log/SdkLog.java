package com.sena.senacamera.Log;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.sena.senacamera.data.AppInfo.AppInfo;
import com.sena.senacamera.utils.fileutils.FileOper;
import com.icatchtek.control.customer.ICatchCameraLog;
import com.icatchtek.control.customer.type.ICatchCamLogLevel;
import com.icatchtek.control.customer.type.ICatchCamLogType;
import com.icatchtek.pancam.customer.ICatchPancamLog;
import com.icatchtek.pancam.customer.type.ICatchGLLogLevel;
import com.icatchtek.pancam.customer.type.ICatchGLLogType;
import com.icatchtek.reliant.customer.transport.ICatchUsbTransportLog;

import static android.os.Build.VERSION.SDK;

/**
 * Created by zhang yanhu C001012 on 2015/11/17 17:49.
 */
public class SdkLog {
    private static SdkLog sdkLog;

    public static SdkLog getInstance() {
        if (sdkLog == null) {
            sdkLog = new SdkLog();
        }
        return sdkLog;
    }

    public void enableSDKLog(Context context) {
        String path = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            path = context.getExternalCacheDir().toString() + AppInfo.SDK_LOG_DIRECTORY_PATH;
        }else {
            path = Environment.getExternalStorageDirectory().toString() + AppInfo.SDK_LOG_DIRECTORY_PATH;
        }

        FileOper.createDirectory(path);
        AppLog.d("sdkLog", "start enable sdklog");
       initPancamLog(path);
       initCameraLog(path);
        initUsbLog(path);
        AppLog.d("sdkLog", "end enable sdklog");
    }

    private void initPancamLog(String path){
        ICatchPancamLog.getInstance().setDebugMode(true);
        ICatchPancamLog.getInstance().setFileLogPath(path);
        ICatchPancamLog.getInstance().setSystemLogOutput(true);
        ICatchPancamLog.getInstance().setFileLogOutput(true);
        ICatchPancamLog.getInstance().setLog(ICatchGLLogType.ICH_GL_LOG_TYPE_COMMON, true);
        ICatchPancamLog.getInstance().setLogLevel(ICatchGLLogType.ICH_GL_LOG_TYPE_COMMON, ICatchGLLogLevel.ICH_GL_LOG_LEVEL_INFO);
        ICatchPancamLog.getInstance().setLog(ICatchGLLogType.ICH_GL_LOG_TYPE_DEVELOP, true);
        ICatchPancamLog.getInstance().setLogLevel(ICatchGLLogType.ICH_GL_LOG_TYPE_DEVELOP, ICatchGLLogLevel.ICH_GL_LOG_LEVEL_INFO);
        ICatchPancamLog.getInstance().setLog(ICatchGLLogType.ICH_GL_LOG_TYPE_OPENGL, true);
        ICatchPancamLog.getInstance().setLogLevel(ICatchGLLogType.ICH_GL_LOG_TYPE_OPENGL, ICatchGLLogLevel.ICH_GL_LOG_LEVEL_INFO);
        ICatchPancamLog.getInstance().setLog(ICatchGLLogType.ICH_GL_LOG_TYPE_STREAM, true);
        ICatchPancamLog.getInstance().setLogLevel(ICatchGLLogType.ICH_GL_LOG_TYPE_STREAM, ICatchGLLogLevel.ICH_GL_LOG_LEVEL_INFO);
    }


    private void initCameraLog(String path) {
        ICatchCameraLog.getInstance().setDebugMode(true);
        ICatchCameraLog.getInstance().setFileLogPath(path);
        ICatchCameraLog.getInstance().setFileLogOutput(true);
        ICatchCameraLog.getInstance().setSystemLogOutput(true);
        ICatchCameraLog.getInstance().setLog(ICatchCamLogType.ICH_CAM_LOG_TYPE_COMMON, true);
        ICatchCameraLog.getInstance().setLogLevel(ICatchCamLogType.ICH_CAM_LOG_TYPE_COMMON, ICatchCamLogLevel.ICH_CAM_LOG_LEVEL_INFO);
        ICatchCameraLog.getInstance().setLog(ICatchCamLogType.ICH_CAM_LOG_TYPE_THIRDLIB, true);
        ICatchCameraLog.getInstance().setLogLevel(ICatchCamLogType.ICH_CAM_LOG_TYPE_THIRDLIB, ICatchCamLogLevel.ICH_CAM_LOG_LEVEL_DEBUG);

    }

    private void initUsbLog(String path) {
        ICatchUsbTransportLog.getInstance().setFileLogPath(path);
        ICatchUsbTransportLog.getInstance().setDebugMode(true);
        ICatchUsbTransportLog.getInstance().setFileLogOutput(true);
        ICatchUsbTransportLog.getInstance().setSystemLogOutput(true);
    }
}
