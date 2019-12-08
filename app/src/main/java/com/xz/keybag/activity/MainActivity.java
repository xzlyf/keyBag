package com.xz.keybag.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.adapter.KeyAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    @BindView(R.id.key_recycler)
    RecyclerView keyRecycler;
    private KeyAdapter keyAdapter;

    @Override
    public boolean homeAsUpEnabled() {
        return false;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {

        keyAdapter = new KeyAdapter(mContext);

    }

}
