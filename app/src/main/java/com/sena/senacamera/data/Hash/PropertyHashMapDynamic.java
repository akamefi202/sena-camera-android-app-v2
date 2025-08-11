package com.sena.senacamera.data.Hash;

import android.content.Context;

import com.sena.senacamera.application.PanoramaApp;
import com.sena.senacamera.R;
import com.sena.senacamera.data.entity.ItemInfo;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.data.PropertyId.PropertyId;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.utils.ConvertTools;
import com.icatchtek.control.customer.ICatchCameraUtil;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;

import java.util.HashMap;
import java.util.List;

public class PropertyHashMapDynamic {
    private static final String TAG = PropertyHashMapDynamic.class.getSimpleName();
    private static PropertyHashMapDynamic propertyHashMap;

    public static PropertyHashMapDynamic getInstance() {
        if (propertyHashMap == null) {
            propertyHashMap = new PropertyHashMapDynamic();
        }
        return propertyHashMap;
    }

    public HashMap<Integer, ItemInfo> getDynamicHashInt(CameraProperties cameraProperties, int propertyId) {
        switch (propertyId) {
            case PropertyId.PHOTO_SELF_TIMER:
                return getSelfTimerMap(cameraProperties);
            case PropertyId.AUTO_POWER_OFF:
                return getAutoPowerOffMap(cameraProperties);
            case PropertyId.PHOTO_VIDEO_EV:
                return getPhotoVideoEvMap(cameraProperties);
            case PropertyId.VIDEO_LOOP_RECORDING:
                return getVideoLoopRecordingMap(cameraProperties);
            case PropertyId.FAST_MOTION_MOVIE:
                return getFastMotionMovieMap(cameraProperties);
            case PropertyId.SCREEN_SAVER:
                return getScreenSaverMap(cameraProperties);
            case PropertyId.GENERAL_FRONT_DISPLAY:
                return getFrontDisplayMap(cameraProperties);
            case PropertyId.GENERAL_MAIN_STATUS_LED:
                return getMainStatusLedMap(cameraProperties);
            case PropertyId.GENERAL_BATTERY_STATUS_LED:
                return getBatteryStatusLedMap(cameraProperties);
            case PropertyId.VIDEO_AUTO_LOW_LIGHT:
                return getAutoLowLightMap(cameraProperties);
            case PropertyId.VIDEO_EIS:
                return getVideoEisMap(cameraProperties);
            case PropertyId.PHOTO_VIDEO_ISO:
                return getPhotoVideoIsoMap(cameraProperties);
            case PropertyId.GENERAL_AUDIO_INPUT:
                return getAudioInputMap(cameraProperties);
            case PropertyId.GENERAL_DISPLAY_BRIGHTNESS:
                return getDisplayBrightnessMap(cameraProperties);
            case PropertyId.PHOTO_VIDEO_TIMELAPSE_INTERVAL:
                return getTimelapseIntervalMap(cameraProperties);
            case PropertyId.PHOTO_VIDEO_TIMELAPSE_DURATION:
                return getTimelapseDurationMap(cameraProperties);
            default:
                return null;
        }
    }

    private HashMap<Integer, ItemInfo> getTimelapseIntervalMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> timelapseIntervalMap = new HashMap<Integer, ItemInfo>();
        List<Integer> timelapseIntervalList = cameraProperties.getSupportedTimeLapseIntervals();

        String temp;
        for (int i = 0; i < timelapseIntervalList.size(); i++) {
            int value = timelapseIntervalList.get(i);
            if (value >= 60) {
                temp = String.format("%d min", value / 60);
            } else {
                temp = String.format("%d sec", value);
            }
            AppLog.d(TAG, "timelapseIntervalList i=" + i + ", value=" + value);
            timelapseIntervalMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return timelapseIntervalMap;
    }

    private HashMap<Integer, ItemInfo> getTimelapseDurationMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> timelapseDurationMap = new HashMap<Integer, ItemInfo>();
        List<Integer> timelapseDurationList = cameraProperties.getSupportedTimeLapseDurations();

