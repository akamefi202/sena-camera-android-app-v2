package com.sena.senacamera.ui.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.data.Mode.DeviceListMode;
import com.sena.senacamera.data.Mode.MediaViewMode;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.listener.DialogButtonListener;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.activity.DeviceListActivity;
import com.sena.senacamera.ui.activity.MediaActivity;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.fragment.FragmentBluetoothSearch;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CameraDeviceListAdapter extends ArrayAdapter<CameraDeviceInfo> {
    private static final String TAG = CameraDeviceListAdapter.class.getSimpleName();

    public ArrayList<CameraDeviceInfo> arrayList = null;
    private Context context;
    private BluetoothDeviceManager bluetoothDeviceManager = BluetoothDeviceManager.getInstance();
    private AppDialogManager appDialogManager = AppDialogManager.getInstance();

    private final View.OnClickListener buttonClickListener = view -> {
        int itemIndex = Integer.parseInt(view.getTag().toString());
        if (itemIndex > -1 && itemIndex < CameraDeviceListAdapter.this.arrayList.size()) {
            int id = view.getId();
            if (id == R.id.device_layout) {
                if (itemIndex == bluetoothDeviceManager.currentIndex || ((DeviceListActivity) this.context).getDeviceListMode().equals(DeviceListMode.EDIT)) {
                    return;
                }

                // show select device confirm dialog
                appDialogManager.showSelectDeviceConfirmDialog(context, new DialogButtonListener() {
                    @Override
                    public void onSelect() {
                        ((DeviceListActivity) context).selectDevice(itemIndex);
                    }
                });
            }
        }
    };

    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;
    static class ViewHolder {
        ImageView cameraImage = null;
        TextView deviceName = null, deviceNameEdit;
        LinearLayout deviceLayout, normalLayout = null, editLayout = null;
        LinearLayout connectedStatusLayout = null, disconnectedStatusLayout = null;
        ImageButton selectButton = null, editButton = null, deleteButton = null;

        ViewHolder() {
        }
    }

    public CameraDeviceListAdapter(@NonNull Context context, int resource, ArrayList<CameraDeviceInfo> arrayList) {
        super(context, resource, arrayList);

        this.context = context;
        this.arrayList = arrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public CameraDeviceInfo getItem(int i) {
        return (CameraDeviceInfo) super.getItem(i);
    }

    @SuppressLint({"MissingPermission", "ClickableViewAccessibility"})
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.inflater.inflate(R.layout.item_camera_device, (ViewGroup) null);
            this.viewHolder = new ViewHolder();
            this.viewHolder.cameraImage = (ImageView) view.findViewById(R.id.camera_image);
            this.viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name_text);
            this.viewHolder.deviceNameEdit = (TextView) view.findViewById(R.id.device_name_text_edit);
            this.viewHolder.deviceLayout = (LinearLayout) view.findViewById(R.id.device_layout);
            this.viewHolder.normalLayout = (LinearLayout) view.findViewById(R.id.normal_layout);
            this.viewHolder.editLayout = (LinearLayout) view.findViewById(R.id.edit_layout);
            this.viewHolder.connectedStatusLayout = (LinearLayout) view.findViewById(R.id.connected_layout);
            this.viewHolder.disconnectedStatusLayout = (LinearLayout) view.findViewById(R.id.disconnected_layout);
            this.viewHolder.selectButton = (ImageButton) view.findViewById(R.id.select_button);
            this.viewHolder.editButton = (ImageButton) view.findViewById(R.id.edit_button);
            this.viewHolder.deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
            view.setTag(this.viewHolder);
        } else {
            this.viewHolder = (ViewHolder) view.getTag();
        }

        this.viewHolder.deviceLayout.setTag(position);
        this.viewHolder.deviceLayout.setOnClickListener(this.buttonClickListener);
        this.viewHolder.deviceLayout.setFocusable(false);

        this.viewHolder.deviceName.setText(this.arrayList.get(position).wifiSsid);
        this.viewHolder.deviceNameEdit.setText(this.arrayList.get(position).wifiSsid);

        if (bluetoothDeviceManager.currentIndex == position) {
            this.viewHolder.normalLayout.setBackgroundColor(context.getResources().getColor(R.color.overlay_gray_color));
        } else {
            this.viewHolder.normalLayout.setBackgroundColor(context.getResources().getColor(R.color.background_secondary_color));
        }

        if (bluetoothDeviceManager.isDeviceConnected(context, position)) {
            // device is connected
            this.viewHolder.disconnectedStatusLayout.setVisibility(View.GONE);
            this.viewHolder.connectedStatusLayout.setVisibility(View.VISIBLE);
        } else {
            // device is disconnected
            this.viewHolder.connectedStatusLayout.setVisibility(View.GONE);
            this.viewHolder.disconnectedStatusLayout.setVisibility(View.VISIBLE);
        }

        if (((DeviceListActivity) this.context).getDeviceListMode().equals(DeviceListMode.NORMAL)) {
            // normal mode
            this.viewHolder.editLayout.setVisibility(View.GONE);
            this.viewHolder.normalLayout.setVisibility(View.VISIBLE);
        } else {
            // edit mode
            this.viewHolder.normalLayout.setVisibility(View.GONE);
            this.viewHolder.editLayout.setVisibility(View.VISIBLE);
        }

        // initialize drag & drop handler
        View finalView = view;
        this.viewHolder.selectButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(finalView) {
                    @Override
                    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                        shadowSize.set(finalView.getWidth(), finalView.getHeight());
                        shadowTouchPoint.set(20, 57);
                    }
                };
                finalView.startDragAndDrop(data, shadowBuilder, finalView, 0);
                ((DeviceListActivity) context).setDraggedIndex(position);
                return true;
            }
            return false;
        });

        this.viewHolder.deleteButton.setOnClickListener((v) -> {
            if (!bluetoothDeviceManager.isDeviceConnected(context, position)) {
                // only able to delete if disconnected
                // show delete confirm dialog
                appDialogManager.showDeleteConfirmDialog(context, new DialogButtonListener() {
                    @Override
                    public void onDelete() {
                        ((DeviceListActivity) context).saveOriginalDeviceInfo();

                        bluetoothDeviceManager.deleteDevice(position);
                        ((DeviceListActivity) context).updateDeviceListView();
                    }
                }, "");
            } else {
                appDialogManager.showAlertDialog(context, null, context.getResources().getString(R.string.not_allowed_to_delete_device));
            }
        });

        this.viewHolder.editButton.setOnClickListener((v) -> {
            if (bluetoothDeviceManager.isDeviceConnected(context, position)) {
                // only able to edit if connected
                appDialogManager.showDeviceRenameDialog(context, new DialogButtonListener() {
                    @Override
                    public void onDone(String param) {
                        ((DeviceListActivity) context).saveOriginalDeviceInfo();

                        CameraDeviceInfo currentDevice = bluetoothDeviceManager.getCurrentDevice();
                        currentDevice.wifiSsid = param;
                        bluetoothDeviceManager.updateCurrentDevice(currentDevice);
                        ((DeviceListActivity) context).updateDeviceListView();
                    }
                }, this.viewHolder.deviceNameEdit.getText().toString());
            } else {
                appDialogManager.showAlertDialog(context, null, context.getResources().getString(R.string.not_allowed_to_edit_device));
            }
        });

        return view;
    }
}
