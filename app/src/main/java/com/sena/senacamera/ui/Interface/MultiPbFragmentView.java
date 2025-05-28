package com.sena.senacamera.ui.Interface;

import androidx.recyclerview.widget.RecyclerView;

import com.sena.senacamera.adapter.MultiPbRecyclerViewAdapter;
import com.sena.senacamera.data.Mode.OperationMode;

public interface MultiPbFragmentView {
    void setRecyclerViewVisibility(int visibility);
    void setRecyclerViewAdapter(MultiPbRecyclerViewAdapter recyclerViewAdapter);
    void setRecyclerViewLayoutManager(RecyclerView.LayoutManager layout);
    void notifyChangeMultiPbMode(OperationMode operationMode);
    void setPhotoSelectNumText(int selectNum);
    void setNoContentTxvVisibility(int visibility);

}
