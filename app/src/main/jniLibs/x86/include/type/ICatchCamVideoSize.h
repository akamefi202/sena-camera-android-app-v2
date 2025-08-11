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

#ifndef __ICATCH_CAM_VIDEO_SIZE_H__
#define __ICATCH_CAM_VIDEO_SIZE_H__

namespace com { namespace icatchtek{ namespace control{

enum ICatchCamVideoSize
{
    ICH_CAM_VIDEO_SIZE_UNDEFINED        = 0x00,        /* for andorid, pls do not modify value. */
    ICH_CAM_VIDEO_SIZE_720P_30FPS       = 0x01,
    ICH_CAM_VIDEO_SIZE_720P_60FPS       = 0x02,
    ICH_CAM_VIDEO_SIZE_1080P_30FPS      = 0x03,
    ICH_CAM_VIDEO_SIZE_1080P_60FPS      = 0x04,

    ICH_CAM_VIDEO_SIZE_720P_120FPS      = 0x05,
    ICH_CAM_VIDEO_SIZE_1440P_30FPS      = 0x06,
    ICH_CAM_VIDEO_SIZE_960P_60FPS       = 0x07,
    ICH_CAM_VIDEO_SIZE_VGA_120FPS       = 0x08,
    ICH_CAM_VIDEO_SIZE_QVGA_240FPS      = 0x09,
    ICH_CAM_VIDEO_SIZE_FULL_30FPS       = 0x0a,        /* from fw, 2048 * 1536 */
    ICH_CAM_VIDEO_SIZE_640_360_240FPS   = 0x10,

    ICH_CAM_VIDEO_SIZE_4K_15FPS         = 0x11,
    ICH_CAM_VIDEO_SIZE_4K_30FPS         = 0x12,
    ICH_CAM_VIDEO_SIZE_2K7_1_25FPS      = 0x13,
    ICH_CAM_VIDEO_SIZE_2K7_2_25FPS      = 0x14,
    ICH_CAM_VIDEO_SIZE_1280P_30FPS      = 0x15,
    ICH_CAM_VIDEO_SIZE_1280P_60FPS      = 0x16,
    ICH_CAM_VIDEO_SIZE_960P_1_30FPS     = 0x17,
    ICH_CAM_VIDEO_SIZE_960P_2_30FPS     = 0x18,
    ICH_CAM_VIDEO_SIZE_640P_15FPS       = 0x19,
    ICH_CAM_VIDEO_SIZE_HD_15FPS         = 0x1a,
    ICH_CAM_VIDEO_SIZE_UNDEFINED1       = 0xFF
};

}}}

#endif
