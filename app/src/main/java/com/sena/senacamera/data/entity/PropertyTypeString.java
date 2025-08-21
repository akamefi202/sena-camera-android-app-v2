package com.sena.senacamera.data.entity;

import android.content.Context;

import com.sena.senacamera.data.Hash.PropertyHashMapDynamic;
import com.sena.senacamera.data.Mode.PreviewMode;
import com.sena.senacamera.data.PropertyId.PropertyId;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class PropertyTypeString {

    private int propertyId;
    private List<String> valueListString;
    private List<String> valueListStringUI;
    private HashMap<String, ItemInfo> hashMap;
    private CameraProperties cameraProperties;

    public PropertyTypeString(CameraProperties cameraProperties, int propertyId, Context context) {
        this.propertyId = propertyId;
        this.cameraProperties = cameraProperties;
        initItem();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
            hashMap = PropertyHashMapDynamic.getInstance().getDynamicHashString(cameraProperties, propertyId);
        }
        if (hashMap == null) {
            return;
        }
        if (propertyId == PropertyId.PHOTO_RESOLUTION) {
            valueListString = cameraProperties.getSupportedImageSizes();
        }
        if (propertyId == PropertyId.VIDEO_RESOLUTION) {
            valueListString = cameraProperties.getSupportedVideoSizes();
        }
        for (int i = 0; i < valueListString.size(); i ++) {
            if (!hashMap.containsKey(valueListString.get(i))) {
                valueListString.remove(i);
                i--;
            }
        }
        valueListStringUI = new LinkedList<String>();
        if (valueListString != null) {
            for (int i = 0; i < valueListString.size(); i ++) {
                valueListStringUI.add(i, hashMap.get(valueListString.get(i)).uiStringInSettingString);
            }
        }
    }

    public String getCurrentValue() {
        // TODO Auto-generated method stub
        return cameraProperties.getCurrentStringPropertyValue(propertyId);
    }

    public String getCurrentUiStringInSetting() {
        if (hashMap == null) {
            return "Unknown";
        }
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = itemInfo.uiStringInSettingString;
        }
        return ret;
    }

    public String getCurrentUiStringInPreview() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
           return "Unknown";
        }
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = itemInfo.uiStringInPreview;
        }
        return ret;
    }

    public String getCurrentUiStringInSetting(int position) {
        // TODO Auto-generated method stub
        return valueListStringUI.get(position);
    }

    public List<String> getValueList() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public List<String> getValueListUI() {
        // TODO Auto-generated method stub
        return valueListStringUI;
    }

    public Boolean setValue(String value) {
        // TODO Auto-generated method stub
        return cameraProperties.setStringPropertyValue(propertyId, value);
    }

    public boolean setValueByPosition(int position) {
        return cameraProperties.setStringPropertyValue(propertyId,
                valueListString.get(position));
    }

    public Boolean needDisplayByMode(int previewMode) {
        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.PHOTO_RESOLUTION:
                //retValue = cameraProperties.setWhiteBalance(valueListInt.get(position));
                if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_IMAGE_SIZE)) {
                    if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        retValue = true;
                        break;
                    }
                }
                break;
            case PropertyId.VIDEO_RESOLUTION:
                if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_VIDEO_SIZE)) {
                    if (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        retValue = true;
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return retValue;
    }
}
