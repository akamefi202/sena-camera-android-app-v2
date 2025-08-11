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

#ifndef __ICATCH_CAMERA_CONTROL__
#define __ICATCH_CAMERA_CONTROL__

#include <vector>
#include <memory>

#include "ICatchICameraListener.h"
#include "type/ICatchFile.h"
#include "type/ICatchCamVideoRecordStatus.h"

namespace com
{
    namespace icatchtek
    {
        namespace control
        {

            class ICatchCameraControl
            {
            public:
                virtual ~ICatchCameraControl() {}

            public:
                virtual int addEventListener(int eventID, std::shared_ptr<com::icatchtek::control::ICatchICameraListener> listener) = 0;
                virtual int removeEventListener(int eventID, std::shared_ptr<com::icatchtek::control::ICatchICameraListener> listener) = 0;
                virtual int addCustomEventListener(unsigned int customEvtID, std::shared_ptr<com::icatchtek::control::ICatchICameraListener> listener) = 0;
                virtual int removeCustomEventListener(unsigned int customEvtID, std::shared_ptr<com::icatchtek::control::ICatchICameraListener> listener) = 0;

            public:
                virtual int getCurrentBatteryLevel(unsigned int &bl) = 0;

                virtual int getSupportedModes(std::vector<unsigned int> &modes) = 0;
                virtual int getCurrentCameraMode(unsigned int &currMode) = 0;
                virtual int changePreviewMode(unsigned int previewMode) = 0;

                virtual bool supportVideoPlayback() = 0;

                virtual int startTimeLapse() = 0;
                virtual int stopTimeLapse() = 0;

                virtual int startMovieRecord() = 0;
                virtual int stopMovieRecord() = 0;
                virtual int stopMovieRecord(int timeoutInSecs) = 0;

                virtual int capturePhoto() = 0;
                virtual int capturePhoto(int timeoutInSecs) = 0;
                virtual int triggerCapturePhoto() = 0;

                virtual int isSDCardExist(bool &exist) = 0;
                virtual int getFreeSpaceInImages(unsigned int &count) = 0;
                virtual int getRemainRecordingTime(unsigned int &secs) = 0;

                virtual int formatStorage() = 0;
                virtual int formatStorage(int timeoutInSecs) = 0;

                virtual int setAudioMute() = 0;
                virtual int setAudioUnMute() = 0;

                virtual int setEventTrigger() = 0;
                virtual int setSeamless() = 0;
                virtual int getVideoRecordStatus(com::icatchtek::control::ICatchCamVideoRecordStatus &videoRecordStatus) = 0;

                virtual int zoomIn() = 0;
                virtual int zoomOut() = 0;

                virtual int pan(int xshift, int yshfit) = 0;
                virtual int panReset() = 0;

                virtual int toStandbyMode() = 0;
                virtual bool setFileProtection(std::shared_ptr<com::icatchtek::reliant::ICatchFile> file, unsigned int newprot) = 0;

                virtual int setExtensionUnitID(int xuID) = 0;
                virtual int extensionUnitGetLength(int cmd, int &length) = 0;
                virtual int extensionUnitSet(int cmd, uint8_t *buffer, int dataSize) = 0;
                virtual int extensionUnitGet(int cmd, uint8_t *buffer, int bufferSize, int &dataSize) = 0;

                virtual int setVideoStreamInterface(int interfaceID) = 0;
            };

        }
    }
}

#endif
