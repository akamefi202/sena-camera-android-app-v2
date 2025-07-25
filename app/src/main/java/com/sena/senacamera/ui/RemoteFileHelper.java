package com.sena.senacamera.ui;

import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.SdkApi.FileOperation;
import com.sena.senacamera.data.PropertyId.PropertyId;
import com.sena.senacamera.data.entity.MultiPbFileResult;
import com.sena.senacamera.data.entity.RemoteMediaItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.utils.ConvertTools;
import com.sena.senacamera.utils.FileFilter;
import com.sena.senacamera.utils.PanoramaTools;
import com.icatchtek.control.customer.type.ICatchCamFeatureID;
import com.icatchtek.control.customer.type.ICatchCamListFileFilter;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author b.jiang
 * @date 2020/1/9
 * @description
 */
public class RemoteFileHelper {
    private static final String TAG = RemoteFileHelper.class.getSimpleName();
    private static RemoteFileHelper instance;
    public HashMap<Integer, List<RemoteMediaItemInfo>> listHashMap = new HashMap<>();
    private int curFilterFileType = ICatchCamListFileFilter.ICH_OFC_FILE_TYPE_ALL_MEDIA;
    private FileFilter fileFilter = null;
    private final int MAX_NUM = 30;
    private boolean supportSegmentedLoading = false;
    private boolean supportSetFileListAttribute = false;
    private int sensorsNum = 1;

    //    ICatchCameraProperty
//    ICatchCamFeatureID
//
    public static synchronized RemoteFileHelper getInstance() {
        if (instance == null) {
            instance = new RemoteFileHelper();
        }
        return instance;
    }

    public void initSupportCapabilities() {
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        CameraProperties cameraProperties = null;
        if (camera != null) {
            cameraProperties = camera.getCameraProperties();
        }
        if (cameraProperties != null
                && cameraProperties.hasFunction(PropertyId.CAMERA_PB_LIMIT_NUMBER)
                && cameraProperties.checkCameraCapabilities(ICatchCamFeatureID.ICH_CAM_NEW_PAGINATION_GET_FILE)) {
            supportSegmentedLoading = true;
            supportSetFileListAttribute = true;
        } else {
            supportSegmentedLoading = false;
            supportSetFileListAttribute = false;
        }

        if (cameraProperties != null) {
            sensorsNum = cameraProperties.getNumberOfSensors();
        }
    }

    public int getSensorsNum() {
        return sensorsNum;
    }

    public boolean isSupportSegmentedLoading() {
        return supportSegmentedLoading;
    }

    public boolean isSupportSetFileListAttribute() {
        return supportSetFileListAttribute;
    }

    public List<RemoteMediaItemInfo> getRemoteFile(FileOperation fileOperation, FileType fileType) {
        int icatchFileType = ICatchFileType.ICH_FILE_TYPE_IMAGE;
        if (fileType == FileType.FILE_PHOTO) {
            icatchFileType = ICatchFileType.ICH_FILE_TYPE_IMAGE;
        } else if (fileType == FileType.FILE_VIDEO) {
            icatchFileType = ICatchFileType.ICH_FILE_TYPE_VIDEO;
        } else if (fileType == FileType.FILE_EMERGENCY_VIDEO) {
            icatchFileType = ICatchFileType.ICH_FILE_TYPE_VIDEO;
        }
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        CameraProperties cameraProperties = null;
        List<RemoteMediaItemInfo> tempItemInfos;
        if (camera != null) {
            cameraProperties = camera.getCameraProperties();
        }
        if (cameraProperties != null && cameraProperties.hasFunction(PropertyId.CAMERA_PB_LIMIT_NUMBER)) {
            tempItemInfos = getFileList(fileOperation,icatchFileType,500);
        } else {
            setFileListAttribute(fileOperation, fileType);
            List<ICatchFile> fileList = fileOperation.getFileList(icatchFileType);
            tempItemInfos = getList(fileList, fileFilter, fileType);
        }
//        setFileListAttribute(fileOperation, fileType);
//        List<ICatchFile> fileList= fileOperation.getFileList(icatchFileType);
//        tempItemInfos = getList(fileList, fileFilter);

        if (tempItemInfos == null) {
            return Collections.emptyList();
        }

        return tempItemInfos;
    }