        String temp;
        for (int i = 0; i < timelapseDurationList.size(); i++) {
            int value = timelapseDurationList.get(i);
            if (value == 0xffff) {
                temp = PanoramaApp.getContext().getString(R.string.setting_time_lapse_duration_unlimited);
            } else {
                temp = String.format("%d min", value);
            }
            AppLog.d(TAG, "timelapseDurationList i=" + i + ", value=" + value);
            timelapseDurationMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return timelapseDurationMap;
    }

    private HashMap<Integer, ItemInfo> getPhotoVideoIsoMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> photoVideoIsoMap = new HashMap<Integer, ItemInfo>();
        List<Integer> photoVideoIsoList = cameraProperties.getSupportedIso();

        String temp;
        for (int i = 0; i < photoVideoIsoList.size(); i++) {
            int value = photoVideoIsoList.get(i);
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.auto);
            } else {
                temp = String.valueOf(value);
            }
            AppLog.d(TAG, "photoVideoIsoList i=" + i + ", value=" + value);
            photoVideoIsoMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return photoVideoIsoMap;
    }

    private HashMap<Integer, ItemInfo> getAutoPowerOffMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> autoPowerOffMap = new HashMap<Integer, ItemInfo>();

        List<Integer> autoPowerOffList = cameraProperties.getSupportedPropertyValues(PropertyId.AUTO_POWER_OFF);
        String temp;
        for (int i = 0; i < autoPowerOffList.size(); i++) {
            int value = autoPowerOffList.get(i);
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = value + "s";
            }
            AppLog.d(TAG, "autoPowerOffList i=" + i + " value=" + value);
            autoPowerOffMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return autoPowerOffMap;
    }

    private HashMap<Integer, ItemInfo> getScreenSaverMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> screenSaverMap = new HashMap<Integer, ItemInfo>();
        List<Integer> screenSaverList = cameraProperties.getSupportedPropertyValues(PropertyId.SCREEN_SAVER);
        String temp;
        for (int i = 0; i < screenSaverList.size(); i++) {
            int value = screenSaverList.get(i);
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = value + "s";
            }
            AppLog.d(TAG, "screenSaverList i=" + i + " value=" + value);
            screenSaverMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return screenSaverMap;
    }

    public HashMap<String, ItemInfo> getDynamicHashString(CameraProperties cameraProperties, int propertyId) {
        switch (propertyId) {
            case PropertyId.PHOTO_RESOLUTION:
                return getPhotoResolutionMap(cameraProperties);
            case PropertyId.VIDEO_RESOLUTION:
                return getVideoResolutionMap(cameraProperties);
            default:
                return null;
        }
    }

    private HashMap<Integer, ItemInfo> getFastMotionMovieMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> fastMotionMovieMap = new HashMap<Integer, ItemInfo>();
        List<Integer> fastMotionMovieList = cameraProperties.getSupportedPropertyValues(PropertyId.FAST_MOTION_MOVIE);
        String temp;
        for (int i = 0; i < fastMotionMovieList.size(); i++) {
            int value = fastMotionMovieList.get(i);
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = value + "x";
            }
            AppLog.d(TAG, "fastMotionMovieList i=" + i + ", value=" + value);
            fastMotionMovieMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return fastMotionMovieMap;
    }

    private HashMap<Integer, ItemInfo> getSelfTimerMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> selfTimerMap = new HashMap<Integer, ItemInfo>();
        List<Integer> selfTimerList = cameraProperties.getSupportedPropertyValues(PropertyId.PHOTO_SELF_TIMER);
        String temp;
        for (int i = 0; i < selfTimerList.size(); i ++) {
            if (selfTimerList.get(i) == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = PanoramaApp.getContext().getString(R.string.delay) + " " + selfTimerList.get(i) / 1000 + "s";
            }

            AppLog.d(TAG, "selfTimerList i=" + i + ", value=" + selfTimerList.get(i));
            selfTimerMap.put(selfTimerList.get(i), new ItemInfo(temp, temp, 0));
        }
        return selfTimerMap;
    }

    private HashMap<Integer, ItemInfo> getPhotoVideoEvMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> exposureCompensationMap = new HashMap<Integer, ItemInfo>();
        List<Integer> exposureCompensationList = cameraProperties.getSupportedPropertyValues(PropertyId.PHOTO_VIDEO_EV);
