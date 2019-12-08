package com.xz.widget.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.xz.widget.utils.KeyBoardUtil;

/**
 * 输入完成后自动跳到下一个输入框,没有下一个就取消焦点
 * 通过设置长度，长度符合后自动执行跳到下一个输入框
 * 适用于ip地址输入等
 */
public class AutoFocusEditView extends EditText implements TextWatcher {
    private EditText lastEd = null;
    private EditText nextEd = null;
    private FinishEditListener mlistener;
    private int mLength = 2;//默认二，当输入到第三位时自动跳转到下个编辑框，没有下一个就取消焦点

    public AutoFocusEditView(Context context) {
        super(context);
        init(context);
    }

    public AutoFocusEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        addTextChangedListener(this);
        //设置最大输入长度
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLength + 1)});
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (start == mLength && after == 1) {
            this.clearFocus();
            if (nextEd != null) {
                nextEd.requestFocus();

            } else {
                //如果下一个为空就隐藏软键盘
                KeyBoardUtil.hideKeyBoard(this);
            }
        }
        if (start == 0 && after == 0) {
            this.clearFocus();
            if (lastEd != null) {
                lastEd.requestFocus();
            } else {
                //如果上一个为空就隐藏软键盘
                KeyBoardUtil.hideKeyBoard(this);
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mlistener != null) {
            mlistener.onFinishText(this, getText().toString());
        }
    }


    /**
     * 设置上一个的输入框
     *
     * @param editView
     */
    public void setLastEditView(EditText editView) {
        lastEd = editView;
    }

    /**
     * 设置下一个的输入框
     *
     * @param editView
     */
    public void setNextEditView(EditText editView) {
        nextEd = editView;
    }

    /**
     * 通过设置长度，长度符合后自动执行跳到下一个输入框
     *
     * @param length
     */
    public void setLength(int length) {
        this.mLength = length;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(length + 1)}); //设置最大输入长度

    }


    /**
     * 输入完成时返回
     *
     * @param listener
     */
    public void setFinishEditListener(FinishEditListener listener) {
        mlistener = listener;
    }

}
