package com.sena.senacamera.data.Hash;

import android.annotation.SuppressLint;

import com.sena.senacamera.data.type.CameraSwitch;
import com.sena.senacamera.data.type.GeneralColorEffect;
import com.sena.senacamera.data.type.GeneralDateFormat;
import com.sena.senacamera.data.type.GeneralDeviceSound;
import com.sena.senacamera.data.type.GeneralLanguage;
import com.sena.senacamera.data.type.ImageQuality;
import com.sena.senacamera.data.type.PhotoMode;
import com.sena.senacamera.data.type.PhotoVideoMetering;
import com.sena.senacamera.data.type.SlowMotion;
import com.sena.senacamera.data.type.TimeLapseDuration;
import com.sena.senacamera.data.type.TimeLapseMode;
import com.sena.senacamera.data.type.Upside;
import com.sena.senacamera.data.entity.ItemInfo;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.R;
import com.sena.senacamera.data.type.VideoFovAngle;
import com.sena.senacamera.data.type.VideoMode;
import com.icatchtek.control.customer.type.ICatchCamBurstNumber;
import com.icatchtek.control.customer.type.ICatchCamDateStamp;
import com.icatchtek.control.customer.type.ICatchCamLightFrequency;
import com.icatchtek.control.customer.type.ICatchCamWhiteBalance;

import java.util.HashMap;

public class PropertyHashMapStatic {
    private static final String TAG = PropertyHashMapStatic.class.getSimpleName();

    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> burstMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> whiteBalanceMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> electricityFrequencyMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> dateStampMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> timeLapseMode = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> timeLapseIntervalMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> timeLapseDurationMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> slowMotionMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> upsideMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> cameraSwitchMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> photoModeMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> imageQualityMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> photoVideoMeteringMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> videoModeMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> videoFovAngleMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> generalDeviceSoundMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> generalLanguageMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> generalColorEffectMap = new HashMap<Integer, ItemInfo>();
    @SuppressLint("UseSparseArrays")
    public static HashMap<Integer, ItemInfo> generalDateFormatMap = new HashMap<Integer, ItemInfo>();

    public static PropertyHashMapStatic propertyHashMap;

    public static PropertyHashMapStatic getInstance() {
        if (propertyHashMap == null) {
            propertyHashMap = new PropertyHashMapStatic();
        }
        return propertyHashMap;
    }

    public void initPropertyHashMap() {
        AppLog.i(TAG, "Start initPropertyHashMap");
        initWhiteBalanceMap();
        initTimeLapseDuration();
        initSlowMotion();
        initUpside();
        initBurstMap();
        initElectricityFrequencyMap();
        initDateStampMap();
        initTimeLapseMode();
        initCameraSwitch();

        initPhotoModeMap();
        initImageQualityMap();
        initPhotoVideoMeteringMap();
        initVideoModeMap();
        initVideoFovAngleMap();
        initGeneralDeviceSoundMap();
        initGeneralLanguageMap();
        initGeneralColorEffectMap();
        initGeneralDateFormatMap();
        AppLog.i(TAG, "End initPropertyHashMap");
    }

    private void initCameraSwitch() {
        // TODO Auto-generated method stub
        cameraSwitchMap.put(CameraSwitch.CAMERA_FRONT, new ItemInfo(R.string.setting_camera_front, null, 0));
        cameraSwitchMap.put(CameraSwitch.CAMERA_BACK, new ItemInfo(R.string.setting_camera_back, null, 0));
    }

    private void initTimeLapseMode() {
        timeLapseMode.put(TimeLapseMode.TIME_LAPSE_MODE_STILL, new ItemInfo(R.string.timeLapse_capture_mode, null, 0));
        timeLapseMode.put(TimeLapseMode.TIME_LAPSE_MODE_VIDEO, new ItemInfo(R.string.timeLapse_video_mode, null, 0));
        // TODO Auto-generated method stub

    }

