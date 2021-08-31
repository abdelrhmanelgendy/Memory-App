package com.example.myapplication.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.util.TwoButtonDialogOnClickListener;

public class TwoButtonDailog implements View.OnClickListener {
    TextView txtShow, txtCancel, txtReplace;
    private TwoButtonDialogOnClickListener listener;

    Dialog dialog;

    public void setListener(TwoButtonDialogOnClickListener listener) {
        this.listener = listener;
    }

    public void ShowDialog(Activity activity, String mgs) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.image_button_dialog);
        dialog.setCancelable(true);

        txtShow = dialog.findViewById(R.id.button_dialog_TV_view);
        txtCancel = dialog.findViewById(R.id.button_dialog_TV_cancel);
        txtReplace = dialog.findViewById(R.id.button_dialog_TV_replace);

        txtShow.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtReplace.setOnClickListener(this);
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_dialog_TV_view:
                listener.firstDialogOnClick();
                break;
            case R.id.button_dialog_TV_replace:
                listener.secondDialogOnClick();
                dialog.dismiss();
                break;
            case R.id.button_dialog_TV_cancel:
                dialog.dismiss();

                break;
        }
    }
}
