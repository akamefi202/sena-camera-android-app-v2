/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.sena.senacamera.SdkApi;

import com.sena.senacamera.log.AppLog;
import com.icatchtek.control.customer.ICatchCameraPlayback;
import com.icatchtek.control.customer.exception.IchCameraModeException;
import com.icatchtek.control.customer.exception.IchNoSuchPathException;
import com.icatchtek.control.customer.type.ICatchCamListFileFilter;
import com.icatchtek.reliant.customer.exception.IchBufferTooSmallException;
import com.icatchtek.reliant.customer.exception.IchDeviceException;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchNoSuchFileException;
import com.icatchtek.reliant.customer.exception.IchSocketException;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;

import java.util.List;

/**
 * Added by zhangyanhu C01012,2014-6-27
 */
public class FileOperation {
    private static final String TAG = FileOperation.class.getSimpleName();
    private ICatchCameraPlayback cameraPlayback;

    public FileOperation(ICatchCameraPlayback cameraPlayback) {
        this.cameraPlayback = cameraPlayback;
    }

    public boolean cancelDownload() {
        AppLog.i(TAG, "begin cancelDownload");
        if (cameraPlayback == null) {
            return true;
        }
        boolean retValue = false;
        try {
            retValue = cameraPlayback.cancelFileDownload();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDeviceException e) {
            AppLog.e(TAG, "IchDeviceException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end cancelDownload retValue =" + retValue);
        return retValue;
    }

    public List<ICatchFile> getFileList(int type, int startIndex, int endIndex) {
        AppLog.d(TAG, "begin getFileList type:" + type + " startIndex:" + startIndex + " endIndex:" + endIndex);
        List<ICatchFile> list = null;
        int timeout = 60;//单位s
        try {
            //Log.d("1111", "start listFiles cameraPlayback=" + cameraPlayback);
            list = cameraPlayback.listFiles(type, startIndex, endIndex, timeout);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchNoSuchPathException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchNoSuchPathException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }

//        if (list != null && list.size() > 0) {
//            for (ICatchFile file : list
//            ) {
//                AppLog.d(TAG, "getFileList info=" + file.toString());
//            }
//        }
        AppLog.d(TAG, "end getFileList");
        AppLog.i(TAG, "end getFileList list size=" + (list != null ? list.size() : -1));
        return list;
    }

    public List<ICatchFile> getFileList(int type) {
        AppLog.i(TAG, "begin getFileList type:" + type);
        List<ICatchFile> list = null;
        try {
            //Log.d("1111", "start listFiles cameraPlayback=" + cameraPlayback);
            list = cameraPlayback.listFiles(type);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchNoSuchPathException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchNoSuchPathException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            for (int ii =0; ii<list.size();ii++) {
                AppLog.d(TAG, "getFileList info=" + list.get(ii).toString());
            }
        }
        AppLog.i(TAG, "end getFileList list size=" + (list != null ? list.size() : -1));
        return list;
    }

    public int getFileCount() {
        int fileCount = 0;
        AppLog.i(TAG, "begin getFileCount");
        try {
            fileCount = cameraPlayback.getFileCount();
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.i(TAG, "begin getFileCount Exception:" + e.getClass().getSimpleName() + " error:" + e.getMessage());
        }
        AppLog.i(TAG, "end getFileList fileCount=" + fileCount);
        return fileCount;
    }

    public boolean setFileListAttribute(int filterType) {
        boolean ret = false;
        AppLog.i(TAG, "begin setFileListAttribute filterType=" + filterType);
        try {
            ret = cameraPlayback.setFileListAttribute(filterType, ICatchCamListFileFilter.ICH_SORT_TYPE_DESCENDING);
//            ret = cameraPlayback.setFileListAttribute(filterType,ICatchCamListFileFilter.ICH_SORT_TYPE_ASCENDING);
            AppLog.i(TAG, "11 setFileListAttribute ret=" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.i(TAG, "setFileListAttribute Exception:" + e.getClass().getSimpleName() + " error:" + e.getMessage());
        }
        AppLog.i(TAG, "end setFileListAttribute ret=" + ret);
        return ret;
    }

    public boolean setFileListAttribute(int filterType,int sensorsType) {
        boolean ret = false;
        AppLog.i(TAG, "begin setFileListAttribute filterType=" + filterType + " sensorsType=" + sensorsType);
        try {
            ret = cameraPlayback.setFileListAttribute(filterType, ICatchCamListFileFilter.ICH_SORT_TYPE_DESCENDING,sensorsType);
//            ret = cameraPlayback.setFileListAttribute(filterType,ICatchCamListFileFilter.ICH_SORT_TYPE_ASCENDING);
            AppLog.i(TAG, "11 setFileListAttribute ret=" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.i(TAG, "setFileListAttribute Exception:" + e.getClass().getSimpleName() + " error:" + e.getMessage());
        }
        AppLog.i(TAG, "end setFileListAttribute ret=" + ret);
        return ret;
    }

    public boolean deleteFile(ICatchFile file) {
        AppLog.i(TAG, "begin deleteFile filename =" + file.getFileName());
        boolean retValue = false;
        try {
            retValue = cameraPlayback.deleteFile(file);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end deleteFile retValue=" + retValue);
        return retValue;
    }

    public boolean downloadFile(ICatchFile file, String path) {
        AppLog.i(TAG, "begin downloadFile filename =" + file.getFileName());
        AppLog.i(TAG, "begin downloadFile path =" + path);
        boolean retValue = false;
        try {
            retValue = cameraPlayback.downloadFile(file, path);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end downloadFile retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public ICatchFrameBuffer downloadFile(ICatchFile curFile) {
        AppLog.i(TAG, "begin downloadFile for buffer filename =" + curFile.getFileName());
        ICatchFrameBuffer buffer = null;
        try {
            buffer = cameraPlayback.downloadFile(curFile);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchBufferTooSmallException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end downloadFile for buffer, buffer =" + buffer);
        return buffer;
    }

    public boolean uploadFile(String filePath, String filename) {
        AppLog.i(TAG, "begin uploadFile filePath =" + filePath);
        AppLog.i(TAG, "begin uploadFile filename =" + filename);
        boolean retValue = false;
        try {
            retValue = cameraPlayback.uploadFile(filePath, filename);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end uploadFile retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-10-28
     */

    public ICatchFrameBuffer getQuickview(ICatchFile curFile) {
        AppLog.i(TAG, "begin getQuickview for buffer filename =" + curFile.getFileName());
        ICatchFrameBuffer buffer = null;
        try {
            buffer = cameraPlayback.getQuickview(curFile);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        } catch (IchNoSuchFileException e) {
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getQuickview for buffer, buffer =" + buffer);
        if (buffer != null) {
            AppLog.i(TAG, "buffer size =" + buffer.getFrameSize());
        }
        return buffer;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public ICatchFrameBuffer getThumbnail(ICatchFile file) {
//        AppLog.i(TAG, "begin getThumbnail file=" + file);
        // TODO Auto-generated method stub
        ICatchFrameBuffer frameBuffer = null;
        try {
            //Log.d("1111", "start cameraPlayback.getThumbnail(file) cameraPlayback=" + cameraPlayback);
            frameBuffer = cameraPlayback.getThumbnail(file);
            //Log.d("1111", "end cameraPlayback.getThumbnail(file)");
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchDeviceException");
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchBufferTooSmallException");
            e.printStackTrace();
        }

        //AppLog.i(TAG, "end getThumbnail frameBuffer=" + frameBuffer);
        return frameBuffer;
    }

    public ICatchFrameBuffer getThumbnail(String filePath) {
        AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail");
        // TODO Auto-generated method stub
        ICatchFile icathfile = new ICatchFile(33, ICatchFileType.ICH_FILE_TYPE_VIDEO, filePath, "", 0);
        AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail file=" + filePath);
        AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail cameraPlayback=" + cameraPlayback);
        ICatchFrameBuffer frameBuffer = null;
        try {
            AppLog.d("test", "start cameraPlayback.getThumbnail(file) cameraPlayback=" + cameraPlayback);
            frameBuffer = cameraPlayback.getThumbnail(icathfile);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchDeviceException");
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchBufferTooSmallException");
            e.printStackTrace();
        }
        AppLog.d("[Normal] -- FileOperation: ", "end getThumbnail frameBuffer=" + frameBuffer);
        return frameBuffer;
    }


}
