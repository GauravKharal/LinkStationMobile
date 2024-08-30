package com.example.linkstation.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.linkstation.R;

public class CustomDialog {
    private Dialog dialog;
    private TextView tvMessage;
    private TextView tvTitle;
    private Button btnYes;
    private Button btnNo;

    public CustomDialog(Context context) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(true);


        tvMessage = dialog.findViewById(R.id.tvMessage);
        tvTitle = dialog.findViewById(R.id.tvTitle);
        btnYes = dialog.findViewById(R.id.btnYes);
        btnNo = dialog.findViewById(R.id.btnNo);

        btnNo.setOnClickListener(v -> dialog.dismiss());

        setDialogDimensions(context, 350);
    }

    private void setDialogDimensions(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthInPixels = (int) (width * displayMetrics.density + 0.5f);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(widthInPixels, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public CustomDialog setMessage(String message) {
        tvMessage.setText(message);
        return this;
    }

    public CustomDialog setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public CustomDialog setPositiveButton(boolean show, String text, View.OnClickListener listener) {
        if (show) {
            btnYes.setVisibility(View.VISIBLE);
            btnYes.setText(text);
            btnYes.setOnClickListener(v -> {
                listener.onClick(v);
                dialog.dismiss();
            });
        } else {
            btnYes.setVisibility(View.GONE);
        }
        return this;
    }

    public CustomDialog setNegativeButton(boolean show, String text, View.OnClickListener listener) {
        if (show) {
            btnNo.setVisibility(View.VISIBLE);
            btnNo.setText(text);
            btnNo.setOnClickListener(v -> {
                listener.onClick(v);
                dialog.dismiss();
            });
        } else {
            btnNo.setVisibility(View.GONE);
        }
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
