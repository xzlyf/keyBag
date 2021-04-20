package com.xz.keybag.activity;

import android.content.ContentValues;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.MD5Util;
import com.xz.utils.RandomString;

import butterknife.BindView;

public class KeyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.old_pwd)
    EditText oldPwd;
    @BindView(R.id.old_layout)
    TextInputLayout oldLayout;
    @BindView(R.id.new_pwd)
    EditText newPwd;
    @BindView(R.id.new_layout)
    TextInputLayout newLayout;
    @BindView(R.id.new_pwd_repeat)
    EditText newPwdRepeat;
    @BindView(R.id.new_layout_repeat)
    TextInputLayout newLayoutRepeat;
    @BindView(R.id.input_layout)
    LinearLayout inputLayout;
    @BindView(R.id.submit)
    TextView submit;

    @Override
    public boolean homeAsUpEnabled() {
        return true;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_key;
    }

    @Override
    public void initData() {
        setTitle(R.string.string_16);

        submit.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        String pwd = newPwdRepeat.getText().toString().trim();
        if (!checkEmpty()) {
            return;
        }
        if (!TextUtils.equals(newPwd.getText().toString().trim(), pwd)) {
            newLayoutRepeat.setError(getString(R.string.string_17));
            return;
        }

        if (!MD5Util.getMD5(oldPwd.getText().toString().trim()).equals(Local.User.loginPwd)) {
            oldPwd.setText("");
            oldLayout.setError(getString(R.string.string_18));
            return;
        }

        String md5Pwd = MD5Util.getMD5(pwd);

        oldPwd.setText("");
        newPwd.setText("");
        newPwdRepeat.setText("");
        oldLayout.setError("");
        newLayout.setError("");
        newLayoutRepeat.setError("");


        //先清空数据表
        SqlManager.deleteAll(mContext, "dbase");

        ContentValues values = new ContentValues();
        values.put("p1", RandomString.getRandomString(16));
        values.put("p2", EOD.encrypt(md5Pwd, Local.SECRET_PWD));
        values.put("p3", RandomString.getRandomString(16, true));
        long i =SqlManager.insert(mContext, "dbase", values);
        if (i==-1){
            sToast(getString(R.string.string_19));
            return;
        }
        Local.User.loginPwd = md5Pwd;
        sToast(getString(R.string.string_20));

    }


    private boolean checkEmpty() {
        if (TextUtils.isEmpty(oldPwd.getText())) {
            oldLayout.setError(getString(R.string.string_21));
            return false;
        } else {
            oldLayout.setError("");
        }
        if (TextUtils.isEmpty(newPwd.getText())) {
            newLayout.setError(getString(R.string.string_21));
            return false;
        } else {
            newLayout.setError("");
        }
        if (TextUtils.isEmpty(newPwdRepeat.getText())) {
            newLayoutRepeat.setError(getString(R.string.string_21));
            return false;
        } else {
            newLayoutRepeat.setError("");
        }
        return true;

    }
}
