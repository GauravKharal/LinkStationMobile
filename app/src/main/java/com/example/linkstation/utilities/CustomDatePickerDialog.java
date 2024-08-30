package com.example.linkstation.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.linkstation.R;

import java.util.Date;

public class CustomDatePickerDialog {
    private Dialog dialog;
    private DatePicker datePicker;
    private Button btnTick;
    private Button btnCross;

    public CustomDatePickerDialog(Context context) {
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.datepicker_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        datePicker = dialog.findViewById(R.id.datePicker);
        btnTick = dialog.findViewById(R.id.btnTick);
        btnCross = dialog.findViewById(R.id.btnCross);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        btnCross.setOnClickListener(v -> dialog.dismiss());
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public CustomDatePickerDialog setDate(int year, int month, int day) {
        datePicker.updateDate(year, month, day);
        return this;
    }

    public int getYear() {
        return datePicker.getYear();
    }

    public int getMonth() {
        return datePicker.getMonth();
    }

    public int getDay() {
        return datePicker.getDayOfMonth();
    }

    public CustomDatePickerDialog setPositiveButton(View.OnClickListener listener) {
        btnTick.setOnClickListener(v -> {
            listener.onClick(v);
            dialog.dismiss();
        });
        return this;
    }


}