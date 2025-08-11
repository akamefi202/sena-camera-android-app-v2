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

#ifndef __ICATCH_CAMERA_UTIL_H__
#define __ICATCH_CAMERA_UTIL_H__

#include <vector>
#include <string>

#include "type/ICatchCamVideoSize.h"
#include "type/ICatchCamWhiteBalance.h"
#include "type/ICatchCamCaptureDelay.h"
#include "type/ICatchCamBurstNumber.h"
#include "type/ICatchCamLightFrequency.h"
#include "type/ICatchCamDateStamp.h"

namespace com { namespace icatchtek{ namespace control{
class ICatchCameraUtil
{
public:
    static int convertImageSizes(std::vector<std::string> sizes, std::vector<unsigned int>& imageSizes);
    static int convertVideoSizes(std::vector<std::string> sizes, std::vector<com::icatchtek::control::ICatchCamVideoSize>& videoSizes);

    static int convertImageSize(std::string size, unsigned int& imageSize);
    static int convertVideoSize(std::string size, com::icatchtek::control::ICatchCamVideoSize& videoSize);

    static int convertWhiteBalances(std::vector<unsigned int> values, std::vector<com::icatchtek::control::ICatchCamWhiteBalance>& wbs);
    static int convertWhiteBalance(unsigned int value, com::icatchtek::control::ICatchCamWhiteBalance& wb);

    static int convertCaptureDelays(std::vector<unsigned int> values, std::vector<com::icatchtek::control::ICatchCamCaptureDelay>& cds);
    static int convertCaptureDelay(unsigned int value, com::icatchtek::control::ICatchCamCaptureDelay& cd);

    static int convertBurstNumbers(std::vector<unsigned int> values, std::vector<com::icatchtek::control::ICatchCamBurstNumber>& bns);
    static int convertBurstNumber(unsigned int value, com::icatchtek::control::ICatchCamBurstNumber& bn);

    static int convertLightFrequencies(std::vector<unsigned int> values, std::vector<com::icatchtek::control::ICatchCamLightFrequency>& lfs);
    static int convertLightFrequency(unsigned int value, com::icatchtek::control::ICatchCamLightFrequency& lf);

    static int convertDateStamps(std::vector<unsigned int> values, std::vector<com::icatchtek::control::ICatchCamDateStamp>& dss);
    static int convertDateStamp(unsigned int value, com::icatchtek::control::ICatchCamDateStamp& ds);
#if 0
    static int decodeAAC(unsigned char* inputData, int frameSize, unsigned char* outputData, int bufferSize);
    static int decodeJPEG(unsigned char* inputData, int frameSize, unsigned char* outputData, int bufferSize);
#endif
    static int getImageResolution(std::string size, unsigned int& width, unsigned int& height);
};
}}}
#endif

