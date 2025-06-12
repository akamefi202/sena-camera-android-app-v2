package com.sena.senacamera.ui.Interface;

import android.graphics.Bitmap;
import android.view.View;

import com.sena.senacamera.ui.adapter.LocalMultiPbWallGridAdapter;
import com.sena.senacamera.ui.adapter.LocalMultiPbWallListAdapter;
import com.sena.senacamera.data.Mode.OperationMode;


/**
 * Created by b.jiang on 2017/5/19.
 */

public interface LocalMediaView {
    void setListViewVisibility(int visibility);

    void setGridViewVisibility(int visibility);

    void setListViewAdapter(LocalMultiPbWallListAdapter photoWallListAdapter);

    void setGridViewAdapter(LocalMultiPbWallGridAdapter PhotoWallGridAdapter);

    void setListViewSelection(int position);

    void setGridViewSelection(int position);

    void setListViewHeaderText(String headerText);

    View listViewFindViewWithTag(int tag);

    View gridViewFindViewWithTag(int tag);

    void updateGridViewBitmaps(String tag, Bitmap bitmap);

    void notifyChangeMultiPbMode(OperationMode operationMode);

    void setPhotoSelectNumText(int selectNum);

    void setNoContentTxvVisibility(int visibility);
}
