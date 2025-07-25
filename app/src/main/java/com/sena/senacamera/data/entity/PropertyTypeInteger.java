package com.sena.senacamera.data.entity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.data.Hash.PropertyHashMapDynamic;
import com.sena.senacamera.data.Mode.PreviewMode;
import com.sena.senacamera.data.PropertyId.PropertyId;
import com.sena.senacamera.data.type.SlowMotion;
import com.sena.senacamera.data.type.TimeLapseMode;
import com.sena.senacamera.data.type.Upside;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PropertyTypeInteger {
    private static final String TAG = PropertyTypeInteger.class.getSimpleName();

    private HashMap<Integer, ItemInfo> hashMap;
    private int propertyId;
    private String[] valueListString;
    private List<Integer> valueListInt;
    private Context context;
    private Resources res;
    private CameraProperties cameraProperties;

    public PropertyTypeInteger(CameraProperties cameraProperties, HashMap<Integer, ItemInfo> hashMap, int propertyId, Context context) {
        this.hashMap = hashMap;
        this.propertyId = propertyId;
        this.context = context;
        this.cameraProperties = cameraProperties;
        initItem();
    }

    public PropertyTypeInteger(CameraProperties cameraProperties, int propertyId, Context context) {
        this.propertyId = propertyId;
        this.context = context;
        this.cameraProperties = cameraProperties;
        initItem();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
            hashMap = PropertyHashMapDynamic.getInstance().getDynamicHashInt(cameraProperties, propertyId);
        }
        res = context.getResources();

        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                valueListInt = cameraProperties.getSupportedWhiteBalances();
                break;
            case PropertyId.CAPTURE_DELAY:
                valueListInt = cameraProperties.getSupportedCaptureDelays();
                break;
            case PropertyId.BURST_NUMBER:
                valueListInt = new LinkedList<>();
                for (int key: cameraProperties.getSupportedBurstNums()) {
                    if (hashMap != null && hashMap.containsKey(key)) {
                        valueListInt.add(key);
                    } else {
                        AppLog.d(TAG,"Contains unsupported values BurstNums key:" + key);
                    }
                }
                break;
            case PropertyId.GENERAL_COLOR_EFFECT:
                valueListInt = new LinkedList<>();
                for (int key: cameraProperties.getSupportedColorEffects()) {
                    if (hashMap != null && hashMap.containsKey(key)) {
                        valueListInt.add(key);
                    } else {
                        AppLog.d(TAG,"Contains unsupported values ColorEffects key: " + key);
                    }
                }
                break;
            case PropertyId.LIGHT_FREQUENCY:
                valueListInt = cameraProperties.getSupportedLightFrequencies();
                break;
            case PropertyId.DATE_STAMP:
                valueListInt = cameraProperties.getSupportedDateStamps();
                break;
            case PropertyId.UP_SIDE:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(Upside.UPSIDE_OFF);
                valueListInt.add(Upside.UPSIDE_ON);
                break;
            case PropertyId.SLOW_MOTION:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(SlowMotion.SLOW_MOTION_OFF);
                valueListInt.add(SlowMotion.SLOW_MOTION_ON);
                break;
            case PropertyId.TIMELAPSE_MODE:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(TimeLapseMode.TIME_LAPSE_MODE_STILL);
                valueListInt.add(TimeLapseMode.TIME_LAPSE_MODE_VIDEO);
                break;
            case PropertyId.AP_MODE_TO_STA_MODE:
                valueListInt = new LinkedList<>();
                for (int key: cameraProperties.getSupportedBurstNums()) {
                    if (hashMap != null && hashMap.containsKey(key)) {
                        valueListInt.add(key);
                    } else {
                        AppLog.d(TAG,"Contains unsupported values BurstNums key:" + key);
                    }
                }
            default:
                valueListInt = cameraProperties.getSupportedPropertyValues(propertyId);
                break;
        }

        valueListString = new String[valueListInt.size()];
        if (valueListInt != null) {
            for (int i = 0; i < valueListInt.size(); i++) {
                if (hashMap.get(valueListInt.get(i)) == null) {
                    continue;
                }
                String uiStringInSettingString = hashMap.get(valueListInt.get(i)).uiStringInSettingString;
                if (uiStringInSettingString != null) {
                    valueListString[i] = uiStringInSettingString;
                } else {
                    valueListString[i] = res.getString(hashMap.get(valueListInt.get(i)).uiStringInSetting);
                }
            }
        }
    }

    public int getCurrentValue() {
        // TODO Auto-generated method stub
        int retValue;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = cameraProperties.getCurrentWhiteBalance();
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = cameraProperties.getCurrentCaptureDelay();
                break;
            case PropertyId.BURST_NUMBER:
                retValue = cameraProperties.getCurrentBurstNum();
                break;
            case PropertyId.GENERAL_COLOR_EFFECT:
                retValue = cameraProperties.getCurrentColorEffect();
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.getCurrentLightFrequency();
                break;
            case PropertyId.DATE_STAMP:
                retValue = cameraProperties.getCurrentDateStamp();
                break;
            case PropertyId.UP_SIDE:
                retValue = cameraProperties.getCurrentUpsideDown();
                break;
            case PropertyId.SLOW_MOTION:
                retValue = cameraProperties.getCurrentSlowMotion();
                break;
            case PropertyId.TIMELAPSE_MODE:
                retValue = CameraManager.getInstance().getCurCamera().timeLapsePreviewMode;
                break;
            default:
                retValue = cameraProperties.getCurrentPropertyValue(propertyId);
                break;
        }
        return retValue;
    }

    public String getCurrentUiStringInSetting() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
            return "Unknown";
        }
        int curValue = getCurrentValue();
        Log.e("getCurrentUiStringInSetting", "propertyId: " + propertyId);
        Log.e("getCurrentUiStringInSetting", "curValue: " + curValue);
        ItemInfo itemInfo = hashMap.get(curValue);
        //Log.e("getCurrentUiStringInSetting", "itemInfo.uiStringInSetting: " + itemInfo.uiStringInSetting);
        String ret = null;
        if (itemInfo == null) {
            ret = "Unknown";
        } else if (itemInfo.uiStringInSetting != 0) {
            ret = res.getString(itemInfo.uiStringInSetting);
        } else if (itemInfo.uiStringInSettingString != null) {
            ret = itemInfo.uiStringInSettingString;
        } else {
            ret = "";
        }
        return ret;
    }

    public String getCurrentUiStringInPreview() {
        if (hashMap == null) {
            return "Unknown";
        }
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = itemInfo.uiStringInPreview;
        }
        // TODO Auto-generated method stub
        return ret;
    }

    public String getCurrentUiStringInSetting(int position) {
        // TODO Auto-generated method stub
        return valueListString[position];
    }

    public int getCurrentIcon() {
        // TODO Auto-generated method stub
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        AppLog.d(TAG, "itemInfo=" + itemInfo);
        if (itemInfo == null) {
            return -1;
        }
        return itemInfo.iconID;
    }

    public String[] getValueList() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public Boolean setValue(int value) {
        // TODO Auto-generated method stub
        boolean retValue;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = cameraProperties.setWhiteBalance(value);
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = cameraProperties.setCaptureDelay(value);
                break;
            case PropertyId.BURST_NUMBER:
                retValue = cameraProperties.setCurrentBurst(value);
                break;
            case PropertyId.GENERAL_COLOR_EFFECT:
                retValue = cameraProperties.setColorEffect(value);
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.setLightFrequency(value);
                break;
            case PropertyId.DATE_STAMP:
                retValue = cameraProperties.setDateStamp(value);
                break;
            default:
                retValue = cameraProperties.setPropertyValue(propertyId, value);
                break;
        }
        return retValue;
    }

    public Boolean setValueByPosition(int position) {
        // TODO Auto-generated method stub

        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = cameraProperties.setWhiteBalance(valueListInt.get(position));
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = cameraProperties.setCaptureDelay(valueListInt.get(position));
                break;
            case PropertyId.BURST_NUMBER:
                retValue = cameraProperties.setCurrentBurst(valueListInt.get(position));
                break;
            case PropertyId.GENERAL_COLOR_EFFECT:
                retValue = cameraProperties.setColorEffect(valueListInt.get(position));
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.setLightFrequency(valueListInt.get(position));
                break;
            case PropertyId.DATE_STAMP:
                retValue = cameraProperties.setDateStamp(valueListInt.get(position));
                break;
            case PropertyId.UP_SIDE:
                retValue = cameraProperties.setUpsideDown(valueListInt.get(position));
                break;
            case PropertyId.SLOW_MOTION:
                retValue = cameraProperties.setSlowMotion(valueListInt.get(position));
                break;
            default:
                retValue = cameraProperties.setPropertyValue(propertyId, valueListInt.get(position));
                break;
        }
        return retValue;
    }

    public Boolean needDisplayByMode(int previewMode) {
        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                //retValue = cameraProperties.setWhiteBalance(valueListInt.get(position));
                if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
                    retValue = true;
                    break;
                }
                retValue = true;
                break;
            case PropertyId.CAPTURE_DELAY:
                if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE) &&
                        cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY) && //IC-564
                        previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    retValue = true;
                    break;
                }
                break;
            case PropertyId.BURST_NUMBER:
                if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER) &&
                        previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    retValue = true;
                    break;
                }
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = true;
                break;
            case PropertyId.DATE_STAMP:
                if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP)) {
                    if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW || previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                        retValue = true;
                        break;
                    }
                }
                break;
            case PropertyId.UP_SIDE:
                if (cameraProperties.hasFunction(PropertyId.UP_SIDE)) {
                    return true;
                }
                break;
            case PropertyId.SLOW_MOTION:
                if (cameraProperties.hasFunction(PropertyId.SLOW_MOTION) &&
                        (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                                previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE)) {
                    retValue = true;
                    break;
                }
                break;

            case PropertyId.TIMELAPSE_MODE:
                boolean supportTimelapseMode  = cameraProperties.hasFunction(PropertyId.TIMELAPSE_MODE);
                AppLog.i(TAG, "TIMELAPSE_MODE isSupportTimelapseMode=" + supportTimelapseMode);
                if (supportTimelapseMode) {
                    if (previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        retValue = true;
                        break;
                    }
                    break;
                }
                break;
            default:
                break;
        }
        return retValue;
    }

}
