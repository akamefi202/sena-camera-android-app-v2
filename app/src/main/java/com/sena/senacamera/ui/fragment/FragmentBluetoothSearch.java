package com.sena.senacamera.ui.fragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.bluetooth.BluetoothScanManager;
import com.sena.senacamera.data.SystemInfo.SystemInfo;
import com.sena.senacamera.data.entity.BluetoothDeviceInfo;
import com.sena.senacamera.data.type.BluetoothConnectStatus;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.listener.BluetoothConnectCallback;
import com.sena.senacamera.listener.BluetoothSearchCallback;
import com.sena.senacamera.listener.DialogButtonListener;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.adapter.BluetoothDeviceListAdapter;
import com.sena.senacamera.ui.adapter.OptionListAdapter;
import com.sena.senacamera.ui.component.CustomPasswordInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentBluetoothSearch extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentBluetoothSearch.class.getSimpleName();

    private ConstraintLayout fragmentLayout;
    private ImageButton closeButton;
    private TextView titleText, descText;
    private ListView deviceListView;
    private Button retryButton;
    private ProgressBar searchingProgress;

    boolean isSearching = true;
    private BluetoothDeviceListAdapter deviceListAdapter;
    private final BluetoothScanManager bluetoothScanManager = BluetoothScanManager.getInstance();
    private final BluetoothCommandManager bluetoothCommandManager = BluetoothCommandManager.getInstance();

    public final BluetoothSearchCallback bluetoothSearchCallback = new BluetoothSearchCallback() {
        @Override
        public void onFound(BluetoothDeviceInfo device) {
            AppLog.i(TAG, "onFound: " + bluetoothScanManager.getDeviceCount());
            deviceListAdapter.add(device);
            deviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed() {
            isSearching = false;
            updateFragment();
        }
    };

    private final BluetoothConnectCallback bluetoothConnectCallback = new BluetoothConnectCallback() {
        @Override
        public void onConnected() {
//            if (bluetoothCommandManager.currentDevice.wifiPassword.isEmpty()) {
                // show the set device fragment
                showSetDeviceFragment(true);
//            } else {
//                // show password check dialog
//                showPasswordInputDialog(bluetoothCommandManager.currentDevice.wifiPassword);
//            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onConnecting() {
            // stop scanning while connecting
            isSearching = false;
            bluetoothScanManager.stopScan();
        }
    };

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_bluetooth_search, viewGroup, false);

        this.closeButton = (ImageButton) this.fragmentLayout.findViewById(R.id.close_button);
        this.titleText = (TextView) this.fragmentLayout.findViewById(R.id.title_text);
        this.descText = (TextView) this.fragmentLayout.findViewById(R.id.desc_text);
        this.deviceListView = (ListView) this.fragmentLayout.findViewById(R.id.device_list);
        this.retryButton = (Button) this.fragmentLayout.findViewById(R.id.retry_button);
        this.searchingProgress = (ProgressBar) this.fragmentLayout.findViewById(R.id.searching_progress);

        this.closeButton.setOnClickListener(v -> onClose());
        this.retryButton.setOnClickListener(v -> onRetry());

        this.deviceListAdapter = new BluetoothDeviceListAdapter(requireContext(), R.id.device_list, new ArrayList<BluetoothDeviceInfo>(this.bluetoothScanManager.getDeviceList()), this);
        this.deviceListView.setAdapter(deviceListAdapter);
        this.bluetoothScanManager.addSearchCallback(bluetoothSearchCallback);

        this.bluetoothCommandManager.setContext(requireContext());
        
        return this.fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.fragmentLayout = null;
        this.closeButton = null;
        this.titleText = null;
        this.descText = null;
        this.deviceListView = null;
        this.retryButton = null;
        this.searchingProgress = null;
    }

    public void updateFragment() {
        if (isSearching) {
            this.titleText.setText(R.string.searching);
            this.descText.setText(R.string.make_sure_your_device_on);
            this.retryButton.setVisibility(View.GONE);
            this.searchingProgress.setVisibility(View.VISIBLE);
        } else {
            this.titleText.setText(R.string.select_your_device);
            this.descText.setText(R.string.connecting_desc);
            this.searchingProgress.setVisibility(View.GONE);
            this.retryButton.setVisibility(View.VISIBLE);
        }
    }

    private void onClose() {
        // show failed after back to connect device screen
        Bundle result = new Bundle();
        result.putString("command", "stop");
        requireActivity().getSupportFragmentManager().setFragmentResult(TAG, result);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void onRetry() {
        // retry scanning after back to connect device screen
        Bundle result = new Bundle();
        result.putString("command", "retry");
        requireActivity().getSupportFragmentManager().setFragmentResult(TAG, result);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View v) {

    }

    public void selectOption(int itemIndex) {
        bluetoothCommandManager.connectDevice(this.deviceListAdapter.getItem(itemIndex), bluetoothConnectCallback, true, true);
    }

    public void showSetDeviceFragment(boolean firstConnect) {
        bluetoothScanManager.stopScan();
        bluetoothScanManager.clearSearchCallbackList();

        Bundle args = new Bundle();
        args.putBoolean("firstConnect", firstConnect);

        Fragment fragment = new FragmentSetDevice();
        fragment.setArguments(args);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void showPasswordInputDialog(String password) {
        // stop scan
        bluetoothScanManager.stopScan();
        bluetoothScanManager.clearSearchCallbackList();

        // show password input dialog
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogLayout = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_password_input, null);
        dialog.setContentView(dialogLayout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button doneButton = dialogLayout.findViewById(R.id.done_button);
        doneButton.setEnabled(false);
        Button backButton = dialogLayout.findViewById(R.id.back_button);
        TextView forgotPasswordButton = dialog.findViewById(R.id.ble_password_input);
        CustomPasswordInput passwordInput = dialog.findViewById(R.id.password_input);
        passwordInput.setTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doneButton.setEnabled(s.length() != 0 && s.toString().equals(password));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemInfo.hideInputMethod(requireActivity());

                showSetDeviceFragment(false);
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentForgotPassword();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
