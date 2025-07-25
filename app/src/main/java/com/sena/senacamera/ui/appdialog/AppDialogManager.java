package com.sena.senacamera.ui.appdialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.listener.DialogButtonListener;
import com.sena.senacamera.ui.component.CustomTextInput;

public class AppDialogManager {

    // instance
    private static final class InstanceHolder {
        private static final AppDialogManager instance = new AppDialogManager();
    }
    public static AppDialogManager getInstance() {
        return AppDialogManager.InstanceHolder.instance;
    }

    public void showDeleteConfirmDialog(Context context, DialogButtonListener listener, String target) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_delete_confirm, null);
        dialog.setContentView(dialogLayout);
        dialog.show();

        TextView descText = dialogLayout.findViewById(R.id.desc_text);
        if (target.isBlank()) {
            descText.setText(String.format("%s?", context.getResources().getString(R.string.wish_to_delete)));
        } else {
            descText.setText(String.format("%s %s?", context.getResources().getString(R.string.wish_to_delete), target));
        }
        Button deleteButton = dialogLayout.findViewById(R.id.delete_button);
        Button cancelButton = dialogLayout.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancel();
                }
                dialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelete();
                }
                dialog.dismiss();

            }
        });
    }

    public void showUnsavedChangesDialog(Context context, DialogButtonListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_unsaved_changes, null);
        dialog.setContentView(dialogLayout);
        dialog.show();

        Button contiuneButton = dialogLayout.findViewById(R.id.continue_button);
        ImageButton closeButton = dialogLayout.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClose();
                }
                dialog.dismiss();
            }
        });

        contiuneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onContinue();
                }
                dialog.dismiss();
            }
        });
    }

    public void showAlertDialog(Context context, DialogButtonListener listener, String desc) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
        dialog.setContentView(dialogLayout);
        dialog.show();

        TextView descText = dialogLayout.findViewById(R.id.desc_text);
        descText.setText(desc);
        Button okButton = dialogLayout.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOk();
                }
                dialog.dismiss();
            }
        });
    }

    public void showDeviceRenameDialog(Context context, DialogButtonListener listener, String currentName) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_device_rename, null);
        dialog.setContentView(dialogLayout);
        dialog.show();

        Button doneButton = dialogLayout.findViewById(R.id.done_button);
        Button cancelButton = dialogLayout.findViewById(R.id.cancel_button);
        CustomTextInput nameInput = dialogLayout.findViewById(R.id.device_name_input);
        nameInput.setText(currentName);
        nameInput.setTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doneButton.setEnabled(s.length() != 0);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancel();
                }
                dialog.dismiss();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDone(nameInput.getText());
                }
                dialog.dismiss();
            }
        });
    }

    public void showLowBatteryDialog(Context context, DialogButtonListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_low_battery, null);
        dialog.setContentView(dialogLayout);
        dialog.show();

        Button okButton = dialogLayout.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOk();
                }
                dialog.dismiss();
            }
        });
    }

    public void showUpdateFailedDialog(Context context, DialogButtonListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_update_failed, null);
        dialog.setContentView(dialogLayout);
        dialog.show();

        Button continueButton = dialogLayout.findViewById(R.id.continue_button);
        ImageButton closeButton = dialogLayout.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClose();
                }
                dialog.dismiss();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onContinue();
                }
                dialog.dismiss();
            }
        });
    }

    public void showNewFirmwareAvailableDialog(Context context, DialogButtonListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_new_firmware_available, null);
        dialog.setContentView(dialogLayout);
        dialog.show();

        Button updateButton = dialogLayout.findViewById(R.id.update_firmware_button);
        ImageButton closeButton = dialogLayout.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClose();
                }
                dialog.dismiss();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onUpdate();
                }
                dialog.dismiss();
            }
        });
    }
}
