package com.xz.keybag.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;

public class LoadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
    }

    @Override
    public boolean homeAsUpEnabled() {
        return false;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_load;
    }

    @Override
    public void initData() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
