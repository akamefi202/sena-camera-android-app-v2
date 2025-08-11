package com.sena.senacamera.data.PropertyId;


import com.icatchtek.control.customer.type.ICatchCamProperty;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 11:49.
 */
public class PropertyId {

    public static final int PHOTO_SELF_TIMER = ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY;
    public static final int PHOTO_BURST = ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER;
    public static final int WHITE_BALANCE = ICatchCamProperty.ICH_CAM_CAP_WHITE_BALANCE; // 0x5005
    public static final int LIGHT_FREQUENCY = ICatchCamProperty.ICH_CAM_CAP_LIGHT_FREQUENCY;
    public static final int UP_SIDE = 0xd614;
    public static final int SLOW_MOTION = 0xd615;
    public static final int DATE_CAPTION = ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP;
    public static final int PHOTO_RESOLUTION = ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE;
    public static final int VIDEO_RESOLUTION = ICatchCamProperty.ICH_CAM_CAP_VIDEO_SIZE;
    public static final int ESSID_NAME = 0xd834;
    public static final int ESSID_PASSWORD = 0xd835;
    public static final int CAMERA_NAME = 0xd831;
    public static final int CAMERA_PASSWORD = 0xD83D;
    public static final int TIMELAPSE_MODE = 0xEE00;
    public static final int CAPTURE_DELAY_MODE = 0xD7F0;
    public static final int NOTIFY_FW_TO_SHARE_MODE = 0xD7FB;
    public static final int VIDEO_SIZE_FLOW = 0xD7FC;
    public static final int VIDEO_RECORDING_TIME = 0xD7FD;
    public static final int CAMERA_DATE = 0x5011;
    public static final int CAMERA_ESSID = 0xD83C;
    public static final int CAMERA_PASSWORD_NEW = 0xD832;
    public static final int SERVICE_ESSID = 0xD836;
    public static final int SERVICE_PASSWORD = 0xD837;
    public static final int CAMERA_CONNECT_CHANGE = 0xD7A1;

    public static final int STA_MODE_SSID = 0xD834;
    public static final int STA_MODE_PASSWORD = 0xD835;
    public static final int AP_MODE_TO_STA_MODE = 0xD7FB;

    public static final int SUPPORT_PREVIEW = 0xD7FF;
    public static final int SCREEN_SAVER = 0xD720; // 55072
    public static final int AUTO_POWER_OFF = 0xD721; // 55073
    public static final int POWER_ON_AUTO_RECORD = 0xD722; // 55074
    //曝光补偿
    public static final int PHOTO_VIDEO_EV = 0xD723; // 55075
    public static final int VIDEO_EIS = 0xD724; // 55076
    public static final int VIDEO_LOOP_RECORDING = 0xD725; // 55077
    public static final int FAST_MOTION_MOVIE = 0xD726; // 55078
    public static final int WIND_NOISE_REDUCTION = 0xD727;
    public static final int CAPTURE_IN_VIDEO_RECORD = 0xD72A;
    // 20180815
    public static final int CAMERA_SWITCH = 0xD733;


    //JIRA BSP-1906
    public static final int CAMERA_DATE_TIMEZONE = 0xD83E;
    //是否进入pv页面
    public static final int DEFAULT_TO_PREVIEW = 0xD72C;
    //pb 分段获取文件
    public static final int CAMERA_PB_LIMIT_NUMBER = 0xD83F; //55359


    //-------------------------
    // JIRA ICOM-2246
    public static final int TIMELAPSE_VIDEO_SIZE_LIST_MASK = 0xD7FB;
    //Multiple  sensor
    public static final int MULTIPLE_CAMERA_SENSOR = 0xD729;

    // akamefi202: new properties
    public static final int PHOTO_MODE = 0xD77C;
    public static final int PHOTO_VIDEO_QUALITY = 0x5004;
    public static final int PHOTO_VIDEO_ISO = 0x500F;
    public static final int PHOTO_VIDEO_METERING = 0x500B;
    public static final int PHOTO_VIDEO_TIMELAPSE_DURATION = 0x501A;
    public static final int PHOTO_VIDEO_TIMELAPSE_INTERVAL = 0x501B;
    public static final int VIDEO_MODE = 0xD770;
    public static final int VIDEO_AUTO_LOW_LIGHT = 0xD77E;
    public static final int PHOTO_VIDEO_FOV = 0xD77D;
    public static final int GENERAL_AUDIO_INPUT = 0xD779;
    public static final int GENERAL_DISPLAY_BRIGHTNESS = 0xD77B;
    public static final int GENERAL_DEVICE_SOUND = 0xD77A;
    public static final int GENERAL_FRONT_DISPLAY = 0xD775;
    public static final int GENERAL_MAIN_STATUS_LED = 0xD776;
    public static final int GENERAL_BATTERY_STATUS_LED = 0xD777;
    public static final int GENERAL_LANGUAGE = 0xD778;
    public static final int GENERAL_COLOR_EFFECT = 0x5017;
    public static final int GENERAL_DATE_FORMAT = 0xD772;
    public static final int GENERAL_WIFI_FREQUENCY = 0xD77F;
    public static final int SD_CARD_INFO = 0xD810;
    public static final int FACTORY_RESET = 0xD774;

}
