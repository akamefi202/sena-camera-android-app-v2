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

#ifndef __ICATCH_CAMERA_PROPERTY_H__
#define __ICATCH_CAMERA_PROPERTY_H__

#include <vector>
// #include <string>

#include "type/ICatchVideoFormat.h"

#include "type/ICatchCamDateStamp.h"
#include "type/ICatchCamBurstNumber.h"
#include "type/ICatchCamWhiteBalance.h"
#include "type/ICatchCamCaptureDelay.h"
#include "type/ICatchCamLightFrequency.h"
#include "type/ICatchCamProperty.h"

namespace com
{
    namespace icatchtek
    {
        namespace control
        {

            class ICatchCameraProperty
            {
            public:
                virtual ~ICatchCameraProperty() {}

            public:
                virtual int setProperty(int cmd, uint8_t *buffer, int dataSize) = 0;
                virtual int getProperty(int cmd, uint8_t *buffer, int bufferSize, int &dataSize) = 0;

                virtual int setPropertyValue(int propId, unsigned int value) = 0;
                virtual int setPropertyValue(int propId, unsigned int value, int timeoutInSecs) = 0;
                virtual int getCurrentPropertyValue(int propId, unsigned int &value) = 0;
                virtual int getCurrentPropertyValue(int propId, unsigned int &value, int timeoutInSecs) = 0;
                virtual int getSupportedPropertyValues(int propId, std::vector<unsigned int> &values) = 0;
                virtual int getSupportedPropertyValues(int propId, std::vector<unsigned int> &values, int timeoutInSecs) = 0;

                virtual int setPropertyValue(int propId, std::string value) = 0;
                virtual int setPropertyValue(int propId, std::string value, int timeoutInSecs) = 0;
                virtual int getCurrentPropertyValue(int propId, std::string &value) = 0;
                virtual int getCurrentPropertyValue(int propId, std::string &value, int timeoutInSecs) = 0;
                virtual int getSupportedPropertyValues(int propId, std::vector<std::string> &values) = 0;
                virtual int getSupportedPropertyValues(int propId, std::vector<std::string> &values, int timeoutInSecs) = 0;

                virtual int setPropertyValue(int propId, const uint8_t *byteValue, int valueSize, int timeoutInSecs) = 0;
                virtual int getCurrentPropertyValue(int propId, uint8_t *byteValue, int bufferSize, int &valueSize, int timeoutInSecs) = 0;

                virtual int setWhiteBalance(unsigned int value) = 0;
                virtual int getSupportedWhiteBalances(std::vector<unsigned int> &wbs) = 0;
                virtual int getCurrentWhiteBalance(unsigned int &wb) = 0;

                virtual int setCaptureDelay(unsigned int value) = 0;
                virtual int getSupportedCaptureDelays(std::vector<unsigned int> &cds) = 0;
                virtual int getCurrentCaptureDelay(unsigned int &cd) = 0;

                virtual int setImageSize(std::string value) = 0;
                virtual int getSupportedImageSizes(std::vector<std::string> &imageSizes) = 0;
                virtual int getCurrentImageSize(std::string &is) = 0;

                virtual int setVideoSize(std::string value) = 0;
                virtual int getSupportedVideoSizes(std::vector<std::string> &videoSizes) = 0;
                virtual int getCurrentVideoSize(std::string &vs) = 0;

                virtual int setLightFrequency(unsigned int value) = 0;
                virtual int getSupportedLightFrequencies(std::vector<unsigned int> &lfs) = 0;
                virtual int getCurrentLightFrequency(unsigned int &lf) = 0;

                virtual int setBurstNumber(unsigned int value) = 0;
                virtual int getSupportedBurstNumbers(std::vector<unsigned int> &bns) = 0;
                virtual int getCurrentBurstNumber(unsigned int &bn) = 0;

                virtual int setDateStamp(unsigned int value) = 0;
                virtual int getSupportedDateStamps(std::vector<unsigned int> &dss) = 0;
                virtual int getCurrentDateStamp(unsigned int &ds) = 0;

                virtual int getCurrentSDCardInfo(unsigned int &sci) = 0;

                virtual int getSupportedColorEffect(std::vector<unsigned int> &cfs) = 0;
                virtual int setColorEffect(unsigned int value) = 0;
                virtual int getCurrentColorEffect(unsigned int &cf) = 0;

                virtual int getSupportedISO(std::vector<unsigned int> &isos) = 0;
                virtual int setISO(unsigned int value) = 0;
                virtual int getCurrentISO(unsigned int &iso) = 0;

                virtual int getSupportedMetering(std::vector<unsigned int> &mts) = 0;
                virtual int setMetering(unsigned int value) = 0;
                virtual int getCurrentMetering(unsigned int &mt) = 0;

                virtual int getSupportedQuality(std::vector<unsigned int> &qls) = 0;
                virtual int setQuality(unsigned int value) = 0;
                virtual int getCurrentQuality(unsigned int &ql) = 0;

                virtual int getSupportedTimeLapseIntervals(std::vector<unsigned int> &tlsis) = 0;
                virtual int setTimeLapseInterval(unsigned int value) = 0;
                virtual int getCurrentTimeLapseInterval(unsigned int &tlsi) = 0;

                virtual int getSupportedTimeLapseDurations(std::vector<unsigned int> &tlsds) = 0;
                virtual int setTimeLapseDuration(unsigned int value) = 0;
                virtual int getCurrentTimeLapseDuration(unsigned int &tlsd) = 0;

                virtual int getCurrentUpsideDown(unsigned int &upsd) = 0;
                virtual int setUpsideDown(unsigned int upsd) = 0;

                virtual int getCurrentSlowMotion(unsigned int &sm) = 0;
                virtual int setSlowMotion(unsigned int sm) = 0;

                virtual int getMaxZoomRatio(unsigned int &maxRatio) = 0;
                virtual int getCurrentZoomRatio(unsigned int &curRatio) = 0;

                virtual int getSupportedProperties(std::vector<unsigned int> &caps) = 0;
                virtual bool supportProperty(unsigned int property) = 0;

                virtual int getSupportedSeamlesses(std::vector<unsigned int> &seamlesses) = 0;
                virtual int setSeamless(unsigned int value) = 0;
                virtual int getCurrentSeamless(unsigned int &seamless) = 0;

                virtual int getSupportedStreamingInfos(std::vector<com::icatchtek::reliant::ICatchVideoFormat> &infos) = 0;
                virtual int getCurrentStreamingInfo(com::icatchtek::reliant::ICatchVideoFormat &info) = 0;
                virtual int setStreamingInfo(com::icatchtek::reliant::ICatchVideoFormat info) = 0;

                virtual int getPreviewCacheTime(unsigned int &cacheTime) = 0;
                virtual int getNumberOfSensors(unsigned int &sensorCount) = 0;
                virtual bool checkCameraCapabilities(unsigned int featureID) = 0;
            };

        }
    }
}

#endif
