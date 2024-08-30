package com.example.linkstation.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.linkstation.R;

public class CustomProgressDialog {
    private Dialog dialog;
    private ProgressBar progressBar;
    private TextView tvTitle;
    private TextView progressText;

    public CustomProgressDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_progress_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int widthInPixels = (int) (350 * displayMetrics.density + 0.5f);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(widthInPixels, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        progressBar = dialog.findViewById(R.id.progressBar);
        tvTitle = dialog.findViewById(R.id.tvTitle);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

}
