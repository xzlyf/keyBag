package com.xz.keybag.base;

import android.content.DialogInterface;

public interface BaseView {
    void showLoading(String text);

	void showLoading(String tips, boolean cancel, DialogInterface.OnCancelListener cancelListener);

	void disLoading();
    void sToast(String text);
    void lToast(String text);
    void sDialog(String title, String msg);
    void dDialog();
}
