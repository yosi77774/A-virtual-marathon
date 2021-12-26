package com.IAppDevelopment.virtual_marathon.Dialog_and_Notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.IAppDevelopment.virtual_marathon.R;

public class Dialog_Accuracy extends AppCompatDialogFragment {
    private Dialog_Accuracy.DialogListener listener;
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Weak_GPS_signals)
                .setMessage(R.string.Message_Weak_GPS_signals)
                .setPositiveButton(R.string.b_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onOkClicked_Dialog_Accuracy();;
                    }
                });

        return builder.create();
    }


    public interface DialogListener {
        void onOkClicked_Dialog_Accuracy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (Dialog_Accuracy.DialogListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "mist implement DialogListener");
        }

    }
}
