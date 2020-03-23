package com.xz.keybag.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.KeyEntity;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.MD5Util;
import com.xz.utils.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.name_edit)
    ImageView nameEdit;
    @BindView(R.id.user)
    EditText user;
    @BindView(R.id.user_edit)
    ImageView userEdit;
    @BindView(R.id.pwd)
    EditText pwd;
    @BindView(R.id.pwd_edit)
    ImageView pwdEdit;
    @BindView(R.id.remark)
    EditText remark;
    @BindView(R.id.remark_edit)
    ImageView remarkEdit;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.submit)
    TextView submit;
    private KeyEntity entity;

    @Override
    public boolean homeAsUpEnabled() {
        return true;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_detail;
    }

    @Override
    public void initData() {
        if (getIntent() == null) {
            sToast(getString(R.string.string_22));
            return;
        }
        entity = (KeyEntity) getIntent().getSerializableExtra("model");
        name.setText(entity.getT1());
        user.setText(entity.getT2());
        pwd.setText(entity.getT3());
        remark.setText(entity.getT4());
        long time;
        try {
            time = Long.valueOf(entity.getT5());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            time = 0;
        }
        endTime.setText(TimeUtil.getSimMilliDate("yyyy年MM月dd日 hh:mm:ss",time));
        name.setOnFocusChangeListener(this);
        user.setOnFocusChangeListener(this);
        pwd.setOnFocusChangeListener(this);
        remark.setOnFocusChangeListener(this);
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.name:
                showEditorImg(hasFocus, nameEdit);
                break;
            case R.id.user:
                showEditorImg(hasFocus, userEdit);

                break;
            case R.id.pwd:
                showEditorImg(hasFocus, pwdEdit);

                break;
            case R.id.remark:
                showEditorImg(hasFocus, remarkEdit);

                break;
        }
    }

    private void showEditorImg(boolean b, ImageView view) {
        if (b) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    private void submit() {


        String t1 = name.getText().toString().trim();
        String t2 = user.getText().toString().trim();
        String t3 = pwd.getText().toString().trim();
        String t4 = remark.getText().toString().trim();
        String t5 = String.valueOf(System.currentTimeMillis());

        if (!checkEmpty(t1, t2, t3, t5)) {
            sToast(getString(R.string.string_12));
            return;
        }
        String eodT1 = EOD.encrypt(entity.getT1(), Local.secret);
        String eodT2 = EOD.encrypt(entity.getT2(), Local.secret);
        String eodT3 = EOD.encrypt(entity.getT3(), Local.secret);


        ContentValues values = new ContentValues();
        values.put("t1", EOD.encrypt(t1, Local.secret));
        values.put("t2", EOD.encrypt(t2, Local.secret));
        values.put("t3", EOD.encrypt(t3, Local.secret));
        values.put("t4", EOD.encrypt(t4, Local.secret));
        values.put("t5", EOD.encrypt(t5, Local.secret));
        int i = SqlManager.update(mContext, "common", values, "t1 = ? and t2 = ? and t3 = ? ", new String[]{eodT1, eodT2, eodT3});//更新
        if (i == 0) {
            sToast(getString(R.string.string_23));
        } else {
            sToast(getString(R.string.string_24));
        }

    }

    /**
     * 检查是否存在空
     *
     * @param arg
     * @return
     */
    private boolean checkEmpty(String... arg) {
        for (String s : arg) {
            if (s == null || s.equals("")) {
                return false;
            }
        }
        return true;
    }

}
