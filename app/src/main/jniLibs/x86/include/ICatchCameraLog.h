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

#ifndef __ICATCH_CONTROL_LOG__
#define __ICATCH_CONTROL_LOG__

#include <string>
#include <memory>
#include "WindowsApi.h"

namespace com { namespace icatchtek{ namespace control{

/**
 * This logger cass contains methods which the customer could used to denable/disable the sdk's debug mode,
 * set log level and destination(to file or to ddms).
 *
 * @author peng.tan
 *
 */
class ICAT_API ICatchCameraLog
{
public:
    /** singleton */
    ICatchCameraLog(){}
    static std::shared_ptr<ICatchCameraLog> getInstance();

public:
    void setFileLogOutput(bool fileLog);
    void setFileLogPath(std::string path);

    void setSystemLogOutput(bool systemLog);

    void setLog(int type, bool enable);
    void setLogLevel(int type, int level);

    void setDebugMode(bool enable);

private:
    static std::shared_ptr<ICatchCameraLog> controlLog;
};

}}}

#endif