    public MultiPbFileResult getRemoteFile(FileOperation fileOperation, FileType fileType, int fileTotalNum, int startIndex) {
        AppLog.d(TAG, "getRemoteFile fileType:" + fileType + " fileTotalNum:" + fileTotalNum + " startIndex:" + startIndex + " maxNum:" + MAX_NUM);
        if (startIndex > fileTotalNum) {
            MultiPbFileResult result = new MultiPbFileResult();
            result.setLastIndex(startIndex);
            result.setMore(false);
            result.setFileList(null);
            return result;
        }
        int endIndex = MAX_NUM + startIndex - 1;
        if (endIndex >= fileTotalNum) {
            endIndex = fileTotalNum;
        }
        setFileListAttribute(fileOperation, fileType);

        List<ICatchFile> fileList = fileOperation.getFileList(ICatchFileType.ICH_FILE_TYPE_ALL, startIndex, endIndex);
//        List<ICatchFile> fileList = fileOperation.getFileList(icatchFileType, startIndex, endIndex);
        if (fileFilter == null || fileFilter.getTimeFilterType() == FileFilter.TIME_TYPE_ALL_TIME) {
            List<RemoteMediaItemInfo> pbItemInfos = getList(fileList, null, fileType);
            int lastIndex = endIndex + 1;
            boolean isMore = lastIndex < fileTotalNum;
            MultiPbFileResult result = new MultiPbFileResult();
            result.setFileList(pbItemInfos);
            result.setLastIndex(lastIndex);
            result.setMore(isMore);
            AppLog.d(TAG, "End getRemoteFile filelist size:" + pbItemInfos.size());

            return result;
        } else {
            List<RemoteMediaItemInfo> tempItemInfos = getList(fileList, fileFilter, fileType);
            List<RemoteMediaItemInfo> pbItemInfos = new LinkedList<>();
            if (tempItemInfos != null && tempItemInfos.size() > 0) {
                pbItemInfos.addAll(tempItemInfos);
            }
            int lastIndex = endIndex + 1;
            boolean isMore;
            while (pbItemInfos.size() < MAX_NUM && lastIndex < fileTotalNum) {
                if (fileList != null && fileList.size() > 0 && fileFilter.isLess(fileList.get(fileList.size() - 1))) {
                    break;
                }
                endIndex = MAX_NUM + lastIndex - 1;
                if (endIndex >= fileTotalNum) {
                    endIndex = fileTotalNum;
                }
                fileList = fileOperation.getFileList(ICatchFileType.ICH_FILE_TYPE_ALL, lastIndex, endIndex);
                tempItemInfos = getList(fileList, fileFilter, fileType);
                if (tempItemInfos != null && tempItemInfos.size() > 0) {
                    pbItemInfos.addAll(tempItemInfos);
                }
                lastIndex = endIndex + 1;

            }
            if (fileList != null && fileList.size() > 0 && fileFilter.isLess(fileList.get(fileList.size() - 1))) {
                isMore = false;
            } else {
                isMore = lastIndex < fileTotalNum;
            }
            MultiPbFileResult result = new MultiPbFileResult();
            result.setFileList(pbItemInfos);
            result.setLastIndex(lastIndex);
            result.setMore(isMore);
            AppLog.d(TAG, "End getRemoteFile and fileFilter filelist size:" + pbItemInfos.size());
            return result;
        }
    }

    public List<RemoteMediaItemInfo> getFileList(FileOperation fileOperation, int type, int maxNum) {
        AppLog.i(TAG, "begin getFileList type: " + type + " maxNum：" + maxNum);
        if (fileOperation == null) {
            AppLog.i(TAG, "cameraPlayback is null");
            return null;
        }
        int startIndex = 0;
        int endIndex;
        int fileCount = -1;
        List<ICatchFile> photoList = new LinkedList<>();
        List<ICatchFile> videoList = new LinkedList<>();

        fileCount = fileOperation.getFileCount();
        if (fileCount <=0) {
            return null;
        }
        if (fileCount  < maxNum) {
            startIndex = 1 ;
            endIndex = fileCount;
        } else {
            startIndex = 1;
            endIndex = maxNum;
        }
        while (fileCount >= startIndex) {
            AppLog.i(TAG, "start getFileList startIndex=" + startIndex + " endIndex=" + endIndex);
            try {
                List<ICatchFile> templist = fileOperation.getFileList(ICatchFileType.ICH_FILE_TYPE_ALL,startIndex, endIndex);//timeout 20s
                if (templist != null) {
                    AppLog.i(TAG, "end getFileList tempList =" + templist.size());
                }
                if (templist != null && templist.size() > 0) {
                    for (ICatchFile file: templist) {
                        AppLog.i(TAG, "getFileList fileInfo[" + file.toString() + "]");
                        if (file != null && file.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
                            videoList.add(file);
                        } else if (file != null && file.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
                            photoList.add(file);
                        }
                    }
//				 	list.addAll(templist);
                }

                AppLog.i(TAG, "end getFileList photoList size=" + photoList.size());
                AppLog.i(TAG, "end getFileList videoList size=" + videoList.size());
            } catch (Exception e) {
                AppLog.e(TAG, "Exception e:" + e.getClass().getSimpleName());
                e.printStackTrace();
            }

            startIndex = endIndex + 1;
            endIndex = Math.min(endIndex + maxNum, fileCount);
            AppLog.i(TAG, "end getFileList startIndex=" + startIndex + " endIndex=" + endIndex);
        }

        List<RemoteMediaItemInfo> photoInfoList = getList(photoList, fileFilter, FileType.FILE_PHOTO);
        List<RemoteMediaItemInfo> videoInfoList = getList(videoList, fileFilter, FileType.FILE_VIDEO);
//        GlobalInfo.getInstance().photoInfoList = photoInfoList;
//        GlobalInfo.getInstance().videoInfoList = videoInfoList;
        setLocalFileList(photoInfoList,FileType.FILE_PHOTO);
        setLocalFileList(videoInfoList,FileType.FILE_VIDEO);
        if (type == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
            return videoInfoList;
        } else if (type == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
            return photoInfoList;
        } else {
            return null;
        }
    }

