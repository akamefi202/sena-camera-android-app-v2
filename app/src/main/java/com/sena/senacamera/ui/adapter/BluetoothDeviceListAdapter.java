package com.sena.senacamera.ui.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sena.senacamera.R;
import com.sena.senacamera.ui.fragment.FragmentBluetoothSearch;
import com.sena.senacamera.ui.fragment.FragmentOptions;

import java.util.ArrayList;

public class BluetoothDeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
    private static final String TAG = CameraDeviceListAdapter.class.getSimpleName();

    public ArrayList<BluetoothDevice> arrayList = null;
    private Context context;
    private FragmentBluetoothSearch parentFragment;
    private final View.OnClickListener buttonClickListener = view -> {
        int itemIndex = Integer.parseInt(view.getTag().toString());
        if (itemIndex > -1 && itemIndex < BluetoothDeviceListAdapter.this.arrayList.size()) {
            int id = view.getId();
            if (id == R.id.bluetooth_device_layout) {
                this.parentFragment.selectOption(itemIndex);
                this.notifyDataSetChanged();
            }
        }
    };

    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;
    static class ViewHolder {
        ImageView cameraImage = null;
        TextView deviceName = null;
        TextView deviceAddress = null;
        LinearLayout deviceLayout = null;

        ViewHolder() {
        }
    }

    public BluetoothDeviceListAdapter(@NonNull Context context, int resource, ArrayList<BluetoothDevice> arrayList, FragmentBluetoothSearch parentFragment) {
        super(context, resource, arrayList);

        this.context = context;
        this.arrayList = arrayList;
        this.inflater = LayoutInflater.from(context);
        this.parentFragment = parentFragment;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        return (BluetoothDevice) super.getItem(i);
    }

    @SuppressLint("MissingPermission")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.inflater.inflate(R.layout.item_bluetooth_device, (ViewGroup) null);
            this.viewHolder = new ViewHolder();
            this.viewHolder.cameraImage = (ImageView) view.findViewById(R.id.camera_image);
            this.viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name_text);
            this.viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address_text);
            this.viewHolder.deviceLayout = (LinearLayout) view.findViewById(R.id.bluetooth_device_layout);
            view.setTag(this.viewHolder);
        } else {
            this.viewHolder = (ViewHolder) view.getTag();
        }

        this.viewHolder.deviceLayout.setTag(position);
        this.viewHolder.deviceLayout.setOnClickListener(this.buttonClickListener);
        this.viewHolder.deviceLayout.setFocusable(false);

        this.viewHolder.deviceName.setText(this.arrayList.get(position).getName());
        this.viewHolder.deviceAddress.setText(String.format("B/D: %s", this.arrayList.get(position).getAddress()));

        return view;
    }
}
