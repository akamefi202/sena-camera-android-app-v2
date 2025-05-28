package com.sena.senacamera.Function;

import com.sena.senacamera.Log.AppLog;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.data.GlobalApp.GlobalInfo;
import com.sena.senacamera.data.Hash.PropertyHashMapStatic;
import com.sena.senacamera.data.PropertyId.PropertyId;
import com.sena.senacamera.data.entity.PropertyTypeInteger;
import com.sena.senacamera.data.entity.PropertyTypeString;
import com.sena.senacamera.data.type.TimeLapseDuration;
import com.sena.senacamera.data.type.TimeLapseInterval;
import com.icatchtek.control.customer.type.ICatchCamProperty;

/**
 * Created by b.jiang on 2017/9/15.
 */

public class BaseProperties {
    private String TAG = BaseProperties.class.getSimpleName();
    private CameraProperties cameraProperty;
    private PropertyTypeInteger whiteBalance;
    private PropertyTypeInteger burst;
    private PropertyTypeInteger electricityFrequency;
    private PropertyTypeInteger dateStamp;
    private PropertyTypeInteger slowMotion;
    private PropertyTypeInteger upside;
    private PropertyTypeInteger captureDelay;
    private PropertyTypeString videoSize;
    private PropertyTypeString imageSize;
    //private TimeLapseInterval timeLapseInterval;
    private TimeLapseInterval timeLapseVideoInterval;
    private TimeLapseInterval timeLapseStillInterval;
    private TimeLapseDuration timeLapseDuration;
    private PropertyTypeInteger timeLapseMode;
    private PropertyTypeInteger exposureCompensation;
    private PropertyTypeInteger videoFileLength;
    private PropertyTypeInteger cameraSwitch;
    private PropertyTypeInteger screenSaver;
    private PropertyTypeInteger autoPowerOff;
    private PropertyTypeInteger fastMotionMovie;

    // akamefi202: updated settings
    private PropertyTypeInteger photoMode;
    private PropertyTypeInteger photoVideoImageQuality;
    private PropertyTypeInteger photoIso;
    private PropertyTypeInteger photoVideoEv;
    private PropertyTypeInteger photoVideoMetering;
    private PropertyTypeInteger photoVideoTimelapseDuration;
    private PropertyTypeInteger videoMode;
    private PropertyTypeInteger videoAutoLowLight;
    private PropertyTypeInteger videoFov;
    private PropertyTypeInteger generalAudioInput;
    private PropertyTypeInteger generalDisplayBrightness;
    private PropertyTypeInteger generalDeviceSound;
    private PropertyTypeInteger generalFontDisplay;
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
        burst = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.burstMap, ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER, GlobalInfo
                .getInstance().getAppContext());
        dateStamp = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.dateStampMap, PropertyId.DATE_STAMP, GlobalInfo.getInstance()
                .getAppContext());
        slowMotion = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.slowMotionMap, PropertyId.SLOW_MOTION, GlobalInfo.getInstance()
                .getAppContext());
        upside = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.upsideMap, PropertyId.UP_SIDE, GlobalInfo.getInstance()
                .getAppContext());

        electricityFrequency = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.electricityFrequencyMap, PropertyId.LIGHT_FREQUENCY,
                GlobalInfo.getInstance().getAppContext());

        captureDelay = new PropertyTypeInteger(cameraProperty, PropertyId.CAPTURE_DELAY, GlobalInfo.getInstance().getAppContext());
        videoSize = new PropertyTypeString(cameraProperty, PropertyId.VIDEO_SIZE, GlobalInfo.getInstance().getAppContext());
        imageSize = new PropertyTypeString(cameraProperty, PropertyId.IMAGE_SIZE, GlobalInfo.getInstance().getAppContext());
        //timeLapseInterval = new TimeLapseInterval(cameraProperty);
        timeLapseVideoInterval = new TimeLapseInterval(cameraProperty);
        timeLapseStillInterval = new TimeLapseInterval(cameraProperty);
        timeLapseDuration = new TimeLapseDuration(cameraProperty);
        timeLapseMode = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.timeLapseMode, PropertyId.TIMELAPSE_MODE, GlobalInfo.getInstance()
                .getAppContext());
        exposureCompensation = new PropertyTypeInteger(cameraProperty,PropertyId.EXPOSURE_COMPENSATION, GlobalInfo.getInstance().getAppContext());
        videoFileLength = new PropertyTypeInteger(cameraProperty,PropertyId.VIDEO_FILE_LENGTH, GlobalInfo.getInstance().getAppContext());
