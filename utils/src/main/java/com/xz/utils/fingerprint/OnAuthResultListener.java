package com.xz.utils.fingerprint;

public interface OnAuthResultListener {
    /**
     * 认证成功
     */
    void onSuccess();

    /**
     * 帮助信息
     *
     * @param msg
     */
    void onHelper(String msg);

    /**
     * 认证失败，返回原因
     *
     * @param msg
     */
    void onFailed(String msg);

    /**
     * 识别到指纹，但无法验证
     * @param msg
     */
    void onAuthenticationFailed(String msg);

    /**
     * 设备不支持
     */
    void onDeviceNotSupport();
}