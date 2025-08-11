package com.sena.senacamera.function;

import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.data.GlobalApp.GlobalInfo;
import com.sena.senacamera.data.Hash.PropertyHashMapStatic;
import com.sena.senacamera.data.PropertyId.PropertyId;
import com.sena.senacamera.data.entity.PropertyTypeInteger;
import com.sena.senacamera.data.entity.PropertyTypeString;

/**
 * Created by b.jiang on 2017/9/15.
 */

public class BaseProperties {
    private static final String TAG = BaseProperties.class.getSimpleName();

    private CameraProperties cameraProperty;
    private PropertyTypeInteger whiteBalance;
    private PropertyTypeInteger photoBurst;
    private PropertyTypeInteger electricityFrequency;
    private PropertyTypeInteger wifiFrequency;
    private PropertyTypeInteger dateCaption;
    private PropertyTypeInteger slowMotion;
    private PropertyTypeInteger upside;
    private PropertyTypeInteger photoSelfTimer;
    private PropertyTypeString videoResolution;
    private PropertyTypeString photoResolution;
    private PropertyTypeInteger timelapseInterval;
    private PropertyTypeInteger timelapseDuration;
    private PropertyTypeInteger timelapseMode;
    private PropertyTypeInteger photoVideoEv;
    private PropertyTypeInteger videoLoopRecording;
    private PropertyTypeInteger cameraSwitch;
    private PropertyTypeInteger screenSaver;
    private PropertyTypeInteger autoPowerOff;
    private PropertyTypeInteger fastMotionMovie;

    // akamefi202: updated settings
    private PropertyTypeInteger photoMode;
    private PropertyTypeInteger photoVideoImageQuality;
    private PropertyTypeInteger photoVideoIso;
    private PropertyTypeInteger photoVideoMetering;
    private PropertyTypeInteger videoMode;
    private PropertyTypeInteger videoAutoLowLight;
    private PropertyTypeInteger videoEis;
    private PropertyTypeInteger photoVideoFov;
    private PropertyTypeInteger generalAudioInput;
    private PropertyTypeInteger generalDisplayBrightness;
    private PropertyTypeInteger generalDeviceSound;
    private PropertyTypeInteger generalFrontDisplay;
    private PropertyTypeInteger generalMainStatusLed;
    private PropertyTypeInteger generalBatteryStatusLed;
    private PropertyTypeInteger generalLanguage;
    private PropertyTypeInteger generalColorEffect;
    private PropertyTypeInteger generalDateFormat;

    public BaseProperties(CameraProperties cameraProperty) {
        this.cameraProperty = cameraProperty;
        PropertyHashMapStatic.getInstance().initPropertyHashMap();
        initProperty();
    }

    private void initProperty() {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "Start initProperty");
        whiteBalance = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.whiteBalanceMap, PropertyId.WHITE_BALANCE, GlobalInfo
                .getInstance().getAppContext());
        photoBurst = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.photoBurstMap, PropertyId.PHOTO_BURST, GlobalInfo
                .getInstance().getAppContext());
        dateCaption = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.dateCaptionMap, PropertyId.DATE_CAPTION, GlobalInfo.getInstance()
                .getAppContext());
        slowMotion = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.slowMotionMap, PropertyId.SLOW_MOTION, GlobalInfo.getInstance()
                .getAppContext());
        upside = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.upsideMap, PropertyId.UP_SIDE, GlobalInfo.getInstance()
                .getAppContext());
        electricityFrequency = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.electricityFrequencyMap, PropertyId.LIGHT_FREQUENCY,
                GlobalInfo.getInstance().getAppContext());
        wifiFrequency = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.wifiFrequencyMap, PropertyId.GENERAL_WIFI_FREQUENCY,
                GlobalInfo.getInstance().getAppContext());

        photoSelfTimer = new PropertyTypeInteger(cameraProperty, PropertyId.PHOTO_SELF_TIMER, GlobalInfo.getInstance().getAppContext());
        videoResolution = new PropertyTypeString(cameraProperty, PropertyId.VIDEO_RESOLUTION, GlobalInfo.getInstance().getAppContext());
        photoResolution = new PropertyTypeString(cameraProperty, PropertyId.PHOTO_RESOLUTION, GlobalInfo.getInstance().getAppContext());
        timelapseInterval = new PropertyTypeInteger(cameraProperty, PropertyId.PHOTO_VIDEO_TIMELAPSE_INTERVAL, GlobalInfo.getInstance().getAppContext());
        timelapseDuration = new PropertyTypeInteger(cameraProperty, PropertyId.PHOTO_VIDEO_TIMELAPSE_DURATION, GlobalInfo.getInstance().getAppContext());
        timelapseMode = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.timelapseMode, PropertyId.TIMELAPSE_MODE, GlobalInfo.getInstance()
                .getAppContext());
        photoVideoEv = new PropertyTypeInteger(cameraProperty, PropertyId.PHOTO_VIDEO_EV, GlobalInfo.getInstance().getAppContext());
        videoLoopRecording = new PropertyTypeInteger(cameraProperty, PropertyId.VIDEO_LOOP_RECORDING, GlobalInfo.getInstance().getAppContext());
