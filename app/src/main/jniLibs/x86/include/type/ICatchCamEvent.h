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

#ifndef __ICATCH_CAM_EVENT__
#define __ICATCH_CAM_EVENT__

#include <string>
#include <memory>
#include "WindowsApi.h"
#include "type/ICatchFile.h"

namespace com { namespace icatchtek{ namespace control{

/**
 * The class of ICatch event.
 */
class ICAT_API ICatchCamEvent
{
private:
    int eventID;
    int sessionID;

    int64_t longValue1;
    int64_t longValue2;
    int64_t longValue3;

    double doubleValue1;
    double doubleValue2;
    double doubleValue3;

    std::string stringValue1;
    std::string stringValue2;
    std::string stringValue3;
    std::shared_ptr<com::icatchtek::reliant::ICatchFile> fileValue;

public:
    /**
     * Constructor, mainly used for the SDK.
     * @param eventID The ID of the event.
     * @param sessionID The ID of the session.
     */
    ICatchCamEvent(int eventID, int sessionID);
    ~ICatchCamEvent();
public:
    /**
     * Get event id.
     * @return event id.
     */
    int getEventID();

    /**
     * Set eventID, usually used by the sdk.
     * @param eventID value.
     */
    void setEventID(int eventID);

    /**
     * Get sessionID.
     * @return sessionID.
     */
    int getSessionID();

    /**
     * Set sessionID, usually used by the sdk.
     * @param sessionID value.
     */
    void setSessionID(int sessionID);

    /**
     * Get value parameter 1(type long).
     * @return long value.
     */
    int64_t getLongValue1();

    /**
     * Set value parameter 1(type long), usually used by the sdk.
     * @param longValue1 value.
     */
    void setLongValue1(int64_t longValue1);

    /**
     * Get value parameter 2(type long).
     * @return long value.
     */
    int64_t getLongValue2();

    /**
     * Set value parameter 2(type long), usually used by the sdk.
     * @param longValue2 value.
     */
    void setLongValue2(int64_t longValue2);

    /**
     * Get value parameter 3(type long).
     * @return long value.
     */
    int64_t getLongValue3();

    /**
     * Set value parameter 3(type long), usually used by the sdk.
     * @param longValue3 value.
     */
    void setLongValue3(int64_t longValue3);

    /**
     * Get value parameter 1(type double).
     * @return parameter 1(type double)
     */
    double getDoubleValue1();

    /**
     * Set value parameter 1(type double), usually used by the sdk.
     * @param doubleValue1 value.
     */
    void setDoubleValue1(double doubleValue1);

    /**
     * Get value parameter 2(type double).
     * @return parameter 2(type double)
     */
    double getDoubleValue2();

    /**
     * Set value parameter 2(type double), usually used by the sdk.
     * @param doubleValue2 value.
     */
    void setDoubleValue2(double doubleValue2);

    /**
     * Get value parameter 3(type double).
     * @return parameter 3(type double)
     */
    double getDoubleValue3();

    /**
     * Set value parameter 3(type double), usually used by the sdk.
     * @param doubleValue3 value.
     */
    void setDoubleValue3(double doubleValue3);

    /**
     * Get string value1.
     * @return string value1.
     */
    std::string getStringValue1();

    /**
     * Set string value1.
     * @param stringValue1 string value1.
     */
    void setStringValue1(std::string stringValue1);

    /**
     * Set string value2.
     * @param stringValue2 string value2.
     */
    void setStringValue2(std::string stringValue2);

    /**
     * Get string value2.
     * @return string value2.
     */
    std::string getStringValue2();

    /**
     * Set string value3.
     * @param stringValue3 string value3.
     */
    void setStringValue3(std::string stringValue3);

    /**
     * Get string value 3.
     * @return string value3.
     */
    std::string getStringValue3();

    void setFileValue(shared_ptr<com::icatchtek::reliant::ICatchFile> fileValue);
    shared_ptr<com::icatchtek::reliant::ICatchFile> getFileValue();


    std::string toString();
};

}}}

#endif

