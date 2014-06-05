package com.changlianxi.modle;

import android.graphics.Bitmap;

/**
 * 联系人对象信息
 * 
 * @author teeker_bin
 * 
 */
public class ContactModle {

    private String name = "";// 联系人姓名
    private String sort_key = "";// 用来排序的关键字 搜索时使用
    private String key_pinyin_fir = "";// 名字首字母 搜索时使用
    private String num = "";
    private Bitmap bmp = null;
    private Long photoid = 0l;
    private Long contactid = 0l;
    private boolean checked = false;
    private String tag = "";

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Long getContactid() {
        return contactid;
    }

    public void setContactid(Long contactid) {
        this.contactid = contactid;
    }

    public Long getPhotoid() {
        return photoid;
    }

    public void setPhotoid(Long photoid) {
        this.photoid = photoid;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public String getSort_key() {
        return sort_key;
    }

    public void setSort_key(String sort_key) {
        this.sort_key = sort_key;
    }

    public String getKey_pinyin_fir() {
        return key_pinyin_fir;
    }

    public void setKey_pinyin_fir(String key_pinyin_fir) {
        this.key_pinyin_fir = key_pinyin_fir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