    public void initWhiteBalanceMap() {
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_AUTO, new ItemInfo(R.string.wb_auto, null, R.drawable.awb_auto));
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_CLOUDY, new ItemInfo(R.string.wb_cloudy, null, R.drawable.awb_cloudy));
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_DAYLIGHT, new ItemInfo(R.string.wb_daylight, null, R.drawable.awb_daylight));
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_FLUORESCENT, new ItemInfo(R.string.wb_fluorescent, null, R.drawable.awb_fluoresecent));
        whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_TUNGSTEN, new ItemInfo(R.string.wb_incandescent, null, R.drawable.awb_incadescent)); //
        // whiteBalanceMap.put(ICatchCamWhiteBalance.ICH_CAM_WB_UNDEFINED,
    }

    private void initTimeLapseDuration() {
        // TODO Auto-generated method stub
        timeLapseIntervalMap.put(TimeLapseDuration.TIME_LAPSE_DURATION_2MIN, new ItemInfo(R.string.setting_time_lapse_duration_2M, null, 0));
        timeLapseIntervalMap.put(TimeLapseDuration.TIME_LAPSE_DURATION_5MIN, new ItemInfo(R.string.setting_time_lapse_duration_5M, null, 0));
        timeLapseIntervalMap.put(TimeLapseDuration.TIME_LAPSE_DURATION_10MIN, new ItemInfo(R.string.setting_time_lapse_duration_10M, null, 0));
        timeLapseIntervalMap.put(TimeLapseDuration.TIME_LAPSE_DURATION_15MIN, new ItemInfo(R.string.setting_time_lapse_duration_15M, null, 0));
        timeLapseIntervalMap.put(TimeLapseDuration.TIME_LAPSE_DURATION_20MIN, new ItemInfo(R.string.setting_time_lapse_duration_20M, null, 0));
        timeLapseIntervalMap.put(TimeLapseDuration.TIME_LAPSE_DURATION_30MIN, new ItemInfo(R.string.setting_time_lapse_duration_30M, null, 0));
        timeLapseIntervalMap.put(TimeLapseDuration.TIME_LAPSE_DURATION_60MIN, new ItemInfo(R.string.setting_time_lapse_duration_60M, null, 0));
        timeLapseIntervalMap.put(TimeLapseDuration.TIME_LAPSE_DURATION_UNLIMITED, new ItemInfo(R.string.setting_time_lapse_duration_unlimit, null, 0));
    }

    private void initSlowMotion() {
        // TODO Auto-generated method stub
        slowMotionMap.put(SlowMotion.SLOW_MOTION_OFF, new ItemInfo(R.string.setting_off, null, 0));
        slowMotionMap.put(SlowMotion.SLOW_MOTION_ON, new ItemInfo(R.string.setting_on, null, 0));
    }

    private void initUpside() {
        // TODO Auto-generated method stub
        upsideMap.put(Upside.UPSIDE_OFF, new ItemInfo(R.string.setting_off, null, 0));
        upsideMap.put(Upside.UPSIDE_ON, new ItemInfo(R.string.setting_on, null, 0));
    }

    public void initBurstMap() {
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_OFF, new ItemInfo(R.string.burst_off, null, 0));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_3, new ItemInfo(R.string.burst_3, null, R.drawable.continuous_shot_1));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_5, new ItemInfo(R.string.burst_5, null, R.drawable.continuous_shot_2));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_10, new ItemInfo(R.string.burst_10, null, R.drawable.continuous_shot_3));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_7, new ItemInfo(R.string.burst_7, null, R.drawable.continuous_shot_7));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_15, new ItemInfo(R.string.burst_15, null, R.drawable.continuous_shot_15));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_30, new ItemInfo(R.string.burst_30, null, R.drawable.continuous_shot_30));
        burstMap.put(ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_HS, new ItemInfo(R.string.burst_hs, null, R.drawable.continuous_shot_continuous));
    }

    public void initElectricityFrequencyMap() {
        electricityFrequencyMap.put(ICatchCamLightFrequency.ICH_CAM_LIGHT_FREQUENCY_50HZ, new ItemInfo(R.string.frequency_50HZ, null, 0));
        electricityFrequencyMap.put(ICatchCamLightFrequency.ICH_CAM_LIGHT_FREQUENCY_60HZ, new ItemInfo(R.string.frequency_60HZ, null, 0));
    }

    public void initDateStampMap() {
        dateStampMap.put(ICatchCamDateStamp.ICH_CAM_DATE_STAMP_OFF, new ItemInfo(R.string.dateStamp_off, null, 0));
        dateStampMap.put(ICatchCamDateStamp.ICH_CAM_DATE_STAMP_DATE, new ItemInfo(R.string.dateStamp_date, null, 0));
        dateStampMap.put(ICatchCamDateStamp.ICH_CAM_DATE_STAMP_DATE_TIME, new ItemInfo(R.string.dateStamp_date_and_time, null, 0));
    }

    public void initPhotoModeMap() {
        photoModeMap.put(PhotoMode.PHOTO_MODE_SINGLE, new ItemInfo(R.string.photo_mode_single, null, 0));
        photoModeMap.put(PhotoMode.PHOTO_MODE_BURST, new ItemInfo(R.string.photo_mode_burst, null, 0));
        photoModeMap.put(PhotoMode.PHOTO_MODE_TIMELAPSE, new ItemInfo(R.string.photo_mode_timelapse, null, 0));
        photoModeMap.put(PhotoMode.PHOTO_MODE_SELF_TIMER, new ItemInfo(R.string.photo_mode_self_timer, null, 0));
    }

    public void initImageQualityMap() {
        imageQualityMap.put(ImageQuality.IMAGE_QUALITY_HIGH, new ItemInfo(R.string.image_quality_high, null, 0));
        imageQualityMap.put(ImageQuality.IMAGE_QUALITY_MIDDLE, new ItemInfo(R.string.image_quality_middle, null, 0));
        imageQualityMap.put(ImageQuality.IMAGE_QUALITY_LOW, new ItemInfo(R.string.image_quality_low, null, 0));
    }

    public void initPhotoVideoMeteringMap() {
        photoVideoMeteringMap.put(PhotoVideoMetering.PHOTO_VIDEO_METERING_CENTER, new ItemInfo(R.string.photo_video_metering_center, null, 0));
        photoVideoMeteringMap.put(PhotoVideoMetering.PHOTO_VIDEO_METERING_MULTI, new ItemInfo(R.string.photo_video_metering_multi, null, 0));
    }

    public void initVideoModeMap() {
        videoModeMap.put(VideoMode.VIDEO_MODE_NORMAL, new ItemInfo(R.string.video_mode_normal, null, 0));
        videoModeMap.put(VideoMode.VIDEO_MODE_TIMELAPSE, new ItemInfo(R.string.video_mode_timelapse, null, 0));
        videoModeMap.put(VideoMode.VIDEO_MODE_LOOP, new ItemInfo(R.string.video_mode_loop_recording, null, 0));
        videoModeMap.put(VideoMode.VIDEO_MODE_SLOW_MOTION, new ItemInfo(R.string.video_mode_slow_motion, null, 0));
    }

    public void initVideoFovAngleMap() {
        videoFovAngleMap.put(VideoFovAngle.VIDEO_FOV_ANGLE_SUPER_WIDE, new ItemInfo(R.string.video_fov_angle_super_wide, null, 0));
        videoFovAngleMap.put(VideoFovAngle.VIDEO_FOV_ANGLE_WIDE, new ItemInfo(R.string.video_fov_angle_wide, null, 0));
        videoFovAngleMap.put(VideoFovAngle.VIDEO_FOV_ANGLE_MEDIUM, new ItemInfo(R.string.video_fov_angle_medium, null, 0));
        videoFovAngleMap.put(VideoFovAngle.VIDEO_FOV_ANGLE_NARROW, new ItemInfo(R.string.video_fov_angle_narrow, null, 0));
    }

    public void initGeneralDeviceSoundMap() {
        generalDeviceSoundMap.put(GeneralDeviceSound.GENERAL_DEVICE_SOUND_HIGH, new ItemInfo(R.string.general_device_sound_high, null, 0));
        generalDeviceSoundMap.put(GeneralDeviceSound.GENERAL_DEVICE_SOUND_LOW, new ItemInfo(R.string.general_device_sound_low, null, 0));
        generalDeviceSoundMap.put(GeneralDeviceSound.GENERAL_DEVICE_SOUND_OFF, new ItemInfo(R.string.general_device_sound_off, null, 0));
    }

    public void initGeneralLanguageMap() {
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_ENGLISH, new ItemInfo(R.string.general_language_english, null, 0));
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_GERMAN, new ItemInfo(R.string.general_language_german, null, 0));
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_FRENCH, new ItemInfo(R.string.general_language_french, null, 0));
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_ITALIAN, new ItemInfo(R.string.general_language_italian, null, 0));
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_SPANISH, new ItemInfo(R.string.general_language_spanish, null, 0));
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_JAPANESE, new ItemInfo(R.string.general_language_japanese, null, 0));
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_CHINESE_SIMPLIFIED, new ItemInfo(R.string.general_language_chinese_simplified, null, 0));
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_CHINESE_TRADITIONAL, new ItemInfo(R.string.general_language_chinese_traditional, null, 0));
        generalLanguageMap.put(GeneralLanguage.GENERAL_LANGUAGE_KOREAN, new ItemInfo(R.string.general_language_korean, null, 0));
    }

    public void initGeneralColorEffectMap() {
        generalColorEffectMap.put(GeneralColorEffect.GENERAL_COLOR_EFFECT_NORMAL, new ItemInfo(R.string.general_color_effect_normal, null, 0));
        generalColorEffectMap.put(GeneralColorEffect.GENERAL_COLOR_EFFECT_BW, new ItemInfo(R.string.general_color_effect_bw, null, 0));
        generalColorEffectMap.put(GeneralColorEffect.GENERAL_COLOR_EFFECT_NATURAL, new ItemInfo(R.string.general_color_effect_natural, null, 0));
        generalColorEffectMap.put(GeneralColorEffect.GENERAL_COLOR_EFFECT_NEGATIVE, new ItemInfo(R.string.general_color_effect_negative, null, 0));
        generalColorEffectMap.put(GeneralColorEffect.GENERAL_COLOR_EFFECT_WARM, new ItemInfo(R.string.general_color_effect_warm, null, 0));
        generalColorEffectMap.put(GeneralColorEffect.GENERAL_COLOR_EFFECT_BRIGHTNESS_CONTRAST, new ItemInfo(R.string.general_color_effect_brightness_contrast, null, 0));
    }

    public void initGeneralDateFormatMap() {
        generalDateFormatMap.put(GeneralDateFormat.GENERAL_DATE_FORMAT_YYMMDD, new ItemInfo(R.string.general_date_format_yymmdd, null, 0));
        generalDateFormatMap.put(GeneralDateFormat.GENERAL_DATE_FORMAT_MMDDYY, new ItemInfo(R.string.general_date_format_mmddyy, null, 0));
        generalDateFormatMap.put(GeneralDateFormat.GENERAL_DATE_FORMAT_DDMMYY, new ItemInfo(R.string.general_date_format_ddmmyy, null, 0));
    }
}
