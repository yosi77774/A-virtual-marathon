package com.IAppDevelopment.virtual_marathon.Dialog_and_Notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.IAppDevelopment.virtual_marathon.R;

public class Dialog extends AppCompatDialogFragment {
private Dialog.DialogListener listener;
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.system_notice)
                .setMessage(R.string.cease_of_action)
                .setNegativeButton(R.string.b_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onCancelClicked();
                    }
                })

                .setPositiveButton(R.string.b_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onOkClicked();
                    }
                });
        return builder.create();
    }


    public interface DialogListener {
        void onOkClicked();
        void onCancelClicked();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "mist implement DialogListener");
        }

    }
}
