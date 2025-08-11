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

#ifndef __ICATCH_CAM_PROPERTY_H__
#define __ICATCH_CAM_PROPERTY_H__

namespace com{ namespace icatchtek{ namespace control{

enum ICatchCamProperty
{
    ICH_CAM_WHITE_BALANCE               = 0x5005,
    ICH_CAM_CAPTURE_DELAY               = 0x5012,
    ICH_CAM_IMAGE_SIZE                  = 0x5003,
    ICH_CAM_VIDEO_SIZE                  = 0xD605,
    ICH_CAM_LIGHT_FREQUENCY             = 0xD606,
    ICH_CAM_BATTERY_LEVEL               = 0x5001,
    ICH_CAM_PRODUCT_NAME                = 0x501E,
    ICH_CAM_FW_VERSION                  = 0x501F,
    ICH_CAM_BURST_NUMBER                = 0x5018,
    ICH_CAM_DATE_STAMP                  = 0xD607,
    ICH_CAM_UPSIDE_DOWN                 = 0xd614,
    ICH_CAM_SLOW_MOTION                 = 0xd615,
    ICH_CAM_DIGITAL_ZOOM                = 0x5016,
    ICH_CAM_TIMELAPSE_STILL             = 0x501b,
    ICH_CAM_TIMELAPSE_VIDEO             = 0xd611,

    ICH_CAM_CAP_GET_NUMBER_OF_SENSORS   = 0xD72B,
    ICH_CAM_CAP_GET_CAMERA_CAPABILITIES = 0xD72C,

    ICH_CAM_UNDEFINED                   = 0xFFFF,
};

}}}

#endif
