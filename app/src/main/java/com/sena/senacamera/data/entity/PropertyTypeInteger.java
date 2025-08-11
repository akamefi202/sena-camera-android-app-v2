package com.sena.senacamera.data.entity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.sena.senacamera.data.type.AutoLowLight;
import com.sena.senacamera.data.type.BatteryStatusLed;
import com.sena.senacamera.data.type.FrontDisplay;
import com.sena.senacamera.data.type.MainStatusLed;
import com.sena.senacamera.data.type.VideoEis;
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
import java.util.Arrays;
import java.util.HashMap;
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
            case PropertyId.PHOTO_SELF_TIMER:
                valueListInt = cameraProperties.getSupportedCaptureDelays();
                break;
            case PropertyId.PHOTO_BURST:
                valueListInt = cameraProperties.getSupportedBurstNums();
                break;
            case PropertyId.GENERAL_COLOR_EFFECT:
                valueListInt = cameraProperties.getSupportedColorEffects();
                break;
            case PropertyId.LIGHT_FREQUENCY:
                valueListInt = cameraProperties.getSupportedLightFrequencies();
                break;
            case PropertyId.DATE_CAPTION:
                valueListInt = cameraProperties.getSupportedDateCaption();
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
            case PropertyId.GENERAL_FRONT_DISPLAY:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(FrontDisplay.FRONT_DISPLAY_OFF);
                valueListInt.add(FrontDisplay.FRONT_DISPLAY_ON);
                break;
            case PropertyId.GENERAL_MAIN_STATUS_LED:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(MainStatusLed.MAIN_STATUS_LED_OFF);
                valueListInt.add(MainStatusLed.MAIN_STATUS_LED_ON);
                break;
            case PropertyId.GENERAL_BATTERY_STATUS_LED:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(BatteryStatusLed.BATTERY_STATUS_LED_OFF);
                valueListInt.add(BatteryStatusLed.BATTERY_STATUS_LED_ON);
                break;
            case PropertyId.VIDEO_AUTO_LOW_LIGHT:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(AutoLowLight.AUTO_LOW_LIGHT_OFF);
                valueListInt.add(AutoLowLight.AUTO_LOW_LIGHT_ON);
                break;
            case PropertyId.VIDEO_EIS:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(VideoEis.VIDEO_EIS_OFF);
                valueListInt.add(VideoEis.VIDEO_EIS_ON);
                break;
            case PropertyId.TIMELAPSE_MODE:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(TimeLapseMode.TIME_LAPSE_MODE_STILL);
                valueListInt.add(TimeLapseMode.TIME_LAPSE_MODE_VIDEO);
                break;
            case PropertyId.PHOTO_VIDEO_METERING:
                valueListInt = cameraProperties.getSupportedMetering();
                break;
            case PropertyId.PHOTO_VIDEO_ISO:
                valueListInt = cameraProperties.getSupportedIso();
                break;
            case PropertyId.PHOTO_VIDEO_QUALITY:
                valueListInt = cameraProperties.getSupportedQuality();
                break;
            case PropertyId.PHOTO_VIDEO_TIMELAPSE_INTERVAL:
                valueListInt = cameraProperties.getSupportedTimeLapseIntervals();
                break;
            case PropertyId.PHOTO_VIDEO_TIMELAPSE_DURATION:
                valueListInt = cameraProperties.getSupportedTimeLapseDurations();
                break;
            default:
                valueListInt = cameraProperties.getSupportedPropertyValues(propertyId);
                break;
        }

        valueListString = new String[valueListInt.size()];
        Arrays.fill(valueListString, "");
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
            case PropertyId.PHOTO_SELF_TIMER:
                // capture delay has same function with self timer
                retValue = cameraProperties.getCurrentCaptureDelay();
                break;
            case PropertyId.PHOTO_BURST:
                retValue = cameraProperties.getCurrentBurstNumber();
                break;
            case PropertyId.GENERAL_COLOR_EFFECT:
                retValue = cameraProperties.getCurrentColorEffect();
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.getCurrentLightFrequency();
                break;
            case PropertyId.DATE_CAPTION:
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
            case PropertyId.PHOTO_VIDEO_TIMELAPSE_INTERVAL:
                retValue = cameraProperties.getCurrentTimeLapseInterval();
                break;
            case PropertyId.PHOTO_VIDEO_TIMELAPSE_DURATION:
                retValue = cameraProperties.getCurrentTimelapseDuration();
                break;
            case PropertyId.PHOTO_VIDEO_METERING:
                retValue = cameraProperties.getCurrentMetering();
                break;
            case PropertyId.PHOTO_VIDEO_ISO:
                retValue = cameraProperties.getCurrentIso();
                break;
            case PropertyId.PHOTO_VIDEO_QUALITY:
                retValue = cameraProperties.getCurrentQuality();
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
            case PropertyId.PHOTO_SELF_TIMER:
                retValue = cameraProperties.setCaptureDelay(value);
                break;
            case PropertyId.PHOTO_BURST:
                retValue = cameraProperties.setBurstNumber(value);
                break;
            case PropertyId.GENERAL_COLOR_EFFECT:
                retValue = cameraProperties.setColorEffect(value);
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.setLightFrequency(value);
                break;
            case PropertyId.DATE_CAPTION:
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
            case PropertyId.PHOTO_SELF_TIMER:
                retValue = cameraProperties.setCaptureDelay(valueListInt.get(position));
                break;
            case PropertyId.PHOTO_BURST:
                retValue = cameraProperties.setBurstNumber(valueListInt.get(position));
                break;
            case PropertyId.GENERAL_COLOR_EFFECT:
                retValue = cameraProperties.setColorEffect(valueListInt.get(position));
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = cameraProperties.setLightFrequency(valueListInt.get(position));
                break;
            case PropertyId.DATE_CAPTION:
                retValue = cameraProperties.setDateStamp(valueListInt.get(position));
                break;
            case PropertyId.UP_SIDE:
                retValue = cameraProperties.setUpsideDown(valueListInt.get(position));
                break;
            case PropertyId.SLOW_MOTION:
                retValue = cameraProperties.setSlowMotion(valueListInt.get(position));
                break;
            case PropertyId.PHOTO_VIDEO_TIMELAPSE_INTERVAL:
                retValue = cameraProperties.setTimeLapseInterval(valueListInt.get(position));
                break;
            case PropertyId.PHOTO_VIDEO_TIMELAPSE_DURATION:
                retValue = cameraProperties.setTimeLapseDuration(valueListInt.get(position));
                break;
            case PropertyId.PHOTO_VIDEO_QUALITY:
                retValue = cameraProperties.setQuality(valueListInt.get(position));
                break;
            case PropertyId.PHOTO_VIDEO_METERING:
                retValue = cameraProperties.setMetering(valueListInt.get(position));
                break;
            case PropertyId.PHOTO_VIDEO_ISO:
                retValue = cameraProperties.setIso(valueListInt.get(position));
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
            case PropertyId.PHOTO_SELF_TIMER:
                if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE) &&
                        cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY) && //IC-564
                        previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    retValue = true;
                    break;
                }
                break;
            case PropertyId.PHOTO_BURST:
                if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER) &&
                        previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    retValue = true;
                    break;
                }
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = true;
                break;
            case PropertyId.DATE_CAPTION:
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