//        cameraSwitch = new PropertyTypeInteger(cameraProperty,PropertyHashMapStatic.cameraSwitchMap, PropertyId.CAMERA_SWITCH, GlobalInfo.getInstance().getAppContext());

        screenSaver = new PropertyTypeInteger(cameraProperty, PropertyId.SCREEN_SAVER, GlobalInfo.getInstance().getAppContext());
        autoPowerOff = new PropertyTypeInteger(cameraProperty, PropertyId.AUTO_POWER_OFF, GlobalInfo.getInstance().getAppContext());
        fastMotionMovie = new PropertyTypeInteger(cameraProperty, PropertyId.FAST_MOTION_MOVIE, GlobalInfo.getInstance().getAppContext());

        // akamefi202: updated settings
        photoMode = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.photoModeMap, PropertyId.PHOTO_MODE, GlobalInfo.getInstance().getAppContext());
        photoVideoImageQuality = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.photoVideoQualityMap, PropertyId.PHOTO_VIDEO_QUALITY, GlobalInfo.getInstance().getAppContext());
        photoVideoMetering = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.photoVideoMeteringMap, PropertyId.PHOTO_VIDEO_METERING, GlobalInfo.getInstance().getAppContext());
        photoVideoFov = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.photoVideoFovMap, PropertyId.PHOTO_VIDEO_FOV, GlobalInfo.getInstance().getAppContext());
        generalDeviceSound = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.generalDeviceSoundMap, PropertyId.GENERAL_DEVICE_SOUND, GlobalInfo.getInstance().getAppContext());
        generalLanguage = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.generalLanguageMap, PropertyId.GENERAL_LANGUAGE, GlobalInfo.getInstance().getAppContext());
        generalColorEffect = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.generalColorEffectMap, PropertyId.GENERAL_COLOR_EFFECT, GlobalInfo.getInstance().getAppContext());
        generalDateFormat = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.generalDateFormatMap, PropertyId.GENERAL_DATE_FORMAT, GlobalInfo.getInstance().getAppContext());

        videoMode = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.videoModeMap, PropertyId.VIDEO_MODE, GlobalInfo.getInstance().getAppContext());
        photoVideoIso = new PropertyTypeInteger(cameraProperty, PropertyId.PHOTO_VIDEO_ISO, GlobalInfo.getInstance().getAppContext());
        generalAudioInput = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_AUDIO_INPUT, GlobalInfo.getInstance().getAppContext());
        generalDisplayBrightness = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_DISPLAY_BRIGHTNESS, GlobalInfo.getInstance().getAppContext());
        generalFrontDisplay = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_FRONT_DISPLAY, GlobalInfo.getInstance().getAppContext());
        generalMainStatusLed = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_MAIN_STATUS_LED, GlobalInfo.getInstance().getAppContext());
        generalBatteryStatusLed = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_BATTERY_STATUS_LED, GlobalInfo.getInstance().getAppContext());
        videoEis = new PropertyTypeInteger(cameraProperty, PropertyId.VIDEO_EIS, GlobalInfo.getInstance().getAppContext());
        videoAutoLowLight = new PropertyTypeInteger(cameraProperty, PropertyId.VIDEO_AUTO_LOW_LIGHT, GlobalInfo.getInstance().getAppContext());
        AppLog.i(TAG, "End initProperty");
    }

    public PropertyTypeInteger getScreenSaver() {
        return screenSaver;
    }

    public PropertyTypeInteger getAutoPowerOff() {
        return autoPowerOff;
    }

    public PropertyTypeInteger getCameraSwitch() { return cameraSwitch;  }


    public PropertyTypeInteger getPhotoVideoEv() {
        return photoVideoEv;
    }

    public PropertyTypeInteger getVideoLoopRecording() {
        return videoLoopRecording;
    }

    public PropertyTypeInteger getFastMotionMovie() {
        return fastMotionMovie;
    }

    public PropertyTypeInteger getWhiteBalance() {
        return whiteBalance;
    }

    public PropertyTypeInteger getPhotoBurst() {
        return photoBurst;
    }

    public PropertyTypeInteger getDateCaption() {
        return dateCaption;
    }

    public PropertyTypeInteger getPhotoSelfTimer() {
        return photoSelfTimer;
    }

    public PropertyTypeInteger getSlowMotion() {
        return slowMotion;
    }

    public PropertyTypeInteger getUpside() {
        return upside;
    }

    public PropertyTypeString getVideoResolution() {
        return videoResolution;
    }

    public PropertyTypeString getPhotoResolution() {
        return photoResolution;
    }

    public PropertyTypeInteger getElectricityFrequency() {
        return electricityFrequency;
    }

    public PropertyTypeInteger getWifiFrequency() {
        return wifiFrequency;
    }

    public PropertyTypeInteger getTimelapseInterval() {
        return timelapseInterval;
    }

    public PropertyTypeInteger getTimelapseDuration() {
        return timelapseDuration;
    }

    public PropertyTypeInteger getTimelapseMode() {
        return timelapseMode;
    }

    public PropertyTypeInteger getPhotoMode() {
        return photoMode;
    }

    public PropertyTypeInteger getPhotoVideoQuality() {
        return photoVideoImageQuality;
    }

    public PropertyTypeInteger getPhotoVideoIso() {
        return photoVideoIso;
    }

    public PropertyTypeInteger getPhotoVideoMetering() {
        return photoVideoMetering;
    }

    public PropertyTypeInteger getVideoMode() {
        return videoMode;
    }

    public PropertyTypeInteger getVideoEis() {
        return videoEis;
    }

    public PropertyTypeInteger getVideoAutoLowLight() {
        return videoAutoLowLight;
    }

    public PropertyTypeInteger getPhotoVideoFov() {
        return photoVideoFov;
    }

    public PropertyTypeInteger getGeneralAudioInput() {
        return generalAudioInput;
    }

    public PropertyTypeInteger getGeneralDisplayBrightness() {
        return generalDisplayBrightness;
    }

    public PropertyTypeInteger getGeneralDeviceSound() {
        return generalDeviceSound;
    }

    public PropertyTypeInteger getGeneralFrontDisplay() {
        return generalFrontDisplay;
    }

    public PropertyTypeInteger getGeneralMainStatusLed() {
        return generalMainStatusLed;
    }

    public PropertyTypeInteger getGeneralBatteryStatusLed() {
        return generalBatteryStatusLed;
    }

    public PropertyTypeInteger getGeneralLanguage() {
        return generalLanguage;
    }

    public PropertyTypeInteger getGeneralColorEffect() {
        return generalColorEffect;
    }

    public PropertyTypeInteger getGeneralDateFormat() {
        return generalDateFormat;
    }
}
