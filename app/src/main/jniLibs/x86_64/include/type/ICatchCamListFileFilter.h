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

#ifndef __ICATCH_CAM_LIST_FILE_FILTER_H__
#define __ICATCH_CAM_LIST_FILE_FILTER_H__

namespace com { namespace icatchtek{ namespace control{

class ICatchCamListFileFilter
{
public:
    static const int ICH_OFC_TYPE_VIDEO;
    static const int ICH_OFC_TYPE_IMAGE;
    static const int ICH_OFC_TYPE_MEDIA;
    static const int ICH_OFC_TYPE_EMERGENCY_VIDEO;
    static const int ICH_OFC_TYPE_EMERGENCY_IMAGE;
    static const int ICH_OFC_TYPE_EMERGENCY_MEDIA;
    static const int ICH_OFC_FILE_TYPE_ALL_MEDIA;

public:
    static const int ICH_SORT_TYPE_ASCENDING;
    static const int ICH_SORT_TYPE_DESCENDING;

public:
    static const int ICH_TAKEN_BY_ALL_SENSORS;
    static const int ICH_TAKEN_BY_FRONT_SENSOR;
    static const int ICH_TAKEN_BY_BACK_SENSOR;
    static const int ICH_COMPOSITE_FILE_TAKEN_BY_ALL_SENSORS;
};

}}}

#endif
