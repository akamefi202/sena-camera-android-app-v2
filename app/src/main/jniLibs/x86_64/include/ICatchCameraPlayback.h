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

#ifndef __ICATCH_CAMERA_PLAYBACK_H__
#define __ICATCH_CAMERA_PLAYBACK_H__

#include <vector>
#include <memory>

#include "type/ICatchFile.h"
#include "type/ICatchFileType.h"
#include "type/ICatchFrameBuffer.h"

namespace com { namespace icatchtek{ namespace control{

class ICatchCameraPlayback
{
public:
    virtual ~ICatchCameraPlayback() {}

public:
    virtual int setFileListAttribute(unsigned int type, unsigned int order) = 0;
    virtual int setFileListAttribute(unsigned int type, unsigned int order, unsigned int takenBy) = 0;
    virtual int getFileCount(int& fileCount) = 0;
    virtual int listFiles(com::icatchtek::reliant::ICatchFileType type, std::vector<std::shared_ptr<com::icatchtek::reliant::ICatchFile>>& files) = 0;
    virtual int listFiles(com::icatchtek::reliant::ICatchFileType type, std::vector<std::shared_ptr<com::icatchtek::reliant::ICatchFile>>& files, int timeoutInSecs) = 0;
    virtual int listFiles(com::icatchtek::reliant::ICatchFileType type, int startIndex, int endIndex, std::vector<std::shared_ptr<com::icatchtek::reliant::ICatchFile>> &files, int timeoutInSecs) = 0;

    virtual int openFileTransChannel() = 0;
    virtual int downloadFileQuick(std::shared_ptr<com::icatchtek::reliant::ICatchFile> file, string path) = 0;
    virtual int closeFileTransChannel() = 0;

    virtual int downloadFile(std::shared_ptr<com::icatchtek::reliant::ICatchFile> file, std::shared_ptr<com::icatchtek::reliant::ICatchFrameBuffer> dataBuffer) = 0;
    virtual int downloadFile(std::shared_ptr<com::icatchtek::reliant::ICatchFile> file, std::string path) = 0;
    virtual int downloadFile(std::string srcPath, std::string dstPath ) = 0;
    virtual int uploadFile(std::string localPath, std::string remotePath) = 0;
    virtual int uploadFileQuick(std::string localPath, std::string remotePath) = 0;
    virtual int cancelFileDownload() = 0;

    virtual int deleteFile(std::shared_ptr<com::icatchtek::reliant::ICatchFile> file) = 0;

    virtual int getThumbnail(std::shared_ptr<com::icatchtek::reliant::ICatchFile> file, std::shared_ptr<com::icatchtek::reliant::ICatchFrameBuffer> dataBuffer) = 0;
    virtual int getQuickview(std::shared_ptr<com::icatchtek::reliant::ICatchFile> file, std::shared_ptr<com::icatchtek::reliant::ICatchFrameBuffer> dataBuffer) = 0;
};

}}}

#endif
