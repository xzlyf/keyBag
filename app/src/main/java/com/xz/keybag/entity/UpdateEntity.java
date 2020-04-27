package com.xz.keybag.entity;

/**
 * @author czr
 * @date 2020/4/27
 */
public class UpdateEntity {

    /**
     * code : 3
     * version : 1.2
     * msg : 1.新增重要更新 2.修复已知漏洞 3.解决安全问题
     * link : http://192.168.1.72:28080/keybag/kb.apk
     */

    private int code;
    private double version;
    private String msg;
    private String link;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
