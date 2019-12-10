package com.xz.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xz.xzwidget.R;

/**
 * 多功能输入框
 * 好看的输入框
 *
 * 未完成2019.12.10
 */
public class ExtorEditView extends ConstraintLayout  {
    private EditText etInput;
    private ImageView backSpace;
    private View line;
    private TextView autoHint;
    private LinearLayout countLayout;
    private TextView current;
    private TextView separate;
    private TextView total;


    public ExtorEditView(Context context) {
        this(context, null);
    }

    public ExtorEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtorEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.custom_extor_editview, this, true);
        //获取attr数据
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExtorEditView);
        String hint = typedArray.getString(R.styleable.ExtorEditView_hint);
        boolean showCount = typedArray.getBoolean(R.styleable.ExtorEditView_showCount, false);
        boolean showBackspace = typedArray.getBoolean(R.styleable.ExtorEditView_showBackspace, true);
        boolean showLine = typedArray.getBoolean(R.styleable.ExtorEditView_showLine, true);
        int total = typedArray.getInt(R.styleable.ExtorEditView_total, -1);//-1不限长度
        int maxLines = typedArray.getInt(R.styleable.ExtorEditView_maxLines, -1);//-1不限行数
        int maxLength = typedArray.getInt(R.styleable.ExtorEditView_maxLength, -1);//-1不限长度
        //释放资源
        typedArray.recycle();

        initView();
        initAnim();
    }

    private TranslateAnimation showTranslateAnimation;
    private TranslateAnimation hideTranslateAnimation;

    private void initAnim() {
        showTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f
                , Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        showTranslateAnimation.setDuration(300);
        showTranslateAnimation.setInterpolator(new DecelerateInterpolator());
        hideTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f
                , Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        hideTranslateAnimation.setDuration(300);
        hideTranslateAnimation.setInterpolator(new DecelerateInterpolator());

    }


    private void initView() {
        etInput = findViewById(R.id.et_input);
        backSpace = findViewById(R.id.back_space);
        line = findViewById(R.id.line);
        autoHint = findViewById(R.id.auto_hint);
        countLayout = findViewById(R.id.count_layout);
        current = findViewById(R.id.current);
        separate = findViewById(R.id.separate);
        total = findViewById(R.id.total);

        etInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showACC();
                } else {
                    hideACC();
                }
            }
        });
    }

    private void hideACC() {

        countLayout.startAnimation(hideTranslateAnimation);
        backSpace.startAnimation(hideTranslateAnimation);
        autoHint.startAnimation(hideTranslateAnimation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                countLayout.setVisibility(GONE);
                backSpace.setVisibility(GONE);
                autoHint.setVisibility(GONE);
            }
        },300);
    }

    private void showACC() {
        backSpace.setVisibility(VISIBLE);
        autoHint.setVisibility(VISIBLE);
        countLayout.setVisibility(VISIBLE);
        countLayout.startAnimation(showTranslateAnimation);
        autoHint.startAnimation(showTranslateAnimation);
        backSpace.startAnimation(showTranslateAnimation);
    }


}
