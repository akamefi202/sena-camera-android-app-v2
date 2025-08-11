package com.sena.senacamera.function.Setting;

import android.content.Context;

import com.sena.senacamera.application.PanoramaApp;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.data.AppInfo.AppInfo;
import com.sena.senacamera.data.type.TimeLapseMode;
import com.sena.senacamera.data.entity.SettingMenu;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.data.PropertyId.PropertyId;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraFixedInfo;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.SdkApi.CameraState;
import com.sena.senacamera.utils.StorageUtil;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.LinkedList;

public class UIDisplaySource {
    public static final int CAPTURE_SETTING_MENU = 1;
    public static final int VIDEO_SETTING_MENU = 2;
    public static final int TIMELAPSE_SETTING_MENU = 3;
    private static UIDisplaySource uiDisplayResource;
    private CameraState cameraState;
    private CameraProperties cameraProperties;
    private BaseProperties BaseProperties;
    private CameraFixedInfo cameraFixedInfo;
    private MyCamera curCamera;
    private LinkedList<SettingMenu> settingMenuList;

    public static UIDisplaySource getInstance() {
        if (uiDisplayResource == null) {
            uiDisplayResource = new UIDisplaySource();
        }
        return uiDisplayResource;
    }

    public LinkedList<SettingMenu> getList(int type, MyCamera currCamera) {
        this.curCamera = currCamera;
        this.cameraState = currCamera.getCameraState();
        this.cameraProperties = currCamera.getCameraProperties();
        this.BaseProperties = currCamera.getBaseProperties();
        this.cameraFixedInfo = currCamera.getCameraFixedInfo();
        switch (type) {
            case CAPTURE_SETTING_MENU:
                return getForCaptureMode();
            case VIDEO_SETTING_MENU:
                return getForVideoMode();
            case TIMELAPSE_SETTING_MENU:
                return getForTimelapseMode();
            default:
                return null;
        }
    }

    public LinkedList<SettingMenu> getForCaptureMode() {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }

