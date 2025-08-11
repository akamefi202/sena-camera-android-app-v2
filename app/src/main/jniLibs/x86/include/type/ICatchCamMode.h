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

#ifndef __ICATCH_CAM_MODE_H__
#define __ICATCH_CAM_MODE_H__

namespace com { namespace icatchtek{ namespace control{

enum ICatchCamMode {
	ICH_CAM_MODE_VIDEO_OFF			= 0x0001,	//"VideoModeOff"
	ICH_CAM_MODE_SHARED 			= 0x0002,	//"ShareMode"
	ICH_CAM_MODE_CAMERA 			= 0x0003,	//"CameraMode"
	ICH_CAM_MODE_IDLE				= 0x0004,	//"IdleMode"
	ICH_CAM_MODE_TIMELAPSE_STILL	= 0x0007,	//"TimeLapse Still"
	ICH_CAM_MODE_TIMELAPSE_VIDEO	= 0x0008,	//"TimeLapse Video"
	ICH_CAM_MODE_TIMELAPSE_STILL_OFF= 0x0009,	//"Timelapse Still OFF"
	ICH_CAM_MODE_TIMELAPSE_VIDEO_OFF= 0x000A,	//"TImelapse Video OFF"
	ICH_CAM_MODE_VIDEO_ON			= 0x0011,	//"VideoModeOn"
	ICH_CAM_MODE_VIDEO				= 0x002A,	//"Macro comes from camera mode."
	ICH_CAM_MODE_TIMELAPSE			= 0x002B,	//"Macro comes from camera mode."
	ICH_CAM_MODE_UNDEFINED			= 0xFFBF,	//"Undefined mode"
};

}}}

#endif

