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

#ifndef __ICATCH_CAMERA_ASSIST_H__
#define __ICATCH_CAMERA_ASSIST_H__

#include <string>
#include "ICatchICameraListener.h"

namespace com{ namespace icatchtek{ namespace control{
    class ICatchCameraSession;
}}}

namespace com{ namespace icatchtek{ namespace control{

class ICatchCameraAssist
{
public:
    virtual bool supportLocalPlay(std::string file) = 0;

    virtual int updateFw(shared_ptr<com::icatchtek::control::ICatchCameraSession> session, std::string fwFile) = 0;
    virtual void notifyUpdateFw() = 0;

    virtual int simpleConfig(std::string essid, std::string passwd, std::string key, std::string ipAddr, std::string macAddr, int timeout) = 0;
    virtual int simpleConfigGet(std::string& content) = 0;
    virtual int simpleConfigCancel() = 0;

public:
    /* wake up camera */
    virtual int wakeUpCamera(std::string macAddress) = 0;

    /* start or stop device scan */
    virtual bool startDeviceScan() = 0;
    virtual bool stopDeviceScan() = 0;

    /* init device, before using p2p connection */
    virtual bool deviceInit(std::string ipAddr) = 0;

    /* get camera udid */
    virtual string getCameraUDID(shared_ptr<com::icatchtek::control::ICatchCameraSession> session, std::string ipAddr) = 0;

public:
    /* listener add or remove api */
    static int addEventListener(int icatchEvtID, shared_ptr<com::icatchtek::control::ICatchICameraListener> listener, bool forAllSession);
    static int removeEventListener(int icatchEvtID, shared_ptr<com::icatchtek::control::ICatchICameraListener> listener, bool forAllSession);
};

}}}

#endif

