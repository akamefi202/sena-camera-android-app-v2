/**************************************************************************
 *
 *         Copyright (c) 2014 by iCatch Technology Co., Ltd.
 *
 *  This software is copyrighted by and is the property of Sunplus
 *  Technology Co., Ltd. All rights are reserved by Sunplus Technology
 *  Co., Ltd. This software may only be used in accordance with the
 *  corresponding license agreement. Any unauthorized use, duplication,
 *  distribution, or disclosure of this software is expressly forbidden.
 *
 *  This Copyright notice MUST not be removed or modified without prior
 *  written consent of Sunplus Technology Co., Ltd.
 *
 *  Sunplus Technology Co., Ltd. reserves the right to modify this
 *  software without notice.
 *
 *  Sunplus Technology Co., Ltd.
 *  19, Innovation First Road, Science-Based Industrial Park,
 *  Hsin-Chu, Taiwan, R.O.C.
 *
 *  Author: peng.tan
 *  Email:  peng.tan@sunmedia.com.cn
 *
 **************************************************************************/

#ifndef __ICATCH_CAM_EVENT_TYPE_H__
#define __ICATCH_CAM_EVENT_TYPE_H__

namespace com { namespace icatchtek{ namespace control{

typedef enum ICatchCamEventID
{
    /*-----------------------------------------------------
      * file events
      */
    ICH_CAM_EVENT_FILE_ADDED                        = 0x01,
    ICH_CAM_EVENT_FILE_REMOVED                      = 0x02,
    ICH_CAM_EVENT_FILE_INFO_CHANGED                 = 0x03,

    /*-----------------------------------------------------
      * sd card events
      */
    ICH_CAM_EVENT_SDCARD_FULL                       = 0x11,
    ICH_CAM_EVENT_SDCARD_ERROR                      = 0x12,
    ICH_CAM_EVENT_SDCARD_REMOVED                    = 0x13,
    ICH_CAM_EVENT_SDCARD_IN                         = 0x14,
    ICH_CAM_EVENT_SDCARD_INFO_CHANGED               = 0x15,

    /*-----------------------------------------------------
      * other events
      */
    ICH_CAM_EVENT_VIDEO_ON                          = 0x21,
    ICH_CAM_EVENT_VIDEO_OFF                         = 0x22,
    ICH_CAM_EVENT_CAPTURE_COMPLETE                  = 0x23,
    ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED             = 0x24,

    /*-----------------------------------------------------
      * device events
      */
    ICH_CAM_EVENT_DEVICE_INFO_CHANGED               = 0x31,

    ICH_CAM_EVENT_WHITE_BALANCE_PROP_CHANGED        = 0x32,
    ICH_CAM_EVENT_CAPTURE_DELAY_PROP_CHANGED        = 0x33,
    ICH_CAM_EVENT_IMAGE_SIZE_PROP_CHANGED           = 0x34,
    ICH_CAM_EVENT_VIDEO_SIZE_PROP_CHANGED           = 0x35,
    ICH_CAM_EVENT_LIGHT_FREQUENCY_PROP_CHANGED      = 0x36,
    ICH_CAM_EVENT_BURST_NUMBER_PROP_CHANGED         = 0x37,

    /*------------------------------------------------------
      * SDK's inner events
      */
    ICH_CAM_EVENT_VIDEO_DOWNLOAD_PROGRESS           = 0x49,
    ICH_CAM_EVENT_CONNECTION_DISCONNECTED           = 0x4A,

    ICH_CAM_EVENT_CONNECTION_INITIALIZE_SUCCEED     = 0x4B,
    ICH_CAM_EVENT_CONNECTION_INITIALIZE_FAILED      = 0x4C,

    /*-----------------------------------------------------
    * TimeLapse events
    */
    ICH_CAM_EVENT_TIMELAPSE_STOP                    = 0x51,
    ICH_CAM_EVENT_CAPTURE_START                     = 0x52,

    /*-----------------------------------------------------
    * DeviceScan events
    */
    ICH_CAM_EVENT_DEVICE_SCAN_ADD                   = 0x55,

    /*-----------------------------------------------------
    *  fw update event
    */
    ICH_CAM_EVENT_FW_UPDATE_CHECK                   = 0x60,
    ICH_CAM_EVENT_FW_UPDATE_COMPLETED               = 0x61,
    ICH_CAM_EVENT_FW_UPDATE_POWEROFF                = 0x62,
    ICH_CAM_EVENT_FW_UPDATE_CHKSUMERR               = 0x63,
    ICH_CAM_EVENT_FW_UPDATE_NG                      = 0x64,

    /**
     * Video record time change.
     */
    ICH_CAM_EVENT_VIDREC_TIME_CHANGE                = 0x65,

    /*-----------------------------------------------------
    *  PIV file auto download
    */
    ICH_CAM_EVENT_FILE_DOWNLOAD                     = 0x67,

    /*-----------------------------------------------------
    *  Video thumbnail
    */
    ICH_CAM_EVENT_VIDEO_THUMB_READY                 = 0x68,
    ICH_CAM_EVENT_VIDEO_THUMB_DONE                  = 0x69,
    ICH_CAM_EVENT_VIDEO_TRIM_DONE                   = 0x6a,

    ICH_CAM_EVENT_SD_CARD_OUT                       = 0x80,
    ICH_CAM_EVENT_SD_CARD_IN                        = 0x81,
    ICH_CAM_EVENT_SD_CARD_ERR                       = 0x82,
    ICH_CAM_EVENT_SD_CARD_LOCKED                    = 0x83,
    ICH_CAM_EVENT_SD_CARD_MEMORY_FULL               = 0x84,
    ICH_CAM_EVENT_INSUFFICIENT_DISK_SPACE           = 0x85,
    ICH_CAM_EVENT_SD_CARD_SPEED_TOO_SLOW            = 0x86,

    /* ---------------------------------------------------
     * other...
     */
    ICH_CAM_EVENT_PV_STREAM_RESTART                 = 0x90,

    ICH_CAM_EVENT_UNDEFINED                         = 0xFF,
} ICatchCamEventID;

}}}

#endif