        // photo mode settings
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_MODE)) {
//            settingMenuList.add(new SettingMenu(R.string.photo_mode, BaseProperties.getPhotoMode().getCurrentUiStringInSetting()));
//        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE)) {
            settingMenuList.add(new SettingMenu(R.string.setting_image_size, BaseProperties.getPhotoResolution().getCurrentUiStringInSetting()));
        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_IMAGE_QUALITY)) {
//            settingMenuList.add(new SettingMenu(R.string.image_quality, BaseProperties.getPhotoVideoImageQuality().getCurrentUiStringInSetting()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_ISO)) {
//            settingMenuList.add(new SettingMenu(R.string.iso, BaseProperties.getPhotoIso().getCurrentUiStringInSetting()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_EV)) {
//            settingMenuList.add(new SettingMenu(R.string.ev, BaseProperties.getPhotoVideoEv().getCurrentUiStringInSetting()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_METERING)) {
//            settingMenuList.add(new SettingMenu(R.string.metering, BaseProperties.getPhotoVideoMetering().getCurrentUiStringInSetting()));
//        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_capture_delay, BaseProperties.getPhotoSelfTimer().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER)) {
            settingMenuList.add(new SettingMenu(R.string.title_burst, BaseProperties.getPhotoBurst().getCurrentUiStringInSetting()));
        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_TIMELAPSE_STILL)) {
//            //settingMenuList.add(new SettingMenu(R.string.timelapse_interval, BaseProperties.getTimeLapseStillInterval().getCurrentValue()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_TIMELAPSE_DURATION)) {
//            settingMenuList.add(new SettingMenu(R.string.setting_time_lapse_duration, BaseProperties.getPhotoVideoTimelapseDuration().getCurrentUiStringInSetting()));
//        }

        // general settings
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_AUDIO_INPUT)) {
//            settingMenuList.add(new SettingMenu(R.string.audio_input, BaseProperties.getGeneralAudioInput().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_DISPLAY_BRIGHTNESS)) {
//            settingMenuList.add(new SettingMenu(R.string.display_brightness, BaseProperties.getGeneralDisplayBrightness().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_DEVICE_SOUND)) {
//            settingMenuList.add(new SettingMenu(R.string.device_sound, BaseProperties.getGeneralDeviceSound().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_FRONT_DISPLAY)) {
//            settingMenuList.add(new SettingMenu(R.string.font_display, BaseProperties.getGeneralFrontDisplay().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_MAIN_STATUS_LED)) {
//            settingMenuList.add(new SettingMenu(R.string.main_status_led, BaseProperties.getGeneralMainStatusLed().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_BATTERY_STATUS_LED)) {
//            settingMenuList.add(new SettingMenu(R.string.battery_status_led, BaseProperties.getGeneralBatteryStatusLed().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_LANGUAGE)) {
//            settingMenuList.add(new SettingMenu(R.string.language, BaseProperties.getGeneralLanguage().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_COLOR_EFFECT)) {
//            settingMenuList.add(new SettingMenu(R.string.color_effect, BaseProperties.getGeneralColorEffect().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_DATE_FORMAT)) {
//            settingMenuList.add(new SettingMenu(R.string.date_format, BaseProperties.getGeneralDateFormat().getCurrentUiStringInSetting()));
//        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, BaseProperties.getWhiteBalance().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, BaseProperties.getElectricityFrequency().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP)) {
            settingMenuList.add(new SettingMenu(R.string.setting_datestamp, BaseProperties.getDateCaption().getCurrentUiStringInSetting()));
        }
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, ""));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, ""));
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, ""));
//        settingMenuList.add( new SettingMenu( R.string.setting_live_address, AppInfo.liveAddress ) );
        settingMenuList.add(new SettingMenu(R.string.setting_format, ""));
        settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(PanoramaApp.getContext())));
        if (cameraProperties.hasFunction(PropertyId.STA_MODE_SSID)) {
            settingMenuList.add(new SettingMenu(R.string.setting_enable_wifi_hotspot, ""));
        }
        if (cameraProperties.hasFunction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, BaseProperties.getUpside().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, ""));
        }
        if (cameraProperties.hasFunction(PropertyId.POWER_ON_AUTO_RECORD)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_power_on_auto_record, ""));
        }
        if (cameraProperties.hasFunction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, BaseProperties.getAutoPowerOff().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_EV)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, BaseProperties.getPhotoVideoEv().getCurrentUiStringInSetting()));
        }

        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION));
        settingMenuList.add(new SettingMenu(R.string.setting_product_name, cameraFixedInfo.getCameraName()));
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, cameraFixedInfo.getCameraVersion()));
        }
        return settingMenuList;
    }

    private LinkedList<SettingMenu> getForVideoMode() {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }

        // photo mode settings
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.VIDEO_MODE)) {
//            settingMenuList.add(new SettingMenu(R.string.video_mode, BaseProperties.getVideoMode().getCurrentUiStringInSetting()));
//        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_VIDEO_SIZE)) {
            settingMenuList.add(new SettingMenu(R.string.setting_video_size, BaseProperties.getVideoResolution().getCurrentUiStringInSetting()));
        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_IMAGE_QUALITY)) {
//            settingMenuList.add(new SettingMenu(R.string.video_quality, BaseProperties.getPhotoVideoImageQuality().getCurrentUiStringInSetting()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_EV)) {
//            settingMenuList.add(new SettingMenu(R.string.ev, BaseProperties.getPhotoVideoEv().getCurrentUiStringInSetting()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_METERING)) {
//            settingMenuList.add(new SettingMenu(R.string.metering, BaseProperties.getPhotoVideoMetering().getCurrentUiStringInSetting()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.VIDEO_AUTO_LOW_LIGHT)) {
//            settingMenuList.add(new SettingMenu(R.string.auto_low_light, BaseProperties.getVideoAutoLowLight().getCurrentUiStringInSetting()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.VIDEO_FOV)) {
//            settingMenuList.add(new SettingMenu(R.string.fov_angle, BaseProperties.getVideoFov().getCurrentUiStringInSetting()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_TIMELAPSE_STILL)) {
//            //settingMenuList.add(new SettingMenu(R.string.timelapse_interval, BaseProperties.getTimeLapseStillInterval().getCurrentValue()));
//        }
        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_TIMELAPSE_DURATION)) {
//            settingMenuList.add(new SettingMenu(R.string.setting_time_lapse_duration, BaseProperties.getPhotoVideoTimelapseDuration().getCurrentUiStringInSetting()));
//        }

        // general settings
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_AUDIO_INPUT)) {
//            settingMenuList.add(new SettingMenu(R.string.audio_input, BaseProperties.getGeneralAudioInput().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_DISPLAY_BRIGHTNESS)) {
//            settingMenuList.add(new SettingMenu(R.string.display_brightness, BaseProperties.getGeneralDisplayBrightness().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_DEVICE_SOUND)) {
//            settingMenuList.add(new SettingMenu(R.string.device_sound, BaseProperties.getGeneralDeviceSound().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_FRONT_DISPLAY)) {
//            settingMenuList.add(new SettingMenu(R.string.font_display, BaseProperties.getGeneralFrontDisplay().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_MAIN_STATUS_LED)) {
//            settingMenuList.add(new SettingMenu(R.string.main_status_led, BaseProperties.getGeneralMainStatusLed().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_BATTERY_STATUS_LED)) {
//            settingMenuList.add(new SettingMenu(R.string.battery_status_led, BaseProperties.getGeneralBatteryStatusLed().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_LANGUAGE)) {
//            settingMenuList.add(new SettingMenu(R.string.language, BaseProperties.getGeneralLanguage().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_COLOR_EFFECT)) {
//            settingMenuList.add(new SettingMenu(R.string.color_effect, BaseProperties.getGeneralColorEffect().getCurrentUiStringInSetting()));
//        }
//        // akamefi202: updated settings
//        if (cameraProperties.hasFunction(PropertyId.GENERAL_DATE_FORMAT)) {
//            settingMenuList.add(new SettingMenu(R.string.date_format, BaseProperties.getGeneralDateFormat().getCurrentUiStringInSetting()));
//        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, BaseProperties.getWhiteBalance().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, BaseProperties.getElectricityFrequency().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP)) {
            settingMenuList.add(new SettingMenu(R.string.setting_datestamp, BaseProperties.getDateCaption().getCurrentUiStringInSetting()));
        }
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, ""));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, ""));
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, ""));
//        settingMenuList.add( new SettingMenu( R.string.setting_live_address, AppInfo.liveAddress ) );
        settingMenuList.add(new SettingMenu(R.string.setting_format, ""));
        settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(PanoramaApp.getContext())));
        if (cameraProperties.hasFunction(PropertyId.STA_MODE_SSID)) {
            settingMenuList.add(new SettingMenu(R.string.setting_enable_wifi_hotspot, ""));
        }
        //settingMenuList.add(new SettingMenu(R.string.setting_enable_wifi_hotspot, ""));
        if (cameraProperties.hasFunction(PropertyId.SLOW_MOTION)) {
            settingMenuList.add(new SettingMenu(R.string.slowmotion, BaseProperties.getSlowMotion().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, BaseProperties.getUpside().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, ""));
        }

        if (cameraProperties.hasFunction(PropertyId.SCREEN_SAVER)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_screen_saver, BaseProperties.getScreenSaver().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.POWER_ON_AUTO_RECORD)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_power_on_auto_record, ""));
        }
        if (cameraProperties.hasFunction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, BaseProperties.getAutoPowerOff().getCurrentUiStringInSetting()));
        }

        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_EV)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, BaseProperties.getPhotoVideoEv().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.VIDEO_EIS)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_image_stabilization, ""));
        }
        settingMenuList.add(new SettingMenu(R.string.setting_update_fw,""));
        if (cameraProperties.hasFunction(PropertyId.VIDEO_LOOP_RECORDING)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_video_file_length, BaseProperties.getVideoLoopRecording().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.FAST_MOTION_MOVIE)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_fast_motion_movie, BaseProperties.getFastMotionMovie().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.WIND_NOISE_REDUCTION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_wind_noise_reduction, ""));
        }

        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION));
        settingMenuList.add(new SettingMenu(R.string.setting_product_name, cameraFixedInfo.getCameraName()));
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, cameraFixedInfo.getCameraVersion()));
        }

        return settingMenuList;
    }

    public LinkedList<SettingMenu> getForTimelapseMode() {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
        if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
            if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE)) {
                settingMenuList.add(new SettingMenu(R.string.setting_image_size, BaseProperties.getPhotoResolution().getCurrentUiStringInSetting()));
            }
        } else if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_VIDEO) {
            if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_VIDEO_SIZE)) {
                settingMenuList.add(new SettingMenu(R.string.setting_video_size, BaseProperties.getVideoResolution().getCurrentUiStringInSetting()));
            }
        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, BaseProperties.getWhiteBalance().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, BaseProperties.getElectricityFrequency().getCurrentUiStringInSetting()));
        }
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, ""));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, ""));
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, ""));
//        settingMenuList.add( new SettingMenu( R.string.setting_live_address, AppInfo.liveAddress ) );
        settingMenuList.add(new SettingMenu(R.string.setting_format, ""));
        settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(PanoramaApp.getContext())));
        if (cameraProperties.hasFunction(PropertyId.STA_MODE_SSID)) {
            settingMenuList.add(new SettingMenu(R.string.setting_enable_wifi_hotspot, ""));
        }
