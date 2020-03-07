package com.xz.keybag.custom;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xz.base.BaseDialog;
import com.xz.keybag.R;

public class SecretInputDialog extends BaseDialog {
    private EditText etInput;
    private Button tvSubmit;
    private Button tvCancel;
    private XOnClickListener onSubmitListener;
    private XOnClickListener onCancelListener;

    public SecretInputDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_secret_key;
    }

    @Override
    protected void initData() {
        initView();
    }

    private void initView() {
        etInput = findViewById(R.id.et_input);
        tvSubmit = findViewById(R.id.tv_submit);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitListener.onClick(etInput.getText().toString().trim(), v);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelListener.onClick(etInput.getText().toString().trim(), v);
            }
        });
    }

    public void setOnSubmitListener(XOnClickListener listener) {
        onSubmitListener = listener;
    }

    public void setOnCancelListener(XOnClickListener listener) {
        onCancelListener = listener;
    }
}