//        cameraSwitch = new PropertyTypeInteger(cameraProperty,PropertyHashMapStatic.cameraSwitchMap, PropertyId.CAMERA_SWITCH, GlobalInfo.getInstance().getAppContext());

        screenSaver = new PropertyTypeInteger(cameraProperty,PropertyId.SCREEN_SAVER, GlobalInfo.getInstance().getAppContext());
        autoPowerOff = new PropertyTypeInteger(cameraProperty,PropertyId.AUTO_POWER_OFF, GlobalInfo.getInstance().getAppContext());
        fastMotionMovie = new PropertyTypeInteger(cameraProperty,PropertyId.FAST_MOTION_MOVIE, GlobalInfo.getInstance().getAppContext());

        // akamefi202: updated settings
        photoMode = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.photoModeMap, PropertyId.PHOTO_MODE, GlobalInfo.getInstance().getAppContext());
        photoVideoImageQuality = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.imageQualityMap, PropertyId.PHOTO_VIDEO_IMAGE_QUALITY, GlobalInfo.getInstance().getAppContext());
        photoVideoMetering = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.photoVideoMeteringMap, PropertyId.PHOTO_VIDEO_METERING, GlobalInfo.getInstance().getAppContext());
        videoFov = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.videoFovAngleMap, PropertyId.VIDEO_FOV, GlobalInfo.getInstance().getAppContext());
        generalDeviceSound = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.generalDeviceSoundMap, PropertyId.GENERAL_DEVICE_SOUND, GlobalInfo.getInstance().getAppContext());
        generalLanguage = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.generalLanguageMap, PropertyId.GENERAL_LANGUAGE, GlobalInfo.getInstance().getAppContext());
        generalColorEffect = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.generalColorEffectMap, PropertyId.GENERAL_COLOR_EFFECT, GlobalInfo.getInstance().getAppContext());
        generalDateFormat = new PropertyTypeInteger(cameraProperty, PropertyHashMapStatic.generalDateFormatMap, PropertyId.GENERAL_DATE_FORMAT, GlobalInfo.getInstance().getAppContext());

        photoIso = new PropertyTypeInteger(cameraProperty, PropertyId.PHOTO_ISO, GlobalInfo.getInstance().getAppContext());
        photoVideoEv = new PropertyTypeInteger(cameraProperty, PropertyId.PHOTO_VIDEO_EV, GlobalInfo.getInstance().getAppContext());
        photoVideoTimelapseDuration = new PropertyTypeInteger(cameraProperty, PropertyId.PHOTO_VIDEO_TIMELAPSE_DURATION, GlobalInfo.getInstance().getAppContext());
        videoAutoLowLight = new PropertyTypeInteger(cameraProperty, PropertyId.VIDEO_AUTO_LOW_LIGHT, GlobalInfo.getInstance().getAppContext());
        generalAudioInput = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_AUDIO_INPUT, GlobalInfo.getInstance().getAppContext());
        generalDisplayBrightness = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_DISPLAY_BRIGHTNESS, GlobalInfo.getInstance().getAppContext());
        generalFontDisplay = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_FONT_DISPLAY, GlobalInfo.getInstance().getAppContext());
        generalMainStatusLed = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_MAIN_STATUS_LED, GlobalInfo.getInstance().getAppContext());
        generalBatteryStatusLed = new PropertyTypeInteger(cameraProperty, PropertyId.GENERAL_BATTERY_STATUS_LED, GlobalInfo.getInstance().getAppContext());
        AppLog.i(TAG, "End initProperty");
    }

    public PropertyTypeInteger getScreenSaver() {
        return screenSaver;
    }

    public PropertyTypeInteger getAutoPowerOff() {
        return autoPowerOff;
    }

    public PropertyTypeInteger getCameraSwitch() { return cameraSwitch;  }


    public PropertyTypeInteger getExposureCompensation() {
        return exposureCompensation;
    }

    public PropertyTypeInteger getVideoFileLength() {
        return videoFileLength;
    }

    public PropertyTypeInteger getFastMotionMovie() {
        return fastMotionMovie;
    }

    public PropertyTypeInteger getWhiteBalance() {
        return whiteBalance;
    }

    public PropertyTypeInteger getBurst() {
        return burst;
    }

    public PropertyTypeInteger getDateStamp() {
        return dateStamp;
    }

    public PropertyTypeInteger getCaptureDelay() {
        return captureDelay;
    }

    public PropertyTypeInteger getSlowMotion() {
        return slowMotion;
    }

    public PropertyTypeInteger getUpside() {
        return upside;
    }

    public PropertyTypeString getVideoSize() {
        return videoSize;
    }

    public PropertyTypeString getImageSize() {
        return imageSize;
    }

    public PropertyTypeInteger getElectricityFrequency() {
        return electricityFrequency;
    }

    public TimeLapseInterval getTimeLapseVideoInterval() {
        return timeLapseVideoInterval;
    }

    public TimeLapseInterval getTimeLapseStillInterval() {
        return timeLapseStillInterval;
    }

//    public TimeLapseInterval getTimeLapseInterval() {
//        return timeLapseInterval;
//    }

    public TimeLapseDuration getTimeLapseDuration() {
        return timeLapseDuration;
    }

    public PropertyTypeInteger getTimeLapseMode() {
        return timeLapseMode;
    }

    public PropertyTypeInteger getPhotoMode() {
        return photoMode;
    }

    public PropertyTypeInteger getPhotoVideoImageQuality() {
        return photoVideoImageQuality;
    }

    public PropertyTypeInteger getPhotoIso() {
        return photoIso;
    }

    public PropertyTypeInteger getPhotoVideoEv() {
        return photoVideoEv;
    }

    public PropertyTypeInteger getPhotoVideoMetering() {
        return photoVideoMetering;
    }

    public PropertyTypeInteger getPhotoVideoTimelapseDuration() {
        return photoVideoTimelapseDuration;
    }

    public PropertyTypeInteger getVideoMode() {
        return videoMode;
    }

    public PropertyTypeInteger getVideoAutoLowLight() {
        return videoAutoLowLight;
    }

    public PropertyTypeInteger getVideoFov() {
        return videoFov;
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

    public PropertyTypeInteger getGeneralFontDisplay() {
        return generalFontDisplay;
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
