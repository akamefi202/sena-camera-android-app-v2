package com.sena.senacamera.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.bluetooth.BluetoothInfo;
import com.sena.senacamera.data.Mode.PreviewMode;
import com.sena.senacamera.listener.BluetoothCommandCallback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.activity.SplashActivity;
import com.sena.senacamera.ui.component.CustomTextInput;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.utils.ClickUtils;
import com.sena.senacamera.utils.ConvertTools;

import java.util.Arrays;

public class FragmentChangeDeviceName extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentChangeDeviceName.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageView cameraImage;
    Button doneButton;
    CustomTextInput deviceNameInput;

    TextWatcher deviceNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // update enable status of done button
            doneButton.setEnabled(s.length() != 0 && !s.toString().isBlank());
        }
    };

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_change_device_name, viewGroup, false);

        this.cameraImage = (ImageView) this.fragmentLayout.findViewById(R.id.camera_image);
        this.doneButton = (Button) this.fragmentLayout.findViewById(R.id.done_button);
        this.deviceNameInput = (CustomTextInput) this.fragmentLayout.findViewById(R.id.device_name_input);

        // read the original device name from parameter
        String orgDeviceName = getArguments().getString("deviceName");
        this.deviceNameInput.setText(orgDeviceName);
        this.deviceNameInput.setTextChangedListener(deviceNameWatcher);

        this.doneButton.setEnabled(orgDeviceName != null && !orgDeviceName.isBlank());
        this.doneButton.setOnClickListener(v -> onDone());

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
        this.cameraImage = null;
        this.doneButton = null;
        this.deviceNameInput = null;
    }

    @Override
    public void onClick(View v) {

    }

    public void updateFragment() {

    }

    private void onDone() {
        Bundle result = new Bundle();
        result.putString("deviceName", deviceNameInput.getText());
        requireActivity().getSupportFragmentManager().setFragmentResult(TAG, result);
        requireActivity().getSupportFragmentManager().popBackStack();

//        BluetoothCommandManager commandManager = BluetoothCommandManager.getInstance();
//        String wifiPassword = commandManager.getCurrentWifiPassword();
//        Context context = requireContext();
//
//        MyProgressDialog.showProgressDialog(requireContext(), "");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (commandManager.isConnected()) {
//                    commandManager.addCommand(BluetoothInfo.setCameraWifiInfoCommand(deviceNameInput.getText(), wifiPassword), BluetoothInfo.setCameraWifiInfoCmdRep, new BluetoothCommandCallback() {
//                        @Override
//                        public void onSuccess(byte[] response) {
//                            AppLog.i(TAG, "setCameraWifiInfoCommand succeeded");
//                            byte[] payload = Arrays.copyOfRange(response, 6, response.length);
//                            String ssid = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 1, 33));
//                            String password = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 33, 65));
//                            AppLog.i(TAG, "setCameraWifiInfoCommand ssid: " + ssid + ", password: " + password);
//
//                            commandManager.setCurrentWifiSsid(ssid);
//                            commandManager.setCurrentWifiPassword(password);
//                            MyProgressDialog.closeProgressDialog();
//
//                            Bundle result = new Bundle();
//                            result.putString("deviceName", ssid);
//                            requireActivity().getSupportFragmentManager().setFragmentResult(TAG, result);
//                            requireActivity().getSupportFragmentManager().popBackStack();
//                        }
//
//                        @Override
//                        public void onFailure() {
//                            AppLog.i(TAG, "setCameraWifiInfoCommand failed");
//                            MyToast.show(context, R.string.error_occurred);
//                            MyProgressDialog.closeProgressDialog();
//                        }
//                    });
//                }
//            }
//        }).start();
    }

}
