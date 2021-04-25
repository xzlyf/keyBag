package com.xz.keybag.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.xz.keybag.R;


public class LoadingView extends LinearLayout {
	private View mShadeView;//阴影
	private ShapeView mShapeView;//形状
	private int mTranslationDistance = 0;
	// 动画执行的时间
	private final long ANIMATOR_DURATION = 350;
	private boolean mIsStopAnimation = false;

	public LoadingView(Context context) {
		this(context, null);
	}

	public LoadingView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mTranslationDistance = dp2x(80);
		initLayout();
	}

	private int dp2x(int dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
	}

	private void initLayout() {
		// 1. 记载写好的 ui_loading_view
    /*    //1.1实例化view
        View view = inflate(getContext(), R.layout.ui_loading_view, null);
        // 1.2 添加到该View
        addView(view);*/
		//将ui_loading_view布局加载到改布局中
		inflate(getContext(), R.layout.view_loading, this);
		mShadeView = findViewById(R.id.shadow_view);
		mShapeView = findViewById(R.id.shape_view);
		post(new Runnable() {
			@Override
			public void run() {
				// onResume 之后View绘制流程执行完毕之后（View的绘制流程源码分析那一章）
				startFallAnimator();
			}
		});
		// onCreate() 方法中执行 ，布局文件解析 反射创建实例
		//startFallAnimator();
	}

	/**
	 * 开始下落动画
	 */
	private void startFallAnimator() {
		if (mIsStopAnimation) {
			return;
		}
		//设置mShapeView的位移动画
		ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY", 0, mTranslationDistance);
		//设置mShadeView的透明度变化
		ObjectAnimator scaleAnimation = ObjectAnimator.ofFloat(mShadeView, "scaleX", 1f, 0.3f);
		AnimatorSet animationSet = new AnimatorSet();
		animationSet.setDuration(ANIMATOR_DURATION);//设置时长
		animationSet.playTogether(translationAnimator, scaleAnimation);//两个动画一起播放
		//有序播放
		//animationSet.playSequentially(translationAnimator,scaleAnimation);
		animationSet.setInterpolator(new AccelerateInterpolator());//加速
		animationSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				//下落完毕后，上抛
				startUpAnimation();
			}
		});
		animationSet.start();
	}

	/**
	 * 开始上抛
	 */
	private void startUpAnimation() {
		if (mIsStopAnimation) {
			return;
		}
		Log.e("TAG", "startUpAnimation");
		//设置mShapeView的位移动画
		ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY", mTranslationDistance, 0);
		//设置mShadeView的透明度变化
		ObjectAnimator scaleAnimation = ObjectAnimator.ofFloat(mShadeView, "scaleX", 0.3f, 1f);
		AnimatorSet animationSet = new AnimatorSet();
		animationSet.setDuration(ANIMATOR_DURATION);//设置时长
		animationSet.playTogether(translationAnimator, scaleAnimation);//两个动画一起播放
		//有序播放
		//animationSet.playSequentially(translationAnimator,scaleAnimation);
		animationSet.setInterpolator(new DecelerateInterpolator());//减速
		animationSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				//上抛完毕后，下落
				startFallAnimator();
			}

			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				//旋转
				startRotationAnimator();
				//改变形状
				mShapeView.exchange();
			}
		});
		animationSet.start();
	}

	/**
	 * 旋转动画
	 */
	private void startRotationAnimator() {
		ObjectAnimator rotationAnimator = null;
		switch (mShapeView.getCurrentShape()) {
			case Circle:
			case Square://旋转180度
				rotationAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, 180);
				break;
			case Triangle:
				rotationAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, -120);
				break;
		}
		rotationAnimator.setDuration(ANIMATOR_DURATION);
		rotationAnimator.setInterpolator(new DecelerateInterpolator());
		rotationAnimator.start();
	}
	//当后台数据返回的时候我们要把当前页面，设置成 gone （隐藏），只是用代码设置为了
	// 隐藏，但是动画View的内存还在跑

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(INVISIBLE);// 不要再去排放和计算，少走一些系统的源码
		//清理所有动画
		mShapeView.clearAnimation();
		mShadeView.clearAnimation();
		//移除父布局
		ViewGroup parent = (ViewGroup) getParent();

		if (parent != null) {
			parent.removeView(this);//父布局中移除当前view
			removeAllViews();//移除自己的所有view
		}
		//此时上诉步骤完成，内存中有所以还是在运行
		mIsStopAnimation = true;
	}
}