//        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE)) {
//            String curTimeLapseInterval;
//            if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
//                curTimeLapseInterval = BaseProperties.getTimeLapseStillInterval().getCurrentValue();
//            } else {
//                curTimeLapseInterval = BaseProperties.getTimeLapseVideoInterval().getCurrentValue();
//            }
//            settingMenuList.add(new SettingMenu(R.string.title_timelapse_mode, BaseProperties.getTimeLapseMode().getCurrentUiStringInSetting()));
//            settingMenuList.add(new SettingMenu(R.string.setting_time_lapse_interval, curTimeLapseInterval));
//            settingMenuList.add(new SettingMenu(R.string.setting_time_lapse_duration, BaseProperties.getTimeLapseDuration().getCurrentValue()));
//        }

        if (cameraProperties.hasFunction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, BaseProperties.getUpside().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, ""));

        }
        if (cameraProperties.hasFunction(PropertyId.POWER_ON_AUTO_RECORD)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_power_on_auto_record, ""));
        }
        if (cameraProperties.hasFunction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, BaseProperties.getAutoPowerOff().getCurrentUiStringInSetting()));
        }
        if (cameraProperties.hasFunction(PropertyId.PHOTO_VIDEO_EV)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, BaseProperties.getPhotoVideoEv().getCurrentUiStringInSetting()));
        }
        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION));
        settingMenuList.add(new SettingMenu(R.string.setting_product_name, cameraFixedInfo.getCameraName()));
        if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, cameraFixedInfo.getCameraVersion()));
        }
        return settingMenuList;
    }


    public LinkedList<SettingMenu> getUSBList(Context context) {
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, "",R.string.setting_title_switch));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_display_temperature, "", R.string.setting_type_switch));
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, ""));
//        settingMenuList.add(new SettingMenu(R.string.setting_image_size, GlobalInfo.getInstance().getCurImageSize(), R.string.setting_type_general));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_storage_location, context.getResources().getString(R.string.setting_value_internal_storage), R.string.setting_type_other));
        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION));
//            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, "",R.string.setting_type_switch));
        return settingMenuList;
    }
}
