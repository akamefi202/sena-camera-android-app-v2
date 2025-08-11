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

#ifndef __ICATCH_CAM_VIDEO_RECORD_STATUS_H__
#define __ICATCH_CAM_VIDEO_RECORD_STATUS_H__

#include <string>

namespace com { namespace icatchtek{ namespace control{

#define CAM_SDCARD_STATUS_PV   0
#define CAM_SDCARD_STATUS_REC  1

class ICatchCamVideoRecordStatus
{
public:
    ICatchCamVideoRecordStatus();

public:
    int getCardStatus();
    void setCardStatus(int cardStatus);

    int getYears();
    void setYears(int years);

    int getMonths();
    void setMonths(int months);

    int getDays();
    void setDays(int days);

    int getHours();
    void setHours(int hours);

    int getMinutes();
    void setMinutes(int minutes);

    int getSeconds();
    void setSeconds(int seconds);

public:
    std::string toString();
    bool fromString(ICatchCamVideoRecordStatus& recordStatus, std::string attributes);

private:
    int cardStatus;
    int years;
    int months;
    int days;
    int hours;
    int minutes;
    int seconds;
};

}}}

#endif

