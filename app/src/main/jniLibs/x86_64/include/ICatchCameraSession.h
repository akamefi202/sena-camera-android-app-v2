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

#ifndef __ICATCH_CAMERA_SESSION_H__
#define __ICATCH_CAMERA_SESSION_H__

#include <string>
#include <memory>

#include "WindowsApi.h"
#include "type/transport/ICatchITransport.h"
#include "type/ICatchCamEventID.h"

#include "ICatchICameraListener.h"
#include "ICatchCameraInfo.h"
#include "ICatchCameraState.h"
#include "ICatchCameraControl.h"
#include "ICatchCameraProperty.h"
#include "ICatchCameraPlayback.h"

#include "ICatchCameraConfig.h"
#include "ICatchCameraAssist.h"

namespace com{ namespace icatchtek{ namespace control{ namespace core{
    class ICatchCameraSession_pimpl;
}}}}

using namespace com::icatchtek::control;

namespace com{ namespace icatchtek{ namespace control{

class ICAT_API ICatchCameraSession
{
public:
    static shared_ptr<com::icatchtek::control::ICatchCameraConfig> getCameraConfig(shared_ptr<com::icatchtek::reliant::ICatchITransport> transport);
    static shared_ptr<com::icatchtek::control::ICatchCameraAssist> getCameraAssist(shared_ptr<com::icatchtek::reliant::ICatchITransport> transport);

public:
    static shared_ptr<ICatchCameraSession> createSession(int sessionID);

public:
    ICatchCameraSession(int sessionID);
    ~ICatchCameraSession();

public:
    /* get the id of this session */
    int getSessionID();

    /* prepare of destroy session */
    int prepareSession(shared_ptr<com::icatchtek::reliant::ICatchITransport> transport);
    bool destroySession();

    /* to check whether the connection status between app and camera. */
    bool checkConnection();

    /* get feature client */
    shared_ptr<com::icatchtek::control::ICatchCameraInfo> getInfoClient();
    shared_ptr<com::icatchtek::control::ICatchCameraState> getStateClient();
    shared_ptr<com::icatchtek::control::ICatchCameraControl> getControlClient();
    shared_ptr<com::icatchtek::control::ICatchCameraProperty> getPropertyClient();
    shared_ptr<com::icatchtek::control::ICatchCameraPlayback> getPlaybackClient();

private:
    int                                 sessionID;
    shared_ptr<com::icatchtek::control::ICatchCameraInfo>        infoClient;
    shared_ptr<com::icatchtek::control::ICatchCameraState>       stateClient;
    shared_ptr<com::icatchtek::control::ICatchCameraControl>     controlClient;
    shared_ptr<com::icatchtek::control::ICatchCameraProperty>    propertyClient;
    shared_ptr<com::icatchtek::control::ICatchCameraPlayback>    playbackClient;

private:
    friend class ICatchCameraAssist;
    com::icatchtek::control::core::ICatchCameraSession_pimpl* session_pimpl;

private:
    ICatchCameraSession(ICatchCameraSession& session);
    ICatchCameraSession& operator = (const ICatchCameraSession&);
};

}}}

#endif
