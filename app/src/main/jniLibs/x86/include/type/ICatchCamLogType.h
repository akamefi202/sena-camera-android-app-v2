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

#ifndef __ICATCH_CAM_LOG_TYPE__
#define __ICATCH_CAM_LOG_TYPE__

/* -----------------------------------------------------------------------------
 * class &namespace definitions
 */
namespace com{ namespace icatchtek{ namespace control {

/**
 * ICatch sdk's log type macros
 *
 * @author peng.tan
 *
 */
typedef enum ICatchCamLogType
{
    /** type for control log */
    ICH_CAM_LOG_TYPE_COMMON      = 0x00,
    ICH_CAM_LOG_TYPE_THIRDLIB    = 0x01,
} ICatchCamLogType;

}}}

#endif

