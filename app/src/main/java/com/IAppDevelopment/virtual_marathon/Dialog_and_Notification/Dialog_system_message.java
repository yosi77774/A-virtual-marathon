package com.IAppDevelopment.virtual_marathon.Dialog_and_Notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.IAppDevelopment.virtual_marathon.R;

public class Dialog_system_message extends AppCompatDialogFragment {
    private Dialog_system_message.DialogListener listener;
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.system_notice)
          //      .setMessage("1.המלצת המערכת לבחור מסלול מישורי לביצוע המקצה...ריצה בעליה תוריד לך זמן ,ריצה בירידה תוסיף.\n2. נסה להישאר במקומות פתוחים במהלך המרוץ,יתכן שמעקב המיקום יהיה בלתי מדויק אם קליטת ה-GPS בטלפון חלשה.\n בהצלחה !!")
                .setMessage(R.string.Message_system_notice)
                .setPositiveButton(R.string.b_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onOkClicked_Dialog_system_message();;
                    }
                });

        return builder.create();
    }


    public interface DialogListener {
        void onOkClicked_Dialog_system_message();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (Dialog_system_message.DialogListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "mist implement DialogListener");
        }

    }
}


