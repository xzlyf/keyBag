package com.xz.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.xz.widget.XType;
import com.xz.widget.listener.OnSearchOnClickListener;
import com.xz.xzwidget.R;

/**
 * 使用时必须将控件宽度设置为MATCH_PARENT或部分布局0dp自适应最大宽度
 * 这样做可以确保控件有位置展开
 */
public class SearchEditView extends ConstraintLayout {
    private ImageView iconSearch;
    private EditText inputSearch;
    private ImageView closeSearch;
    private ConstraintLayout constantLayout;
    private TransitionSet mSet;
    private LayoutParams layoutParams;


    private int state = 0;//0 收起状态， 1展开状态
    private OnSearchOnClickListener mListener;

    public SearchEditView(Context context) {
        this(context, null);
    }

    public SearchEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.custom_search_editview, this, true);

        //获取attr数据
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchEditView);
        int gravity = typedArray.getInt(R.styleable.SearchEditView_gravity, -1);
        //释放资源
        typedArray.recycle();

        initView();
        initData();
        setGravity(gravity);
    }

    private void initView() {
        iconSearch = findViewById(R.id.icon_search);
        inputSearch = findViewById(R.id.input_search);
        closeSearch = findViewById(R.id.close_search);
        constantLayout = findViewById(R.id.constant_layout);
        iconSearch.setOnClickListener(onClick);
        closeSearch.setOnClickListener(onClick);
    }

    private void initData() {
        layoutParams = (ConstraintLayout.LayoutParams) constantLayout.getLayoutParams();
        inputSearch.setVisibility(GONE);
        closeSearch.setVisibility(GONE);


    }

    /**
     * 点击事件处理
     */
    private View.OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_search) {
                if (state == 0) {
                    showSearch();
                }
            }
            if (v.getId() == R.id.close_search) {
                if (state == 1) {
                    hideSearch();
                }
                if (mListener != null) {
                    mListener.onClick(inputSearch.getText().toString());
                }


            }
        }
    };


    /**
     * 搜索框展开动画
     */
    public void showSearch() {
        state = 1;
        closeSearch.setVisibility(VISIBLE);
        inputSearch.setVisibility(VISIBLE);
        inputSearch.requestFocus();
        //layoutParams.rightToLeft = ConstraintLayout.layoutParams.PARENT_ID;
        //layoutParams.leftToRight = ConstraintLayout.layoutParams.PARENT_ID;;
        //layoutParams.width = 0;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.setMargins(0, 0, 0, 0);
        constantLayout.setLayoutParams(layoutParams);
        //设置动画
        beginDelayedTransition(constantLayout);
    }

    public void hideSearch() {
        state = 0;
        closeSearch.setVisibility(GONE);
        inputSearch.setVisibility(GONE);
        inputSearch.clearFocus();
        inputSearch.setText("");
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(0, 0, 0, 0);
        constantLayout.setLayoutParams(layoutParams);
        //设置动画
        beginDelayedTransition(constantLayout);
    }

    private void beginDelayedTransition(ViewGroup view) {
        mSet = new AutoTransition();
        //设置动画持续时间
        mSet.setDuration(300);
        mSet.setInterpolator(new DecelerateInterpolator(0.8f));
        // 开始播放
        TransitionManager.beginDelayedTransition(view, mSet);
    }

    /**
     * dp转px
     *
     * @param dpVale
     * @return
     */
    private int dip2px(float dpVale) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }


    /**
     * 设置layout显示位置
     * gravity常量位于XType中
     *
     * @param gravity
     */
    public void setGravity(int gravity) {
        switch (gravity) {
            case XType.SEARCHET_LEFT:
                layoutParams.leftToLeft = LayoutParams.PARENT_ID;
                break;
            case XType.SEARCHET_CENTER:
                layoutParams.rightToRight = LayoutParams.PARENT_ID;
                layoutParams.leftToLeft = LayoutParams.PARENT_ID;
                break;
            case XType.SEARCHET_RIGHT:
                layoutParams.rightToRight = LayoutParams.PARENT_ID;
                break;
            default:
                return;
        }

        constantLayout.setLayoutParams(layoutParams);

    }

    /**
     * 获取文本
     *
     * @return
     */
    public String getText() {
        return inputSearch.getText().toString();
    }

    /**
     * 关闭按钮监听
     * 关闭后会清除文本输入
     *
     * @param listener
     */
    public void setOnSearchCloseOnClickListener(OnSearchOnClickListener listener) {
        mListener = listener;
    }

    /**
     * 输入框文本监听
     *
     * @param textWatcher
     */
    public void addTextChangeListener(TextWatcher textWatcher) {

        inputSearch.addTextChangedListener(textWatcher);
    }

    /**
     * 检测是否触摸控件外部
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isTouchPointInView(int x, int y) {

        int[] location = new int[2];
        getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + getMeasuredWidth();
        int bottom = top + getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }
}
