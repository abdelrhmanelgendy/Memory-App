package com.example.myapplication.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.myapplication.R;

public class ProgressDialog {
    Dialog progressDialog;
    TextView txtText;

    public ProgressDialog(Activity activity, Boolean cancelable) {

         progressDialog = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        progressDialog.setContentView(inflater.inflate(R.layout.progress_dialog, null));
        progressDialog.setCancelable(false);
        txtText=progressDialog.findViewById(R.id.txtProgressTEXT);




    }

    public ProgressDialog() {

    }

    public void show(String txt) {
        txtText.setText(txt);
      try {
          progressDialog.show();

      }catch (Exception e)
      {}

    }
    public void show() {

        progressDialog.show();



    }
    public void dismiss()
    {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
        catch (Exception e)
        {
            return;

        }    }
}
