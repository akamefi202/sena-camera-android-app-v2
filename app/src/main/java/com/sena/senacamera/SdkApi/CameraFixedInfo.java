/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.sena.senacamera.SdkApi;

import com.sena.senacamera.log.AppLog;
import com.icatchtek.control.customer.ICatchCameraInfo;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;

/**
 * Added by zhangyanhu C01012,2014-6-27
 */
public class CameraFixedInfo {
	private static final String TAG = CameraFixedInfo.class.getSimpleName();
	private ICatchCameraInfo cameraInfo;

	public CameraFixedInfo(ICatchCameraInfo cameraInfo) {
		this.cameraInfo = cameraInfo;
	}

	public String getCameraName() {
		AppLog.i(TAG, "begin getCameraName");
		String name = "";
		try {
			name = cameraInfo.getCameraProductName();
		} catch (IchInvalidSessionException e) {
			e.printStackTrace();
		}
		AppLog.i(TAG, "end getCameraName name =" + name);
		return name;
	}

	public String getCameraVersion() {
		AppLog.i(TAG, "begin getCameraVersion");
		String version = "";
		try {
			version = cameraInfo.getCameraFWVersion();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(TAG, "IchInvalidSessionException");
			e.printStackTrace();
		}
		AppLog.i(TAG, "end getCameraVersion version =" + version);
		return version;
	}

	public String getSdkVersion() {
		AppLog.i(TAG, "begin getSdkVersion");
		String version = "";
		try {
			version = cameraInfo.getSDKVersion();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(TAG, "IchInvalidSessionException");
			e.printStackTrace();
		}
		AppLog.i(TAG, "end getSdkVersion version =" + version);
		return version;
	}
}