//        String temp;
        for (int i = 0; i < exposureCompensationList.size(); i ++) {
            int value = exposureCompensationList.get(i);
            String temp = ConvertTools.getExposureCompensation(value);

            AppLog.d(TAG, "exposureCompensationList i=" + i + ", value=" + value + ", temp=" + temp);
            exposureCompensationMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return exposureCompensationMap;
    }

    private HashMap<Integer, ItemInfo> getFrontDisplayMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> frontDisplayMap = new HashMap<Integer, ItemInfo>();
        List<Integer> frontDisplayList = cameraProperties.getSupportedPropertyValues(PropertyId.GENERAL_FRONT_DISPLAY);

        for (int i = 0; i < frontDisplayList.size(); i++) {
            int value = frontDisplayList.get(i);
            String temp;
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = PanoramaApp.getContext().getString(R.string.on);
            }

            AppLog.d(TAG, "frontDisplayList i=" + i + ", value=" + value + ", temp=" + temp);
            frontDisplayMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return frontDisplayMap;
    }

    private HashMap<Integer, ItemInfo> getMainStatusLedMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> mainStatusLedMap = new HashMap<Integer, ItemInfo>();
        List<Integer> mainStatusLedList = cameraProperties.getSupportedPropertyValues(PropertyId.GENERAL_MAIN_STATUS_LED);

        for (int i = 0; i < mainStatusLedList.size(); i++) {
            int value = mainStatusLedList.get(i);
            String temp;
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = PanoramaApp.getContext().getString(R.string.on);
            }

            AppLog.d(TAG, "mainStatusLedList i=" + i + ", value=" + value + ", temp=" + temp);
            mainStatusLedMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return mainStatusLedMap;
    }

    private HashMap<Integer, ItemInfo> getBatteryStatusLedMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> batteryStatusLedMap = new HashMap<Integer, ItemInfo>();
        List<Integer> batteryStatusLedList = cameraProperties.getSupportedPropertyValues(PropertyId.GENERAL_BATTERY_STATUS_LED);

        for (int i = 0; i < batteryStatusLedList.size(); i++) {
            int value = batteryStatusLedList.get(i);
            String temp;
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = PanoramaApp.getContext().getString(R.string.on);
            }

            AppLog.d(TAG, "batteryStatusLedList i=" + i + ", value=" + value + ", temp=" + temp);
            batteryStatusLedMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return batteryStatusLedMap;
    }

    private HashMap<Integer, ItemInfo> getAutoLowLightMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> autoLowLightMap = new HashMap<Integer, ItemInfo>();
        List<Integer> autoLowLightList = cameraProperties.getSupportedPropertyValues(PropertyId.VIDEO_AUTO_LOW_LIGHT);

        for (int i = 0; i < autoLowLightList.size(); i++) {
            int value = autoLowLightList.get(i);
            String temp;
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = PanoramaApp.getContext().getString(R.string.on);
            }

            AppLog.d(TAG, "autoLowLightList i=" + i + ", value=" + value + ", temp=" + temp);
            autoLowLightMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return autoLowLightMap;
    }

    private HashMap<Integer, ItemInfo> getVideoEisMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> videoEisMap = new HashMap<Integer, ItemInfo>();
        List<Integer> videoEisList = cameraProperties.getSupportedPropertyValues(PropertyId.VIDEO_EIS);

        for (int i = 0; i < videoEisList.size(); i++) {
            int value = videoEisList.get(i);
            String temp;
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = PanoramaApp.getContext().getString(R.string.on);
            }

            AppLog.d(TAG, "videoEisList i=" + i + ", value=" + value + ", temp=" + temp);
            videoEisMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return videoEisMap;
    }

    private HashMap<Integer, ItemInfo> getAudioInputMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> audioInputMap = new HashMap<Integer, ItemInfo>();
        List<Integer> audioInputList = cameraProperties.getSupportedPropertyValues(PropertyId.GENERAL_AUDIO_INPUT);

        for (int i = 0; i < audioInputList.size(); i++) {
            int value = audioInputList.get(i);
            String temp;
            if (value == 0) {
                temp = PanoramaApp.getContext().getString(R.string.off);
            } else {
                temp = PanoramaApp.getContext().getString(R.string.on);
            }

            AppLog.d(TAG, "audioInputList i=" + i + ", value=" + value + ", temp=" + temp);
            audioInputMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return audioInputMap;
    }

    private HashMap<Integer, ItemInfo> getDisplayBrightnessMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> displayBrightnessMap = new HashMap<Integer, ItemInfo>();
        List<Integer> displayBrightnessList = cameraProperties.getSupportedPropertyValues(PropertyId.GENERAL_DISPLAY_BRIGHTNESS);

        for (int i = 0; i < displayBrightnessList.size(); i++) {
            int value = displayBrightnessList.get(i);
            String temp = String.valueOf(value);

            AppLog.d(TAG, "audioInputList i=" + i + ", value=" + value + ", temp=" + temp);
            displayBrightnessMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return displayBrightnessMap;
    }

    private HashMap<Integer, ItemInfo> getVideoLoopRecordingMap(CameraProperties cameraProperties) {
        HashMap<Integer, ItemInfo> videoLoopRecordingMap = new HashMap<Integer, ItemInfo>();
        List<Integer> videoLoopRecordingList = cameraProperties.getSupportedPropertyValues(PropertyId.VIDEO_LOOP_RECORDING);
        String temp;
        Context context = PanoramaApp.getContext();
        for (int i = 0; i < videoLoopRecordingList.size(); i++) {
            int value = videoLoopRecordingList.get(i);
            if (value == 0) {
                temp = context.getString(R.string.text_file_length_unlimited);
            } else if (value < 1000) {
                temp = value / 60 + " " + context.getString(R.string.time_minutes);
            } else {
                //AIBSP-1934 for CVR 20200701
                String fileSize = value / 1000 + "MB";
                String fileLength;
                if (value % 1000 == 0) {
                    fileLength =  fileSize;
                } else {
                    fileLength = (value % 1000) / 60 + context.getString(R.string.time_minutes);
                }
                temp = fileLength + " + " + fileSize;
            }

            AppLog.d(TAG, "videoLoopRecordingList i=" + i + ", value=" + value);
            videoLoopRecordingMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return videoLoopRecordingMap;
    }

    private HashMap<String, ItemInfo> getPhotoResolutionMap(CameraProperties cameraProperties) {
//        AppLog.i( TAG, "begin initImageSizeMap" );
        HashMap<String, ItemInfo> imageSizeMap = new HashMap<String, ItemInfo>();
        List<String> imageSizeList = cameraProperties.getSupportedImageSizes();
        List<Integer> convertImageSizeList = null;

        try {
            convertImageSizeList = ICatchCameraUtil.convertImageSizes(imageSizeList);
        } catch (IchInvalidArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String tempSetting;
        String tempPreview;
        for (int i = 0; i < imageSizeList.size(); i++) {
            if (convertImageSizeList.get(i) == 0) {
//                tempSetting = "VGA (" + imageSizeList.get(i) + ")";
                tempSetting = "VGA";
                tempPreview = "VGA";
                imageSizeMap.put(imageSizeList.get(i), new ItemInfo(tempSetting, tempPreview, 0));
            } else {
//                tempSetting = convertImageSizeList.get(i) + "MP (" + imageSizeList.get(i) + ")";
                tempSetting = convertImageSizeList.get(i) + "MP";
                tempPreview = convertImageSizeList.get(i) + "MP";
                imageSizeMap.put(imageSizeList.get(i), new ItemInfo(tempSetting, tempPreview, 0));
            }
            AppLog.i(TAG, "imageSize =" + tempSetting);
        }
        return imageSizeMap;
    }

    private HashMap<String, ItemInfo> getVideoResolutionMap(CameraProperties cameraProperties) {
        HashMap<String, ItemInfo> videoSizeMap = new HashMap<String, ItemInfo>();
        List<String> videoSizeList = cameraProperties.getSupportedVideoSizes();
//        List<String> videoSizeList = camera.getShSetting().getCameraSettingProperty().getVideoSizeList();
        if (videoSizeList == null) {
            return videoSizeMap;
        }
        for (int i = 0; i < videoSizeList.size(); i++) {
            String videoSize = videoSizeList.get(i);
            AppLog.i(TAG, "videoSizeList_" + i + " = " + videoSize);
//            String fullName = getFullName(videoSize);
            String fullName = getAbbreviation(videoSize);
            String abbreviationSize = getAbbreviation(videoSize);
            if (fullName != null && abbreviationSize != null) {
                videoSizeMap.put(videoSizeList.get(i), new ItemInfo(fullName, abbreviationSize, 0));
            }
        }
        AppLog.i(TAG, "end initVideoSizeMap videoSizeList size=" + videoSizeList.size() + " videoSizeMap size=" + videoSizeMap.size());
        return videoSizeMap;
    }

    //採用分段映射
    String getAbbreviation(String videoSize) {
        if (videoSize == null) {
            AppLog.d(TAG, "getAbbreviation videoSize is null!");
            return null;
        }

        String[] strings = videoSize.split(" ");
        if (strings == null || strings.length != 2) {
            AppLog.d(TAG, "getAbbreviation videoSize format is wrong!");
            return null;
        }

        String size = strings[0];
        String pts = strings[1];
        String abbreviationSize = null;

        if (size.equals("1920x1080")) {
            abbreviationSize = "1080P";
        } else if (size.equals("1280x720")) {
            abbreviationSize = "720P";
        } else if (size.equals("1920x1440")) {
            abbreviationSize = "1440P";
        } else if (size.equals("2560x1280")) {
            abbreviationSize = "1280P";
        } else if (size.equals("1920x960")
                || size.equals("1440x960")
                || size.equals("1280x960")) {
            abbreviationSize = "960P";
        } else if (size.equals("1280x640")
                || size.equals("480x640")
                || size.equals("1152x648")) {
            abbreviationSize = "640P";
        } else if (size.equals("240x320")) {
            abbreviationSize = "320P";
        } else if (size.equals("320x240")) {
            abbreviationSize = "240P";
        } else if (size.equals("640x480")
                 ||size.equals("640x360")) {
            abbreviationSize = "VGA";
        } else if (size.equals("7680x4320")) {
            abbreviationSize = "8K";
        } else if (size.equals("5760x2880")
                || size.equals("5760x3240")) {
            abbreviationSize = "6K";
        } else if (size.equals("5120x2880")) {
            abbreviationSize = "5K";
        } else if (size.equals("3840x2160")
                || size.equals("3840x1920")) {
            abbreviationSize = "4K";
        } else if (size.equals("2704x1524")
                || size.equals("2800x1400")
                || size.equals("2880x1440")
                || size.equals("2720x1520")
                || size.equals("2704x1400")
                || size.equals("2704x1520")
                || size.equals("2560x1440")) {
            abbreviationSize = "2.7K";
        } else if (size.equals("848x480")) {
            abbreviationSize = "WVGA";
        }

        if (abbreviationSize == null) {
            AppLog.d(TAG, "getAbbreviation videoSize 不支持!");
            abbreviationSize = size + " " + pts + "FPS";
        } else {
            abbreviationSize = abbreviationSize + " " + pts + "FPS";
        }

        AppLog.d(TAG, "getAbbreviation abbreviation=" + abbreviationSize);
        return abbreviationSize;
    }

    String getFullName(String videoSize) {
        if (videoSize == null) {
            AppLog.d(TAG, "getFullName videoSize is null!");
            return null;
        }
        String[] strings = videoSize.split(" ");
        if (strings == null || strings.length != 2) {
            AppLog.d(TAG, "getFullName videoSize 格式不正确!");
            return null;
        }
        String size = strings[0];
        String pts = strings[1];
//        String quality = strings[2];
        String fullName = size + " " + pts + "fps";
        AppLog.d(TAG, "getFullName fullName=" + fullName);
        return fullName;
    }

}
