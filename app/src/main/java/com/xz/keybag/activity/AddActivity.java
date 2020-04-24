package com.xz.keybag.activity;

import android.content.ContentValues;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.constant.Local;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.widget.dialog.XOnClickListener;
import com.xz.widget.dialog.XzTipsDialog;

import butterknife.BindView;

public class AddActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.tv_back)
    ImageView tvBack;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.user)
    EditText user;
    @BindView(R.id.psw)
    EditText psw;
    @BindView(R.id.note)
    EditText note;
    @BindView(R.id.save)
    TextView save;
    private XzTipsDialog tipsDialog;

    @Override
    public boolean homeAsUpEnabled() {
        return false;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add;
    }

    @Override
    public void initData() {
        tvBack.setOnClickListener(this);
        save.setOnClickListener(this);
        name.setOnClickListener(this);
        user.setOnClickListener(this);
        psw.setOnClickListener(this);
        note.setOnClickListener(this);
        if (isNightMode()) {
            tvBack.setColorFilter(getResources().getColor(R.color.icons));
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_back:
                if (hasContent()) {

                    if (tipsDialog == null) {
                        tipsDialog = new XzTipsDialog.Builder(mContext)
                                .setContent(getString(R.string.string_9))
                                .setSubmitBtnBackground(0)
                                .setSubmitTextColor(Color.GRAY)
                                .setSubmitOnClickListener(getString(R.string.string_10), new XOnClickListener() {
                                    @Override
                                    public void onClick(int viewId, String s, int position) {
                                        finish();
                                    }
                                })
                                .setCancelBtnBackground(0)
                                .setCancelTextColor(Color.GRAY)
                                .setCancelOnclickListener(getString(R.string.string_11), new XOnClickListener() {
                                    @Override
                                    public void onClick(int viewId, String s, int position) {
                                    }
                                })
                                .create();
                    }

                    tipsDialog.show();


                } else {
                    finish();
                }

                break;
            case R.id.save:
                submit();
                break;
            case R.id.name:
            case R.id.user:
            case R.id.psw:
            case R.id.note:
                break;
        }
    }

    /**
     * 存储
     */
    private void submit() {
        String t1 = name.getText().toString().trim();
        String t2 = user.getText().toString().trim();
        String t3 = psw.getText().toString().trim();
        String t4 = note.getText().toString().trim();
        String t5 = String.valueOf(System.currentTimeMillis());

        if (!checkEmpty(t1, t2, t3, t5)) {
            sToast(getString(R.string.string_12));
            return;
        }

        ContentValues values = new ContentValues();
        values.put("t1", EOD.encrypt(t1, Local.secret));
        values.put("t2", EOD.encrypt(t2, Local.secret));
        values.put("t3", EOD.encrypt(t3, Local.secret));
        values.put("t4", EOD.encrypt(t4, Local.secret));
        values.put("t5", EOD.encrypt(t5, Local.secret));
        long i = SqlManager.insert(mContext, "common", values);//插入数据
        if (i == -1) {
            sToast(getString(R.string.string_13));
        } else {
            sToast(getString(R.string.string_14));
            finish();
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

    /**
     * 是否有内容
     *
     * @return true 有 false 无
     */
    private boolean hasContent() {
        if (!name.getText().toString().trim().equals("")) {
            return true;
        }
        if (!user.getText().toString().trim().equals("")) {
            return true;
        }
        if (!psw.getText().toString().trim().equals("")) {
            return true;
        }
        if (!note.getText().toString().trim().equals("")) {
            return true;
        }
        return false;

    }
}