    private List<RemoteMediaItemInfo> getList(List<ICatchFile> fileList, FileFilter fileFilter, FileType fileType) {
        List<RemoteMediaItemInfo> multiPbItemInfoList = new LinkedList<>();
        if (fileList == null) {
            return multiPbItemInfoList;
        }
        String fileDate;
        String fileSize;
        String fileTime;
        String fileDuration;
        boolean isPanorama;
        for (int ii = 0; ii < fileList.size(); ii++) {
            ICatchFile iCatchFile = fileList.get(ii);
            fileDate = ConvertTools.getTimeByFileDate(iCatchFile.getFileDate());
            fileSize = ConvertTools.ByteConversionGBMBKB(iCatchFile.getFileSize());;
            fileTime = ConvertTools.getDateTimeString(iCatchFile.getFileDate());
            fileDuration = ConvertTools.millisecondsToMinuteOrHours((int) Math.ceil(iCatchFile.getFileDuration()));
            isPanorama = PanoramaTools.isPanorama(iCatchFile.getFileWidth(), iCatchFile.getFileHeight());

            if (fileFilter != null) {
                if (fileFilter.isMatch(iCatchFile)) {
                    RemoteMediaItemInfo mGridItem = new RemoteMediaItemInfo(iCatchFile, 0, isPanorama, fileSize, fileTime, fileDate, fileDuration, fileType == FileType.FILE_PHOTO? "photo": "video");
                    multiPbItemInfoList.add(mGridItem);
                }
            } else {
                RemoteMediaItemInfo mGridItem = new RemoteMediaItemInfo(iCatchFile, 0, isPanorama, fileSize, fileTime, fileDate, fileDuration, fileType == FileType.FILE_PHOTO? "photo": "video");
                multiPbItemInfoList.add(mGridItem);
            }
        }

        return multiPbItemInfoList;
    }


    public void setFileListAttribute(FileOperation fileOperation, FileType fileType) {
        if (!supportSetFileListAttribute) {
            return;
        }
        int filterFileType = ICatchCamListFileFilter.ICH_OFC_TYPE_IMAGE;
        if (fileType == FileType.FILE_PHOTO) {
            filterFileType = ICatchCamListFileFilter.ICH_OFC_TYPE_IMAGE;
        } else if (fileType == FileType.FILE_VIDEO) {
            filterFileType = ICatchCamListFileFilter.ICH_OFC_TYPE_VIDEO;
        } else if (fileType == FileType.FILE_EMERGENCY_VIDEO) {
            filterFileType = ICatchCamListFileFilter.ICH_OFC_TYPE_EMERGENCY_VIDEO;
        }
//        if (curFilterFileType != filterFileType) {
//            fileOperation.setFileListAttribute(filterFileType);
//            curFilterFileType = filterFileType;
//        } else {
//            AppLog.d(TAG, "Current is already fileType:" + fileType);
//        }
        if (fileFilter != null) {
            fileOperation.setFileListAttribute(filterFileType, fileFilter.getSensorType());
        } else {
            fileOperation.setFileListAttribute(filterFileType, ICatchCamListFileFilter.ICH_TAKEN_BY_ALL_SENSORS);
        }

    }

    public int getFileCount(FileOperation fileOperation, FileType fileType) {
        setFileListAttribute(fileOperation, fileType);
        int fileCount = fileOperation.getFileCount();
        AppLog.d(TAG, "fileCount:" + fileCount);
        return fileCount;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    public void setLocalFileList(List<RemoteMediaItemInfo> pbItemInfoList, FileType fileType) {
        if (pbItemInfoList == null) {
            return;
        }
        if (listHashMap.containsKey(fileType.ordinal())) {
            listHashMap.remove(fileType.ordinal());
        }
        List<RemoteMediaItemInfo> temp = new LinkedList<>();
        temp.addAll(pbItemInfoList);
        listHashMap.put(fileType.ordinal(), temp);
    }

    public List<RemoteMediaItemInfo> getLocalFileList(FileType fileType) {
        return listHashMap.get(fileType.ordinal());
    }

    public void clearFileList(FileType fileType) {
        if (listHashMap.containsKey(fileType.ordinal())) {
            listHashMap.remove(fileType.ordinal());
        }
    }

    public void remove(RemoteMediaItemInfo file, FileType fileType) {
        List<RemoteMediaItemInfo> multiPbItemInfos = getLocalFileList(fileType);
        if (multiPbItemInfos != null) {
            multiPbItemInfos.remove(file);
        }
    }

    public void clearAllFileList() {
        listHashMap.clear();
    }

    public boolean needFilter() {
        if (fileFilter != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needFilterMoreFile(ICatchFile lastFile) {
        if (fileFilter != null) {
            return !fileFilter.isLess(lastFile);
        } else {
            return true;
        }
    }
}