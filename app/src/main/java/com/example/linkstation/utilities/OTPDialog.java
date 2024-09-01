package com.example.linkstation.utilities;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.linkstation.R;

public class OTPDialog extends Dialog {

    private final Context context;
    private TextView tvResendOtp;
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button btnVerify;
    private OTPDialogListener listener;

    public OTPDialog(@NonNull Context context, OTPDialogListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.otp_verification_dialog);  // replace with your actual layout file name

        initializeViews();
        setListeners();
    }

    private void initializeViews() {
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        btnVerify = findViewById(R.id.btnVerify);
        tvResendOtp = findViewById(R.id.tvResendOtp);
    }

    private void setListeners() {
        // Add text change listener to move to the next OTP field
        otp1.addTextChangedListener(new OTPTextWatcher(otp1));
        otp2.addTextChangedListener(new OTPTextWatcher(otp2));
        otp3.addTextChangedListener(new OTPTextWatcher(otp3));
        otp4.addTextChangedListener(new OTPTextWatcher(otp4));
        otp5.addTextChangedListener(new OTPTextWatcher(otp5));
        otp6.addTextChangedListener(new OTPTextWatcher(otp6));

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpCode = otp1.getText().toString().trim() +
                        otp2.getText().toString().trim() +
                        otp3.getText().toString().trim() +
                        otp4.getText().toString().trim() +
                        otp5.getText().toString().trim() +
                        otp6.getText().toString().trim();

                listener.onOTPEntered(otpCode);
                dismiss();
            }
        });

        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onResendOtp();
            }
        });
    }

    private class OTPTextWatcher implements TextWatcher {
        private View view;

        private OTPTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            if (view.getId() == R.id.otp1) {
                if (text.length() == 1) {
                    otp2.requestFocus();
                }
            } else if (view.getId() == R.id.otp2) {
                if (text.length() == 1) {
                    otp3.requestFocus();
                } else if (text.isEmpty()) {
                    otp1.requestFocus();
                }
            } else if (view.getId() == R.id.otp3) {
                if (text.length() == 1) {
                    otp4.requestFocus();
                } else if (text.isEmpty()) {
                    otp2.requestFocus();
                }
            } else if (view.getId() == R.id.otp4) {
                if (text.length() == 1) {
                    otp5.requestFocus();
                } else if (text.isEmpty()) {
                    otp3.requestFocus();
                }
            } else if (view.getId() == R.id.otp5) {
                if (text.length() == 1) {
                    otp6.requestFocus();
                } else if (text.isEmpty()) {
                    otp4.requestFocus();
                }
            } else if (view.getId() == R.id.otp6) {
                if (text.isEmpty()) {
                    otp5.requestFocus();
                }
            }

        }
    }

    public interface OTPDialogListener {
        void onOTPEntered(String otp);
        void onResendOtp();
    }
}
