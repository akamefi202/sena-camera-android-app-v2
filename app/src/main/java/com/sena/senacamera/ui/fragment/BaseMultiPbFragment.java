package com.sena.senacamera.ui.fragment;

import androidx.fragment.app.Fragment;

import com.sena.senacamera.listener.OnStatusChangedListener;
import com.sena.senacamera.data.entity.RemoteMediaItemInfo;
import com.sena.senacamera.data.type.PhotoWallLayoutType;

import java.util.List;

public abstract class BaseMultiPbFragment extends Fragment {
    public abstract void setOperationListener(OnStatusChangedListener modeChangedListener);
    public abstract void changePreviewType(PhotoWallLayoutType layoutType);
    public abstract void quitEditMode();
    public abstract void selectOrCancelAll(boolean isSelectAll);
    public abstract void deleteFile();
    public abstract List<RemoteMediaItemInfo> getSelectedList();
    public abstract void loadPhotoWall();


}
