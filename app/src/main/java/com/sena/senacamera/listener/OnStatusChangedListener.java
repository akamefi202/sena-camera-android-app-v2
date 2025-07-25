package com.sena.senacamera.listener;

import com.sena.senacamera.data.Mode.OperationMode;

public interface OnStatusChangedListener {
    public void onChangeOperationMode(OperationMode curOperationMode);
    public void onSelectedItemsCountChanged(int SelectedNum);
}
