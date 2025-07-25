package com.sena.senacamera.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.utils.ClickUtils;

public class FragmentSdCard extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentSdCard.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    Button formatButton;
    TextView totalCapacityText, usedCapacityText, remainingCapacityText;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_sd_card, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.formatButton = (Button) this.fragmentLayout.findViewById(R.id.format_sd_card_button);
        this.totalCapacityText = (TextView) this.fragmentLayout.findViewById(R.id.total_capacity_text);
        this.usedCapacityText = (TextView) this.fragmentLayout.findViewById(R.id.used_capacity_text);
        this.remainingCapacityText = (TextView) this.fragmentLayout.findViewById(R.id.remaining_capacity_text);

        this.backButton.setOnClickListener(v -> onBack());
        this.formatButton.setOnClickListener(v -> formatSdCard());

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
        this.backButton = null;
        this.formatButton = null;
        this.totalCapacityText = null;
        this.usedCapacityText = null;
        this.remainingCapacityText = null;
    }

    public void updateFragment() {

    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View v) {

    }

    public void formatSdCard() {
        if (ClickUtils.isFastClick()) {
            return;
        }

        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null || !curCamera.isConnected()) {
            AppLog.i(TAG, "camera is not connected");
            return;
        }
        CameraProperties cameraProperties = curCamera.getCameraProperties();
        if (!cameraProperties.isSDCardExist()) {
            AppLog.i(TAG, "sd card does not exist");
            return;
        }
        CameraAction cameraAction = curCamera.getCameraAction();

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogLayout = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_format_sd_card, null);
        dialog.setContentView(dialogLayout);
        dialog.show();

        Button formatButton = dialogLayout.findViewById(R.id.format_button);
        ImageButton closeButton = dialogLayout.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        formatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final Handler handler = new Handler();
                MyProgressDialog.showProgressDialog(requireContext(), R.string.formatting);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int messageId;

                        if (cameraAction.formatStorage()) {
                            messageId = R.string.text_operation_success;
                        } else {
                            messageId = R.string.text_operation_failed;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                MyToast.show(requireContext(), messageId);
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
