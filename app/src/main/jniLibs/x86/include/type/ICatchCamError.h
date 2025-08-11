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

#ifndef __ICATCH_CAM_ERROR_H__
#define __ICATCH_CAM_ERROR_H__

#include "type/ICatchError.h"

namespace com{ namespace icatchtek{ namespace control{

enum ICatchCamError
{
    /* control error code starts from -300 */
    ICH_CAM_BATTERY_LEVEL_NOT_SUPPORTED         = -300,

    ICH_CAM_MODE_NOT_SUPPORT                    = -301,
    ICH_CAM_MODE_SET_ILLEGAL                    = -302,
    ICH_CAM_MODE_CAMERA_BUSY                    = -303,
    ICH_CAM_MODE_PTP_CLIENT_INVALID             = -304,
    ICH_CAM_MODE_CHANGE_FAILED                  = -305,

    ICH_CAM_WB_NOT_SUPPORTED                    = -306,
    ICH_CAM_WB_GET_FAILED                       = -307,
    ICH_CAM_WB_SET_FAILED                       = -308,

    ICH_CAM_CAP_DELAY_NOT_SUPPORTED             = -309,
    ICH_CAM_CAP_DELAY_GET_FAILED                = -310,
    ICH_CAM_CAP_DELAY_SET_FAILED                = -311,

    ICH_CAM_IMAGE_SIZE_NOT_SUPPORTED            = -312,
    ICH_CAM_IMAGE_SIZE_GET_FAILED               = -313,
    ICH_CAM_IMAGE_SIZE_SET_FAILED               = -314,

    ICH_CAM_VIDEO_SIZE_NOT_SUPPORTED            = -315,
    ICH_CAM_VIDEO_SIZE_GET_FAILED               = -316,
    ICH_CAM_VIDEO_SIZE_SET_FAILED               = -317,

    ICH_CAM_LIGHT_FREQ_NOT_SUPPORTED            = -318,
    ICH_CAM_LIGHT_FREQ_GET_FAILED               = -319,
    ICH_CAM_LIGHT_FREQ_SET_FAILED               = -320,

    ICH_CAM_BURST_NUMBER_NOT_SUPPORTED          = -321,
    ICH_CAM_BURST_NUMBER_GET_FAILED             = -322,
    ICH_CAM_BURST_NUMBER_SET_FAILED             = -323,

    ICH_CAM_CAPTURE_ERROR                       = -324,
    ICH_CAM_STORAGE_FORMAT_ERROR                = -325,

    ICH_CAM_IMAGE_SIZE_FORMAT_ERROR             = -326,
    ICH_CAM_VIDEO_SIZE_FORMAT_ERROR             = -327,

    ICH_CAM_SD_CARD_NOT_EXIST                   = -328,

    /*-----------------------------------------------
      * !!!Add new error code there.
      * ----------------------------------------------*/
    ICH_CAM_FREE_SPACE_IN_IMAGE_NOT_SUPPORTED   = -329,
    ICH_CAM_REMAIN_RECORD_TIME_NOT_SUPPORTED    = -330,
    ICH_CAM_MTP_GET_OBJECTS_ERROR               = -331,

    ICH_CAM_PROP_NOT_EXIST                      = -334,
    ICH_CAM_PROP_TYPE_ERROR                     = -335,
    ICH_CAM_PROP_VALUE_ERROR                    = -336,
    ICH_CAM_PROP_PARSE_ERROR                    = -337,

    ICH_CAM_SESSION_PASSWORD_ERR                = -347,
    ICH_CAM_PTP_INIT_FAILED                     = -348,
    ICH_CAM_TUTK_INIT_FAILED                    = -349,
    ICH_CAM_WAIT_TIME_OUT                       = -350,
};

/**--------------------------------------------------------------
 * check icatch value, return bool or icatch value
 */
#define CHECK_ICHE_RET_I(ret)       \
do {                                \
    if (ret != com::icatchtek::reliant::ICH_SUCCEED) {      \
        ICATCH_API_OUT();           \
        return ret;                 \
    }                               \
} while(0);

#define CHECK_ICHE_RET_B(ret)       \
do {                                \
    if (ret != com::icatchtek::reliant::ICH_SUCCEED) {      \
        ICATCH_API_OUT();           \
        return false;               \
    }                               \
} while(0);

/**--------------------------------------------------------------
 * check bool value, return bool or icatch value(ICH_UNKNOWN_ERROR)
 */
#define CHECK_BOOL_RET_I(ret)       \
do {                                \
    if (ret != true) {              \
        ICATCH_API_OUT();           \
        return com::icatchtek::reliant::ICH_UNKNOWN_ERROR;   \
    }                               \
} while(0);

#define CHECK_BOOL_RET_B(ret)       \
do {                                \
    if (ret != true) {              \
        ICATCH_API_OUT();           \
        return false;               \
    }                               \
} while(0);

/**---------------------------------------------------
 * check icatch value, return bool or icatch value
 */
#define RTP_CHECK_RET_ICH(ret)      \
do {                                \
    if (ret != com::icatchtek::reliant::ICH_SUCCEED) {      \
        ICATCH_API_OUT();           \
        return ret;                 \
    }                               \
} while(0);

#define RTP_CHECK_RET_BOOL(ret)     \
do {                                \
    if (ret != true) {              \
        ICATCH_API_OUT();           \
        return false;               \
    }                               \
} while(0);

}}}

#endif
